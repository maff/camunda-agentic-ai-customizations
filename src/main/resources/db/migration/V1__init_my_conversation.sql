-- A logical conversation: one row per agent execution, identified by the
-- conversationId that MyConversationContext carries.
--
-- The `last_known_head_id` column is the storage-side projection of the live
-- conversation head. It is updated in MyConversationStore#onJobCompleted after
-- Zeebe has accepted the job-completion command, so the projection never
-- points at an uncommitted turn. The UI reads from this projection.
CREATE TABLE conversations
(
    id                   UUID NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    job_context          JSONB NOT NULL,
    last_known_head_id   UUID,
    CONSTRAINT pk_conversations PRIMARY KEY (id)
);

-- One row per agent turn. Append-only: storeMessages always inserts a new row.
-- Each turn stores only the messages added during that turn (delta), and
-- points at its predecessor via `parent_id`. The full message history is
-- reassembled by walking parent_id from the head turn back to the root.
--
-- Orphan rows can exist transiently if a job-completion is rejected by Zeebe
-- before MyConversationStore#onJobCompletionFailed deletes them. They are
-- harmless: nothing references them, the chain skips over them, and they are
-- invisible to the UI.
CREATE TABLE conversation_turns
(
    id                    UUID NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    conversation_id       UUID NOT NULL,
    parent_id             UUID,
    element_instance_key  BIGINT NOT NULL,
    job_activated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    messages              JSONB NOT NULL,
    CONSTRAINT pk_conversation_turns PRIMARY KEY (id),
    CONSTRAINT fk_conversation_turns_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);

CREATE INDEX idx_conversation_turns_conversation_id ON conversation_turns (conversation_id);
CREATE INDEX idx_conversation_turns_parent_id ON conversation_turns (parent_id);
