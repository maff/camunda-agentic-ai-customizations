package io.camunda.example.aiagentruntime.memory.conversation.entity;

import io.camunda.connector.agenticai.model.message.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(indexes = {@Index(name = "idx_conversation_id", columnList = "conversationId")})
public class MyConversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @CreationTimestamp private ZonedDateTime createdAt;
  @UpdateTimestamp private ZonedDateTime updatedAt;

  @Column(nullable = false)
  private UUID conversationId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private ProcessContext processContext;

  @Column(nullable = false)
  private boolean archived = false;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private List<Message> messages = new ArrayList<>();

  protected MyConversation() {}

  public MyConversation(UUID conversationId, ProcessContext processContext) {
    this.conversationId = conversationId;
    this.processContext = processContext;
  }

  public UUID getId() {
    return id;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public ProcessContext getProcessContext() {
    return processContext;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
