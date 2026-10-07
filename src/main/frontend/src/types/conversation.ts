import { z } from 'zod';

// Base content schema
export const BaseContentSchema = z.object({
  type: z.string(),
});

// Text content schema
export const TextContentSchema = BaseContentSchema.extend({
  type: z.literal('text'),
  text: z.string(),
});

// Document content schema
export const DocumentContentSchema = BaseContentSchema.extend({
  type: z.literal('document'),
  document: z.any(), // Document structure would need to be defined based on actual usage
});

// Object content schema (structured data, e.g. in tool call results)
export const ObjectContentSchema = BaseContentSchema.extend({
  type: z.literal('object'),
  content: z.any(),
});

// Reasoning content schema (provider specific reasoning payload, e.g. encrypted reasoning items,
// with an optional human readable summary in text)
export const ReasoningContentSchema = BaseContentSchema.extend({
  type: z.literal('reasoning'),
  provider: z.string().nullable().optional(),
  payload: z.any(),
  text: z.string().nullable().optional(),
});

// Provider content schema (opaque provider specific content which is passed back to the provider)
export const ProviderContentSchema = BaseContentSchema.extend({
  type: z.literal('provider'),
  provider: z.string().nullable().optional(),
  payload: z.any(),
});

// Fallback for content types the UI does not know (yet), so unknown content never breaks the view
export const GenericContentSchema = z.looseObject({
  type: z.string(),
});

// Union of all content types
export const ContentSchema = z.union([
  TextContentSchema,
  DocumentContentSchema,
  ObjectContentSchema,
  ReasoningContentSchema,
  ProviderContentSchema,
  GenericContentSchema,
]);

// Tool call schema
export const ToolCallSchema = z.object({
  id: z.string(),
  name: z.string(),
  arguments: z.record(z.string(), z.any()),
});

// Tool call result schema
export const ToolCallResultSchema = z.object({
  id: z.string().nullable(),
  name: z.string().nullable(),
  // list of content blocks, e.g. { type: 'text', text } or { type: 'object', content }
  content: z.any().nullable(),
  elementId: z.string().nullable().optional(),
  completedAt: z.string().nullable().optional(),
  properties: z.record(z.string(), z.any()).optional(),
});

// Message metadata schema: a timestamp plus provider specific entries (e.g. { openai: { responseId, stopReason } })
export const MessageMetadataSchema = z.looseObject({
  // epoch seconds (older data) or an ISO-8601 string
  timestamp: z.union([z.number(), z.string()]).optional(),
});

// Base message schema
export const BaseMessageSchema = z.object({
  id: z.string().optional(),
  metadata: MessageMetadataSchema.nullable().optional(),
});

// Content message schema (for messages that can have content)
export const ContentMessageSchema = z.object({
  content: z.array(ContentSchema),
});

// System message schema
export const SystemMessageSchema = BaseMessageSchema.extend({
  role: z.literal('system'),
  content: z.array(ContentSchema),
});

// User message schema
export const UserMessageSchema = BaseMessageSchema.extend({
  role: z.literal('user'),
  name: z.string().nullable().optional(),
  content: z.array(ContentSchema),
});

// Assistant message schema
export const AssistantMessageSchema = BaseMessageSchema.extend({
  role: z.literal('assistant'),
  modelId: z.string().nullable().optional(),
  stopReason: z.string().nullable().optional(),
  content: z.array(ContentSchema).optional(),
  toolCalls: z.array(ToolCallSchema).optional(),
});

// Tool call result message schema
export const ToolCallResultMessageSchema = BaseMessageSchema.extend({
  role: z.literal('tool_call_result'),
  results: z.array(ToolCallResultSchema),
});

// Union of all message types
export const MessageSchema = z.discriminatedUnion('role', [
  SystemMessageSchema,
  UserMessageSchema,
  AssistantMessageSchema,
  ToolCallResultMessageSchema,
]);

// Job context schema (process-level metadata snapshotted at conversation creation;
// per-turn element instance key lives on the turn row, not here).
export const JobContextSchema = z.object({
  bpmnProcessId: z.string(),
  processDefinitionKey: z.number(),
  processInstanceKey: z.number(),
  elementId: z.string(),
  tenantId: z.string(),
  type: z.string(),
});

// Conversation list item schema
export const ConversationListSchema = z.object({
  id: z.string().uuid(),
  conversationId: z.string().uuid(),
  createdAt: z.string(),
  updatedAt: z.string(),
  bpmnProcessId: z.string(),
  firstUserMessage: z.string(),
});

// Full conversation schema
export const ConversationSchema = z.object({
  id: z.string().uuid(),
  conversationId: z.string().uuid(),
  createdAt: z.string(),
  updatedAt: z.string(),
  jobContext: JobContextSchema,
  messages: z.array(MessageSchema),
  firstUserMessage: z.string(),
});

// TypeScript types derived from schemas
export type BaseContent = z.infer<typeof BaseContentSchema>;
export type TextContent = z.infer<typeof TextContentSchema>;
export type DocumentContent = z.infer<typeof DocumentContentSchema>;
export type Content = z.infer<typeof ContentSchema>;
export type ToolCall = z.infer<typeof ToolCallSchema>;
export type ToolCallResult = z.infer<typeof ToolCallResultSchema>;
export type ObjectContent = z.infer<typeof ObjectContentSchema>;
export type ReasoningContent = z.infer<typeof ReasoningContentSchema>;
export type ProviderContent = z.infer<typeof ProviderContentSchema>;
export type MessageMetadata = z.infer<typeof MessageMetadataSchema>;
export type BaseMessage = z.infer<typeof BaseMessageSchema>;
export type ContentMessage = z.infer<typeof ContentMessageSchema>;
export type SystemMessage = z.infer<typeof SystemMessageSchema>;
export type UserMessage = z.infer<typeof UserMessageSchema>;
export type AssistantMessage = z.infer<typeof AssistantMessageSchema>;
export type ToolCallResultMessage = z.infer<typeof ToolCallResultMessageSchema>;
export type Message = z.infer<typeof MessageSchema>;
export type JobContext = z.infer<typeof JobContextSchema>;
export type ConversationList = z.infer<typeof ConversationListSchema>;
export type Conversation = z.infer<typeof ConversationSchema>;