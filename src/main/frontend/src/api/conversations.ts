import { z } from 'zod';
import { ConversationListSchema, ConversationSchema } from '@/types/conversation';
import type { ConversationList, Conversation } from '@/types/conversation';

const API_BASE = '/api';

export async function fetchConversations(): Promise<ConversationList[]> {
  const response = await fetch(`${API_BASE}/conversations`);
  if (!response.ok) {
    throw new Error(`Failed to fetch conversations: ${response.status}`);
  }
  const data = await response.json();
  
  // Validate response with Zod
  const result = z.array(ConversationListSchema).safeParse(data);
  if (!result.success) {
    console.error('Invalid conversation list data:', result.error);
    throw new Error('Invalid data format received from server');
  }
  
  return result.data;
}

export async function fetchConversation(conversationId: string): Promise<Conversation> {
  const response = await fetch(`${API_BASE}/conversations/${conversationId}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Conversation not found');
    }
    throw new Error(`Failed to fetch conversation: ${response.status}`);
  }
  const data = await response.json();
  
  // Validate response with Zod
  const result = ConversationSchema.safeParse(data);
  if (!result.success) {
    console.error('Invalid conversation data:', result.error);
    console.error('Raw data:', JSON.stringify(data, null, 2));
    throw new Error('Invalid data format received from server');
  }
  
  return result.data;
}