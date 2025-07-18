import { Link } from 'react-router-dom';
import { useConversations } from '@/hooks/useConversations';
import { Loading, InlineNotification, Grid, Column } from '@carbon/react';
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
              <div style={{ 
                padding: '1.5rem',
                backgroundColor: 'white',
                border: '1px solid #d0d0d0',
                borderRadius: '8px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                transition: 'all 0.2s ease',
                cursor: 'pointer',
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'flex-start'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)';
                e.currentTarget.style.cursor = 'pointer';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
              }}
              >
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '1.125rem', fontWeight: '500', marginBottom: '0.5rem', color: '#161616' }}>
                    {conversation.firstUserMessage}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem' }}>
                    Process: {conversation.bpmnProcessId}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem' }}>
                    ID: {conversation.conversationId}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem' }}>
                    Created: {new Date(conversation.createdAt).toLocaleString()}
                  </div>
                  {conversation.updatedAt !== conversation.createdAt && (
                    <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem' }}>
                      Updated: {new Date(conversation.updatedAt).toLocaleString()}
                    </div>
                  )}
                </div>
                <ChevronRight size={20} style={{ color: '#525252' }} />
              </div>
            </Link>
          ))}
        </div>
      </Column>
    </Grid>
  );
}