package io.camunda.example.aiagentruntime.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ConversationListDto(
    UUID id,
    UUID conversationId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime updatedAt,
    String bpmnProcessId,
    String firstUserMessage) {}
