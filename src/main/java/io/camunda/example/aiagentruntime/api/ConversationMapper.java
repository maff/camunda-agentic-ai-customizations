package io.camunda.example.aiagentruntime.api;

import io.camunda.connector.agenticai.model.message.ContentMessage;
import io.camunda.connector.agenticai.model.message.Message;
import io.camunda.connector.agenticai.model.message.UserMessage;
import io.camunda.connector.agenticai.model.message.content.TextContent;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversation;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversation.MyConversationJobContext;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

  public ConversationListDto toListDto(MyConversation conversation) {
    return new ConversationListDto(
        conversation.getId(),
        conversation.getConversationId(),
        conversation.getCreatedAt(),
        conversation.getUpdatedAt(),
        extractFirstUserMessage(conversation)
    );
  }

  public ConversationDto toDto(MyConversation conversation) {
    return new ConversationDto(
        conversation.getId(),
        conversation.getConversationId(),
        conversation.getCreatedAt(),
        conversation.getUpdatedAt(),
        toJobContextDto(conversation.getJobContext()),
        conversation.getMessages(),
        extractFirstUserMessage(conversation)
    );
  }

  private ConversationDto.JobContextDto toJobContextDto(MyConversationJobContext jobContext) {
    return new ConversationDto.JobContextDto(
        jobContext.bpmnProcessId(),
        jobContext.processDefinitionKey(),
        jobContext.processInstanceKey(),
        jobContext.elementId(),
        jobContext.elementInstanceKey(),
        jobContext.tenantId(),
        jobContext.type()
    );
  }

  private String extractFirstUserMessage(MyConversation conversation) {
    return conversation.getMessages().stream()
        .filter(message -> message instanceof UserMessage)
        .findFirst()
        .map(this::extractTextContent)
        .orElse("No user message");
  }

  private String extractTextContent(Message message) {
    if (!(message instanceof ContentMessage contentMessage)) {
      return "Non-content message";
    }
    
    if (contentMessage.content() == null || contentMessage.content().isEmpty()) {
      return "Empty message";
    }
    
    // Get first text content from the message
    return contentMessage.content().stream()
        .filter(content -> content instanceof TextContent)
        .map(content -> ((TextContent) content).text())
        .findFirst()
        .map(text -> {
          // Truncate if too long
          return text != null && text.length() > 100 
              ? text.substring(0, 97) + "..." 
              : text;
        })
        .orElse("Non-text message");
  }
}
