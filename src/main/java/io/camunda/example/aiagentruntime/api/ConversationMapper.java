package io.camunda.example.aiagentruntime.api;

import io.camunda.connector.agenticai.model.message.ContentMessage;
import io.camunda.connector.agenticai.model.message.Message;
import io.camunda.connector.agenticai.model.message.UserMessage;
import io.camunda.connector.agenticai.model.message.content.TextContent;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversation;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversation.MyConversationJobContext;
import io.camunda.example.aiagentruntime.memory.conversation.MyConversationTurn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

  public ConversationListDto toListDto(MyConversation conversation, MyConversationTurn rootTurn) {
    final var rootMessages = rootTurn != null ? rootTurn.getMessages() : List.<Message>of();
    return new ConversationListDto(
        conversation.getId(),
        conversation.getId(),
        conversation.getCreatedAt(),
        conversation.getUpdatedAt(),
        conversation.getJobContext().bpmnProcessId(),
        extractFirstUserMessage(rootMessages));
  }

  public ConversationDto toDto(MyConversation conversation, List<MyConversationTurn> chain) {
    final List<Message> messages = new ArrayList<>();
    for (final var turn : chain) {
      messages.addAll(turn.getMessages());
    }
    return new ConversationDto(
        conversation.getId(),
        conversation.getId(),
        conversation.getCreatedAt(),
        conversation.getUpdatedAt(),
        toJobContextDto(conversation.getJobContext()),
        messages,
        extractFirstUserMessage(messages));
  }

  private ConversationDto.JobContextDto toJobContextDto(MyConversationJobContext jobContext) {
    return new ConversationDto.JobContextDto(
        jobContext.bpmnProcessId(),
        jobContext.processDefinitionKey(),
        jobContext.processInstanceKey(),
        jobContext.elementId(),
        jobContext.tenantId(),
        jobContext.type());
  }

  private String extractFirstUserMessage(List<Message> messages) {
    return messages.stream()
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

    return contentMessage.content().stream()
        .filter(content -> content instanceof TextContent)
        .map(content -> ((TextContent) content).text())
        .findFirst()
        .map(text -> text.length() > 100 ? text.substring(0, 97) + "..." : text)
        .orElse("Non-text message");
  }
}
