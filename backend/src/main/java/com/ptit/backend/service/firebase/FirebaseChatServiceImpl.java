package com.ptit.backend.service.firebase;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.ptit.backend.dto.request.support.SupportOpenRequest;
import com.ptit.backend.dto.response.FirebaseCustomTokenResponse;
import com.ptit.backend.dto.response.support.StaffDashboardSummaryResponse;
import com.ptit.backend.dto.response.support.SupportConversationResponse;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.OrderRepository;
import com.ptit.backend.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FirebaseChatServiceImpl implements FirebaseChatService {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_STAFF = "STAFF";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final Duration STAFF_STALE_THRESHOLD = Duration.ofMinutes(2);

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public FirebaseCustomTokenResponse createCustomToken(Authentication authentication) {
        User user = resolveUser(authentication);
        String uid = buildFirebaseUid(user.getUserId());
        String primaryRole = normalizePrimaryRole(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("appUserId", user.getUserId());
        claims.put("primaryRole", primaryRole);
        claims.put("displayName", StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getUsername());

        try {
            String token = FirebaseAuth.getInstance().createCustomToken(uid, claims);
            return FirebaseCustomTokenResponse.builder()
                    .token(token)
                    .uid(uid)
                    .primaryRole(primaryRole)
                    .userId(user.getUserId())
                    .fullName(StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getUsername())
                    .build();
        } catch (FirebaseAuthException exception) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Khong tao duoc Firebase custom token");
        }
    }

    @Override
    public SupportConversationResponse openConversation(Authentication authentication, SupportOpenRequest request) {
        User user = resolveUser(authentication);
        if (!ROLE_USER.equals(normalizePrimaryRole(user))) {
            throw new AppException(ErrorCode.FORBIDDEN, "Chi khach hang moi duoc mo hoi thoai ho tro");
        }

        Firestore db = FirestoreClient.getFirestore();
        String userUid = buildFirebaseUid(user.getUserId());
        Timestamp now = Timestamp.now();
        String safeMessage = request.getMessage().trim();

        try {
            QuerySnapshot existingSnapshot = db.collection("conversations")
                    .whereEqualTo("userUid", userUid)
                    .whereIn("status", List.of("WAITING", "ACTIVE"))
                    .get()
                    .get();

            if (!existingSnapshot.isEmpty()) {
                QueryDocumentSnapshot existing = existingSnapshot.getDocuments().get(0);
                DocumentReference conversationRef = existing.getReference();
                appendMessage(db, conversationRef, userUid, ROLE_USER, displayName(user), safeMessage, now);
                conversationRef.update(Map.of(
                        "lastMessage", safeMessage,
                        "lastMessageAt", now,
                        "updatedAt", now,
                        "unreadByStaff", FieldValue.increment(1)
                ));
                return mapConversationResponse(existing.getId(), existing.getData(), "Đã mở lại cuộc trò chuyện đang tồn tại.");
            }

            Optional<StaffCandidate> candidateOptional = findAvailableStaff(db);
            String conversationId = UUID.randomUUID().toString();
            DocumentReference conversationRef = db.collection("conversations").document(conversationId);

            Map<String, Object> conversation = new HashMap<>();
            conversation.put("userUid", userUid);
            conversation.put("userId", user.getUserId());
            conversation.put("userName", displayName(user));
            conversation.put("staffUid", null);
            conversation.put("staffId", null);
            conversation.put("staffName", null);
            conversation.put("status", "WAITING");
            conversation.put("createdAt", now);
            conversation.put("updatedAt", now);
            conversation.put("lastMessage", safeMessage);
            conversation.put("lastMessageAt", now);
            conversation.put("unreadByUser", 0L);
            conversation.put("unreadByStaff", 1L);

            if (candidateOptional.isPresent()) {
                StaffCandidate candidate = candidateOptional.get();
                conversation.put("staffUid", candidate.staffUid());
                conversation.put("staffId", candidate.staffId());
                conversation.put("staffName", candidate.staffName());
                conversation.put("status", "ACTIVE");

                conversationRef.set(conversation).get();
                appendMessage(db, conversationRef, userUid, ROLE_USER, displayName(user), safeMessage, now);
                candidate.reference().update(Map.of(
                        "currentLoad", candidate.currentLoad() + 1,
                        "lastAssignedAt", now,
                        "lastSeenAt", now
                )).get();

                return SupportConversationResponse.builder()
                        .conversationId(conversationId)
                        .status("ACTIVE")
                        .staffUid(candidate.staffUid())
                        .staffId(candidate.staffId())
                        .staffName(candidate.staffName())
                        .message("Đã kết nối với nhân viên hỗ trợ.")
                        .build();
            }

            conversationRef.set(conversation).get();
            appendMessage(db, conversationRef, userUid, ROLE_USER, displayName(user), safeMessage, now);
            return SupportConversationResponse.builder()
                    .conversationId(conversationId)
                    .status("WAITING")
                    .message("Hiện chưa có nhân viên rảnh. Yêu cầu của bạn đã được xếp hàng chờ.")
                    .build();
        } catch (Exception exception) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Khong the mo hoi thoai ho tro");
        }
    }


    @Override
    public SupportConversationResponse claimWaitingConversation(Authentication authentication) {
        User actor = resolveUser(authentication);
        String primaryRole = normalizePrimaryRole(actor);
        if (!(ROLE_STAFF.equals(primaryRole) || ROLE_MANAGER.equals(primaryRole) || ROLE_ADMIN.equals(primaryRole))) {
            throw new AppException(ErrorCode.FORBIDDEN, "Chi nhan vien moi duoc nhan hoi thoai cho");
        }

        Firestore db = FirestoreClient.getFirestore();
        String staffUid = buildFirebaseUid(actor.getUserId());
        Timestamp now = Timestamp.now();
        Instant staleAt = Instant.now().minus(STAFF_STALE_THRESHOLD);

        try {
            return db.runTransaction(transaction -> {
                DocumentReference staffStatusRef = db.collection("staff_status").document(staffUid);
                DocumentSnapshot staffStatus = transaction.get(staffStatusRef).get();
                if (!staffStatus.exists()) {
                    return SupportConversationResponse.builder()
                            .status("IDLE")
                            .message("Nhan vien chua bat che do nhan chat tren dashboard.")
                            .build();
                }

                boolean acceptingChats = Boolean.TRUE.equals(staffStatus.getBoolean("acceptingChats"));
                long currentLoad = numberValue(staffStatus.getLong("currentLoad"));
                long maxLoad = Math.max(1, numberValue(staffStatus.getLong("maxLoad"), 3));
                Timestamp lastSeenAt = staffStatus.getTimestamp("lastSeenAt");
                Instant staffLastSeen = lastSeenAt != null ? lastSeenAt.toDate().toInstant() : Instant.EPOCH;
                if (!acceptingChats || currentLoad >= maxLoad || staffLastSeen.isBefore(staleAt)) {
                    return SupportConversationResponse.builder()
                            .status("IDLE")
                            .message("Nhan vien hien khong san sang nhan them hoi thoai.")
                            .build();
                }

                QuerySnapshot waitingSnapshot = transaction.get(
                        db.collection("conversations")
                                .whereEqualTo("status", "WAITING")
                                .orderBy("createdAt")
                                .limit(1)
                ).get();

                if (waitingSnapshot.isEmpty()) {
                    transaction.update(staffStatusRef, Map.of("lastSeenAt", now));
                    return SupportConversationResponse.builder()
                            .status("IDLE")
                            .message("Khong con hoi thoai nao dang cho.")
                            .build();
                }

                QueryDocumentSnapshot waitingConversation = waitingSnapshot.getDocuments().get(0);
                DocumentReference conversationRef = waitingConversation.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("staffUid", staffUid);
                updates.put("staffId", actor.getUserId());
                updates.put("staffName", displayName(actor));
                updates.put("status", "ACTIVE");
                updates.put("updatedAt", now);
                transaction.update(conversationRef, updates);

                Map<String, Object> staffUpdates = new HashMap<>();
                staffUpdates.put("currentLoad", currentLoad + 1);
                staffUpdates.put("lastAssignedAt", now);
                staffUpdates.put("lastSeenAt", now);
                transaction.update(staffStatusRef, staffUpdates);

                return SupportConversationResponse.builder()
                        .conversationId(waitingConversation.getId())
                        .status("ACTIVE")
                        .staffUid(staffUid)
                        .staffId(actor.getUserId())
                        .staffName(displayName(actor))
                        .message("Da nhan mot hoi thoai dang cho.")
                        .build();
            }).get();
        } catch (AppException exception) {
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AppException(
                    ErrorCode.UNCATEGORIZED_EXCEPTION,
                    "Khong the nhan hoi thoai dang cho: " + exception.getMessage()
            );
        }
    }

    @Override
    public SupportConversationResponse closeConversation(Authentication authentication, String conversationId) {
        User actor = resolveUser(authentication);
        String primaryRole = normalizePrimaryRole(actor);
        if (!(ROLE_STAFF.equals(primaryRole) || ROLE_MANAGER.equals(primaryRole) || ROLE_ADMIN.equals(primaryRole))) {
            throw new AppException(ErrorCode.FORBIDDEN, "Chi nhan vien moi duoc dong hoi thoai");
        }

        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference conversationRef = db.collection("conversations").document(conversationId);
            DocumentSnapshot snapshot = conversationRef.get().get();
            if (!snapshot.exists()) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Khong tim thay hoi thoai tren Firebase");
            }

            String assignedStaffUid = snapshot.getString("staffUid");
            if (StringUtils.hasText(assignedStaffUid) && !assignedStaffUid.equals(buildFirebaseUid(actor.getUserId())) && !ROLE_ADMIN.equals(primaryRole)) {
                throw new AppException(ErrorCode.FORBIDDEN, "Ban khong phu trach hoi thoai nay");
            }

            Timestamp now = Timestamp.now();
            conversationRef.update(Map.of(
                    "status", "CLOSED",
                    "updatedAt", now,
                    "closedAt", now
            )).get();

            if (StringUtils.hasText(assignedStaffUid)) {
                DocumentReference staffStatusRef = db.collection("staff_status").document(assignedStaffUid);
                DocumentSnapshot staffStatus = staffStatusRef.get().get();
                long currentLoad = numberValue(staffStatus.getLong("currentLoad"));
                long nextLoad = Math.max(0, currentLoad - 1);
                staffStatusRef.update(Map.of(
                        "currentLoad", nextLoad,
                        "lastSeenAt", now
                )).get();
            }

            return SupportConversationResponse.builder()
                    .conversationId(conversationId)
                    .status("CLOSED")
                    .message("Đã đóng hội thoại hỗ trợ.")
                    .build();
        } catch (AppException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Khong the dong hoi thoai ho tro");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StaffDashboardSummaryResponse getStaffDashboardSummary() {
        return StaffDashboardSummaryResponse.builder()
                .totalBooks(bookRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByOrderStatusContainingIgnoreCase("cho"))
                .shippingOrders(orderRepository.countByOrderStatusContainingIgnoreCase("giao"))
                .totalCustomers(userRepository.countByRoleRoleNameIgnoreCase("USER"))
                .build();
    }

    private Optional<StaffCandidate> findAvailableStaff(Firestore db) throws Exception {
        QuerySnapshot snapshot = db.collection("staff_status")
                .whereEqualTo("acceptingChats", true)
                .get()
                .get();

        List<StaffCandidate> candidates = new ArrayList<>();
        Instant staleAt = Instant.now().minus(STAFF_STALE_THRESHOLD);

        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            Boolean acceptingChats = document.getBoolean("acceptingChats");
            long currentLoad = numberValue(document.getLong("currentLoad"));
            long maxLoad = Math.max(1, numberValue(document.getLong("maxLoad"), 3));
            Timestamp lastSeenAt = document.getTimestamp("lastSeenAt");
            Instant staffLastSeen = lastSeenAt != null ? lastSeenAt.toDate().toInstant() : Instant.EPOCH;
            if (!Boolean.TRUE.equals(acceptingChats) || currentLoad >= maxLoad || staffLastSeen.isBefore(staleAt)) {
                continue;
            }

            candidates.add(new StaffCandidate(
                    document.getReference(),
                    document.getId(),
                    document.getLong("staffId"),
                    safeString(document.getString("staffName")),
                    currentLoad,
                    maxLoad,
                    document.getTimestamp("lastAssignedAt")
            ));
        }

        return candidates.stream()
                .sorted(Comparator
                        .comparingLong(StaffCandidate::currentLoad)
                        .thenComparing(candidate -> candidate.lastAssignedAt() == null ? 0L : candidate.lastAssignedAt().getSeconds())
                        .thenComparing(StaffCandidate::staffName)
                )
                .findFirst();
    }

    private void appendMessage(
            Firestore db,
            DocumentReference conversationRef,
            String senderUid,
            String senderRole,
            String senderName,
            String content,
            Timestamp now
    ) throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("senderUid", senderUid);
        message.put("senderRole", senderRole);
        message.put("senderName", senderName);
        message.put("content", content);
        message.put("createdAt", now);
        message.put("read", false);

        CollectionReference messagesRef = db.collection("conversations")
                .document(conversationRef.getId())
                .collection("messages");
        messagesRef.add(message).get();
    }

    private User resolveUser(Authentication authentication) {
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private SupportConversationResponse mapConversationResponse(String conversationId, Map<String, Object> data, String message) {
        return SupportConversationResponse.builder()
                .conversationId(conversationId)
                .status(safeString(data.get("status")))
                .staffUid(safeString(data.get("staffUid")))
                .staffId(data.get("staffId") instanceof Number number ? number.longValue() : null)
                .staffName(safeString(data.get("staffName")))
                .message(message)
                .build();
    }

    private String buildFirebaseUid(Long userId) {
        return "app_" + userId;
    }

    private String normalizePrimaryRole(User user) {
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : ROLE_USER;
        if (!StringUtils.hasText(roleName)) {
            return ROLE_USER;
        }
        String normalized = roleName.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }

    private String displayName(User user) {
        return StringUtils.hasText(user.getFullName()) ? user.getFullName() : user.getUsername();
    }

    private static long numberValue(Long value) {
        return value == null ? 0L : value;
    }

    private static long numberValue(Long value, long fallback) {
        return value == null ? fallback : value;
    }

    private static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private record StaffCandidate(
            DocumentReference reference,
            String staffUid,
            Long staffId,
            String staffName,
            long currentLoad,
            long maxLoad,
            Timestamp lastAssignedAt
    ) {}
}
