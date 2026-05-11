package io.camunda.example.aiagentruntime.memory.conversation;

import io.camunda.connector.api.outbound.JobContext;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

/**
 * Parent entity: one row per logical conversation, keyed by the {@code conversationId} that {@link
 * MyConversationContext} carries.
 *
 * <p>The {@code lastKnownHeadId} column is a storage-side projection of the live conversation head,
 * updated by {@link MyConversationStore#onJobCompleted} after Zeebe has accepted the job-completion
 * command. Because the projection only advances post-commit, it can be stale (if a callback fails
 * to fire) but never points at an uncommitted turn.
 */
@Entity
@Table(name = "conversations")
public class MyConversation {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private ZonedDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private ZonedDateTime updatedAt;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "job_context", nullable = false)
  private MyConversationJobContext jobContext;

  @Column(name = "last_known_head_id")
  private UUID lastKnownHeadId;

  protected MyConversation() {}

  public MyConversation(MyConversationJobContext jobContext) {
    this.jobContext = jobContext;
    this.createdAt = ZonedDateTime.now();
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

  public MyConversationJobContext getJobContext() {
    return jobContext;
  }

  public UUID getLastKnownHeadId() {
    return lastKnownHeadId;
  }

  public void setLastKnownHeadId(UUID lastKnownHeadId) {
    this.lastKnownHeadId = lastKnownHeadId;
  }

  @Embeddable
  public record MyConversationJobContext(
      String bpmnProcessId,
      long processDefinitionKey,
      long processInstanceKey,
      String elementId,
      String tenantId,
      String type) {

    public static MyConversationJobContext from(JobContext jobContext) {
      return new MyConversationJobContext(
          jobContext.getBpmnProcessId(),
          jobContext.getProcessDefinitionKey(),
          jobContext.getProcessInstanceKey(),
          jobContext.getElementId(),
          jobContext.getTenantId(),
          jobContext.getType());
    }
  }
}
