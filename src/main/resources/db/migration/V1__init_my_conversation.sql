CREATE TABLE my_conversation
(
    id              UUID    NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE,
    conversation_id UUID    NOT NULL,
    process_context JSONB   NOT NULL,
    archived        BOOLEAN NOT NULL DEFAULT FALSE,
    messages        JSONB   NOT NULL,
    CONSTRAINT pk_myconversation PRIMARY KEY (id)
);

CREATE INDEX idx_conversation_id ON my_conversation (conversation_id);
