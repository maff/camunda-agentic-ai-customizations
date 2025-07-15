import { Link, useParams } from 'react-router-dom';
import { useConversation } from '@/hooks/useConversations';
import { ChatMessage } from './ChatMessage';
import { Loading, InlineNotification, Grid, Column } from '@carbon/react';
import { ArrowLeft } from '@carbon/icons-react';
import { useEffect, useRef } from 'react';
import { Conversation } from '@/types/conversation';

const THINKING_WORDS = [
  'flabbergasting',
  'crunching',
  'pondering',
  'cogitating',
  'ruminating',
  'percolating',
  'mulling',
  'contemplating',
  'deliberating',
  'marinating'
];

const TOOL_CALL_WORDS = [
  'tooling around',
  'widget wrangling',
  'gadget grinding',
  'mechanism meddling',
  'apparatus assembling',
  'contraption conjuring',
  'device dancing',
  'gizmo gathering',
  'instrument investigating',
  'utensil utilizing'
];

function getRandomThinkingWord(): string {
  return THINKING_WORDS[Math.floor(Math.random() * THINKING_WORDS.length)];
}

function getRandomToolCallWord(): string {
  return TOOL_CALL_WORDS[Math.floor(Math.random() * TOOL_CALL_WORDS.length)];
}

function isConversationProgressing(conversation: Conversation): boolean {
  if (!conversation.messages || conversation.messages.length === 0) {
    return false;
  }
  
  const lastMessage = conversation.messages[conversation.messages.length - 1];
  
  // Check if last message was a user message
  if (lastMessage.role === 'user') {
    return true;
  }
  
  // Check if last message was an assistant message with tool calls
  if (lastMessage.role === 'assistant' && lastMessage.toolCalls && lastMessage.toolCalls.length > 0) {
    return true;
  }
  
  // Check if last message was a tool call result message
  if (lastMessage.role === 'tool_call_result') {
    return true;
  }
  
  return false;
}

function isWaitingForToolResults(conversation: Conversation): boolean {
  if (!conversation.messages || conversation.messages.length === 0) {
    return false;
  }
  
  const lastMessage = conversation.messages[conversation.messages.length - 1];
  
  // Check if last message was an assistant message with tool calls
  return lastMessage.role === 'assistant' && lastMessage.toolCalls && lastMessage.toolCalls.length > 0;
}

export function ConversationDetail() {
  const { conversationId } = useParams<{ conversationId: string }>();
  const { data: conversation, isLoading, error } = useConversation(conversationId!);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const thinkingWordRef = useRef<string>(getRandomThinkingWord());
  const toolCallWordRef = useRef<string>(getRandomToolCallWord());

  useEffect(() => {
    if (conversation && messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [conversation?.messages?.length]);

  // Update thinking word when conversation progressing status changes
  useEffect(() => {
    if (conversation && isConversationProgressing(conversation)) {
      if (isWaitingForToolResults(conversation)) {
        toolCallWordRef.current = getRandomToolCallWord();
      } else {
        thinkingWordRef.current = getRandomThinkingWord();
      }
    }
  }, [conversation?.messages?.length]);

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
              
              {isConversationProgressing(conversation) && (
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center',
                  gap: '0.5rem',
                  padding: '1rem',
                  backgroundColor: '#f4f4f4',
                  borderRadius: '0.5rem',
                  color: '#525252',
                  fontStyle: 'italic'
                }}>
                  <div style={{
                    width: '16px',
                    height: '16px',
                    border: '2px solid #e0e0e0',
                    borderTop: '2px solid #0f62fe',
                    borderRadius: '50%',
                    animation: 'spin 1s linear infinite'
                  }} />
                  <span>{isWaitingForToolResults(conversation) ? toolCallWordRef.current : thinkingWordRef.current}...</span>
                  <style>{`
                    @keyframes spin {
                      0% { transform: rotate(0deg); }
                      100% { transform: rotate(360deg); }
                    }
                  `}</style>
                </div>
              )}
              
              <div ref={messagesEndRef} />
            </div>
          )}
        </div>
      </Column>
    </Grid>
  );
}