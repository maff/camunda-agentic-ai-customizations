package io.camunda.example.aiagentruntime.memory.conversation.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record ProcessContext(
    String processDefinitionKey,
    String processInstanceKey,
    String elementId,
    String elementInstanceKey) {}
