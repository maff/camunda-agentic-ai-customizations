package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.aiagent.model.AgentJobContext;
import io.camunda.connector.agenticai.model.message.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

  private ZonedDateTime createdAt;
  @UpdateTimestamp private ZonedDateTime updatedAt;

  @Column(nullable = false)
  private UUID conversationId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private MyConversationJobContext jobContext;

  @Column(nullable = false)
  private boolean archived = false;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private List<Message> messages = new ArrayList<>();

  protected MyConversation() {}

  public MyConversation(UUID conversationId, MyConversationJobContext jobContext) {
    this.conversationId = conversationId;
    this.jobContext = jobContext;
    this.createdAt = ZonedDateTime.now();
  }

  public MyConversation(
      UUID conversationId, MyConversationJobContext jobContext, MyConversation previous) {
    this.conversationId = conversationId;
    this.jobContext = jobContext;
    this.createdAt = previous.createdAt;
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

  public MyConversationJobContext getJobContext() {
    return jobContext;
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

  @Embeddable
  public record MyConversationJobContext(
      String bpmnProcessId,
      long processDefinitionKey,
      long processInstanceKey,
      String elementId,
      long elementInstanceKey,
      String tenantId,
      String type) {

    public static MyConversationJobContext from(AgentJobContext jobContext) {
      return new MyConversationJobContext(
          jobContext.bpmnProcessId(),
          jobContext.processDefinitionKey(),
          jobContext.processInstanceKey(),
          jobContext.elementId(),
          jobContext.elementInstanceKey(),
          jobContext.tenantId(),
          jobContext.type());
    }
  }
}
