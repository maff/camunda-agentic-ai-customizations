CREATE TABLE my_conversation
(
    id              UUID  NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE,
    conversation_id UUID  NOT NULL,
    parent_id       UUID,
    messages        JSONB NOT NULL,
    CONSTRAINT pk_myconversation PRIMARY KEY (id)
);

ALTER TABLE my_conversation
    ADD CONSTRAINT FK_MYCONVERSATION_ON_PARENT FOREIGN KEY (parent_id) REFERENCES my_conversation (id);

CREATE INDEX idx_conversation_id ON my_conversation (conversation_id);
