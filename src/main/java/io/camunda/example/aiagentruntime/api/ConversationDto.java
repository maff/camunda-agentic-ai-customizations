package io.camunda.example.aiagentruntime.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.camunda.connector.agenticai.model.message.Message;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ConversationDto(
    UUID id,
    UUID conversationId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime updatedAt,
    JobContextDto jobContext,
    List<Message> messages,
    String firstUserMessage) {

  public record JobContextDto(
      String bpmnProcessId,
      long processDefinitionKey,
      long processInstanceKey,
      String elementId,
      long elementInstanceKey,
      String tenantId,
      String type) {}
}
