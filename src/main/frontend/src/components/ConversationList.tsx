import { Link } from 'react-router-dom';
import { useConversations } from '@/hooks/useConversations';
import { Loading, InlineNotification, ClickableTile, Grid, Column } from '@carbon/react';
import { ChevronRight } from '@carbon/icons-react';

export function ConversationList() {
  const { data: conversations, isLoading, error } = useConversations();

  if (isLoading) {
    return (
      <Grid className="min-h-screen">
        <Column sm={4} md={8} lg={16}>
          <Loading description="Loading conversations..." />
        </Column>
      </Grid>
    );
  }

  if (error) {
    return (
      <Grid className="min-h-screen">
        <Column sm={4} md={8} lg={16}>
          <InlineNotification
            kind="error"
            title="Error loading conversations"
            subtitle={error.message}
          />
        </Column>
      </Grid>
    );
  }

  if (!conversations || conversations.length === 0) {
    return (
      <Grid className="min-h-screen">
        <Column sm={4} md={8} lg={16}>
          <InlineNotification
            kind="info"
            title="No conversations found"
            subtitle="There are no conversations to display"
          />
        </Column>
      </Grid>
    );
  }

  return (
    <Grid>
      <Column sm={4} md={8} lg={16}>
        <h1 style={{ marginBottom: '2rem', fontSize: '2rem', fontWeight: 'bold' }}>Conversations</h1>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {conversations.map((conversation) => (
            <Link
              key={conversation.id}
              to={`/conversation/${conversation.conversationId}`}
              style={{ textDecoration: 'none' }}
            >
              <ClickableTile>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem' }}>
                    ID: {conversation.conversationId}
                  </div>
                  <div style={{ fontSize: '1.125rem', fontWeight: '500', marginBottom: '0.5rem' }}>
                    {conversation.firstUserMessage}
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#525252' }}>
                    Created: {new Date(conversation.createdAt).toLocaleString()}
                  </div>
                  {conversation.updatedAt !== conversation.createdAt && (
                    <div style={{ fontSize: '0.875rem', color: '#525252' }}>
                      Updated: {new Date(conversation.updatedAt).toLocaleString()}
                    </div>
                  )}
                </div>
                <ChevronRight size={20} />
              </div>
              </ClickableTile>
            </Link>
          ))}
        </div>
      </Column>
    </Grid>
  );
}