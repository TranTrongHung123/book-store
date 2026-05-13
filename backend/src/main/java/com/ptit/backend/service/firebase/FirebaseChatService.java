package com.ptit.backend.service.firebase;

import com.ptit.backend.dto.request.support.StaffChatStatusRequest;
import com.ptit.backend.dto.request.support.SupportMessageRequest;
import com.ptit.backend.dto.request.support.SupportOpenRequest;
import com.ptit.backend.dto.request.support.SupportTagsRequest;
import com.ptit.backend.dto.response.FirebaseCustomTokenResponse;
import com.ptit.backend.dto.response.support.StaffDashboardSummaryResponse;
import com.ptit.backend.dto.response.support.SupportConversationResponse;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;

public interface FirebaseChatService {

    FirebaseCustomTokenResponse createCustomToken(Authentication authentication);

    SupportConversationResponse openConversation(Authentication authentication, SupportOpenRequest request);

    SupportConversationResponse claimWaitingConversation(Authentication authentication);

    SupportConversationResponse closeConversation(Authentication authentication, String conversationId);

    StaffDashboardSummaryResponse getStaffDashboardSummary();

    List<Map<String, Object>> getStaffConversations(Authentication authentication);

    List<Map<String, Object>> getStaffConversationMessages(Authentication authentication, String conversationId);

    SupportConversationResponse sendStaffMessage(Authentication authentication, String conversationId, SupportMessageRequest request);

    SupportConversationResponse updateStaffConversationTags(Authentication authentication, String conversationId, SupportTagsRequest request);

    SupportConversationResponse markStaffConversationRead(Authentication authentication, String conversationId);

    SupportConversationResponse upsertStaffChatStatus(Authentication authentication, StaffChatStatusRequest request);
}
