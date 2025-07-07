package io.camunda.example.aiagentruntime.memory.conversation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.camunda.connector.agenticai.aiagent.memory.conversation.ConversationContext;
import java.util.UUID;

@JsonTypeName("my-conversation")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record MyConversationContext(String conversationId, UUID recordId)
    implements ConversationContext {}
