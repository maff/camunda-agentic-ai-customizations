import { useQuery } from '@tanstack/react-query';
import { fetchConversations, fetchConversation } from '@/api/conversations';

export function useConversations() {
  return useQuery({
    queryKey: ['conversations'],
    queryFn: fetchConversations,
    staleTime: 30000, // 30 seconds
    refetchInterval: 2000, // Poll every 2 seconds
  });
}

export function useConversation(conversationId: string) {
  return useQuery({
    queryKey: ['conversation', conversationId],
    queryFn: () => fetchConversation(conversationId),
    enabled: !!conversationId,
    staleTime: 60000, // 1 minute
    refetchInterval: 500, // Poll every 500ms
  });
}