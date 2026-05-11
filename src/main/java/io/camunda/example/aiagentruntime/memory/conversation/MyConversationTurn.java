package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.agenticai.model.message.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Child entity: one row per agent turn. Append-only — {@link MyConversationSession#storeMessages}
 * always inserts a new row, never mutates an existing one.
 *
 * <p>Each row stores only the messages added during this turn (the delta). The full conversation
 * history is reassembled by walking {@code parentId} from the head turn back to the root and
 * concatenating message lists.
 */
@Entity
@Table(name = "conversation_turns")
public class MyConversationTurn {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "conversation_id", nullable = false)
  private UUID conversationId;

  @Column(name = "parent_id")
  private UUID parentId;

  @Column(name = "element_instance_key", nullable = false)
  private long elementInstanceKey;

  @Column(name = "job_activated_at", nullable = false)
  private ZonedDateTime jobActivatedAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private List<Message> messages = new ArrayList<>();

  protected MyConversationTurn() {}

  public MyConversationTurn(
      UUID conversationId,
      UUID parentId,
      long elementInstanceKey,
      ZonedDateTime jobActivatedAt,
      List<Message> messages) {
    this.conversationId = conversationId;
    this.parentId = parentId;
    this.elementInstanceKey = elementInstanceKey;
    this.jobActivatedAt = jobActivatedAt;
    this.messages = messages;
    this.createdAt = ZonedDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public UUID getConversationId() {
    return conversationId;
  }

  public UUID getParentId() {
    return parentId;
  }

  public long getElementInstanceKey() {
    return elementInstanceKey;
  }

  public ZonedDateTime getJobActivatedAt() {
    return jobActivatedAt;
  }

  public List<Message> getMessages() {
    return messages;
  }
}
