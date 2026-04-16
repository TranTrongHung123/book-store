package com.ptit.backend.service.firebase;

import com.ptit.backend.dto.request.support.SupportOpenRequest;
import com.ptit.backend.dto.response.FirebaseCustomTokenResponse;
import com.ptit.backend.dto.response.support.StaffDashboardSummaryResponse;
import com.ptit.backend.dto.response.support.SupportConversationResponse;
import org.springframework.security.core.Authentication;

public interface FirebaseChatService {

    FirebaseCustomTokenResponse createCustomToken(Authentication authentication);

    SupportConversationResponse openConversation(Authentication authentication, SupportOpenRequest request);

    SupportConversationResponse claimWaitingConversation(Authentication authentication);

    SupportConversationResponse closeConversation(Authentication authentication, String conversationId);

    StaffDashboardSummaryResponse getStaffDashboardSummary();
}
