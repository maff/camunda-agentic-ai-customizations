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

// Union of all content types
export const ContentSchema = z.discriminatedUnion('type', [
  TextContentSchema,
  DocumentContentSchema,
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
  content: z.any().nullable(),
  properties: z.record(z.string(), z.any()).optional(),
});

// Token usage schema
export const TokenUsageSchema = z.object({
  inputTokenCount: z.number(),
  outputTokenCount: z.number(),
  totalTokenCount: z.number(),
});

// Framework metadata schema
export const FrameworkMetadataSchema = z.object({
  id: z.string(),
  modelName: z.string(),
  tokenUsage: TokenUsageSchema,
  finishReason: z.string(),
});

// Message metadata schema
export const MessageMetadataSchema = z.object({
  framework: FrameworkMetadataSchema.optional(),
  timestamp: z.number().optional(),
});

// Base message schema
export const BaseMessageSchema = z.object({
  metadata: z.record(z.string(), z.any()).optional(),
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

// Job context schema
export const JobContextSchema = z.object({
  bpmnProcessId: z.string(),
  processDefinitionKey: z.number(),
  processInstanceKey: z.number(),
  elementId: z.string(),
  elementInstanceKey: z.number(),
  tenantId: z.string(),
  type: z.string(),
});

// Conversation list item schema
export const ConversationListSchema = z.object({
  id: z.string().uuid(),
  conversationId: z.string().uuid(),
  createdAt: z.string(),
  updatedAt: z.string(),
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
export type TokenUsage = z.infer<typeof TokenUsageSchema>;
export type FrameworkMetadata = z.infer<typeof FrameworkMetadataSchema>;
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