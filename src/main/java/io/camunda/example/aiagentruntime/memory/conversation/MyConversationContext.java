package io.camunda.example.aiagentruntime.memory.conversation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationContext;
import java.util.UUID;

/**
 * Storage cursor for the custom conversation store. The {@code conversationId} identifies the
 * logical conversation (string form of the {@link MyConversation} row's UUID id, per the {@link
 * ConversationContext#conversationId()} contract); {@code recordId} is the head of the turn chain
 * ({@link MyConversationTurn} id). Loading walks from {@code recordId} back to the root via {@code
 * parent_id}.
 */
@JsonTypeName("my-conversation")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record MyConversationContext(String conversationId, UUID recordId)
    implements ConversationContext {}
