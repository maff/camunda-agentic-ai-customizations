import { describe, expect, it } from 'vitest';
import { ConversationSchema } from './conversation';
import conversationJson from './__fixtures__/conversation.json';

// Captured from GET /api/conversations/{id} of a running agent (texts shortened, one document added).
// Update it when the backend's message format changes.
const fixture: unknown = conversationJson;

describe('ConversationSchema', () => {
  it('parses a conversation as returned by the API', () => {
    const result = ConversationSchema.safeParse(fixture);

    expect(result.error?.issues).toBeUndefined();
    expect(result.success).toBe(true);
  });

  it('parses reasoning, document and tool call content', () => {
    const conversation = ConversationSchema.parse(fixture);
    const types = conversation.messages.flatMap((message) =>
      'content' in message && Array.isArray(message.content)
        ? message.content.map((content) => content.type)
        : []
    );

    expect(types).toContain('reasoning');
    expect(types).toContain('document');
    expect(conversation.messages.map((message) => message.role)).toContain('tool_call_result');
  });

  it('rejects numeric message timestamps', () => {
    const conversation = structuredClone(fixture) as { messages: { metadata?: object }[] };
    conversation.messages[1].metadata = { timestamp: 1759860000 };

    expect(ConversationSchema.safeParse(conversation).success).toBe(false);
  });
});
