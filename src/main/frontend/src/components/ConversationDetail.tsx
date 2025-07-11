import { Link, useParams } from 'react-router-dom';
import { useConversation } from '@/hooks/useConversations';
import { ChatMessage } from './ChatMessage';
import { Loading, InlineNotification, Grid, Column } from '@carbon/react';
import { ArrowLeft } from '@carbon/icons-react';

export function ConversationDetail() {
  const { conversationId } = useParams<{ conversationId: string }>();
  const { data: conversation, isLoading, error } = useConversation(conversationId!);

  if (isLoading) {
    return (
      <Grid className="min-h-screen">
        <Column sm={4} md={8} lg={16}>
          <Loading description="Loading conversation..." />
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
            title="Error loading conversation"
            subtitle={error.message}
          />
          <Link to="/" style={{ marginTop: '1rem', display: 'inline-block' }}>
            Back to conversations
          </Link>
        </Column>
      </Grid>
    );
  }

  if (!conversation) {
    return (
      <Grid className="min-h-screen">
        <Column sm={4} md={8} lg={16}>
          <InlineNotification
            kind="info"
            title="Conversation not found"
            subtitle="The requested conversation could not be found"
          />
          <Link to="/" style={{ marginTop: '1rem', display: 'inline-block' }}>
            Back to conversations
          </Link>
        </Column>
      </Grid>
    );
  }

  return (
    <Grid>
      <Column sm={4} md={8} lg={16}>
        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', marginBottom: '1.5rem', textDecoration: 'none', color: '#0f62fe' }}>
            <ArrowLeft size={16} style={{ marginRight: '0.5rem' }} />
            Back to conversations
          </Link>
          
          <div style={{ marginBottom: '1rem' }}>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', marginBottom: '0.75rem' }}>
              Conversation Details
            </h1>
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'auto 1fr', 
              gap: '0.5rem 1rem',
              fontSize: '0.875rem',
              color: '#525252',
              maxWidth: '600px'
            }}>
              <span style={{ fontWeight: '500', color: '#393939' }}>ID:</span>
              <span style={{ fontFamily: 'monospace' }}>{conversation.conversationId}</span>
              
              <span style={{ fontWeight: '500', color: '#393939' }}>Created:</span>
              <span>{new Date(conversation.createdAt).toLocaleString()}</span>
              
              <span style={{ fontWeight: '500', color: '#393939' }}>Updated:</span>
              <span>{new Date(conversation.updatedAt).toLocaleString()}</span>
              
              <span style={{ fontWeight: '500', color: '#393939' }}>Process:</span>
              <span style={{ fontFamily: 'monospace' }}>{conversation.jobContext.bpmnProcessId}</span>
            </div>
          </div>
        </div>

        {/* Messages */}
        <div>
          <h2 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '1rem' }}>Messages</h2>
          
          {conversation.messages.length === 0 ? (
            <InlineNotification
              kind="info"
              title="No messages"
              subtitle="This conversation has no messages"
            />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {conversation.messages.map((message, index) => (
                <ChatMessage key={index} message={message} />
              ))}
            </div>
          )}
        </div>
      </Column>
    </Grid>
  );
}