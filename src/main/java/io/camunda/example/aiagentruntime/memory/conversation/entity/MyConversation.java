package io.camunda.example.aiagentruntime.memory.conversation.entity;

import io.camunda.connector.agenticai.model.message.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
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

  @OneToOne(fetch = FetchType.LAZY)
  private MyConversation parent;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb", nullable = false)
  private List<Message> messages = new ArrayList<>();

  public UUID getId() {
    return id;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public void setConversationId(UUID conversationId) {
    this.conversationId = conversationId;
  }

  public MyConversation getParent() {
    return parent;
  }

  public void setParent(MyConversation parent) {
    this.parent = parent;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
