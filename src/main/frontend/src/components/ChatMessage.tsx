import type { Message } from '@/types/conversation';
import { MessageMetadataComponent } from './MessageMetadata';
import { Tile, CodeSnippet } from '@carbon/react';

interface ChatMessageProps {
  message: Message;
}

export function ChatMessage({ message }: ChatMessageProps) {
  const renderContent = () => {
    // Only content messages have content property
    if (message.role === 'tool_call_result') {
      return <span style={{ color: '#6f6f6f', fontStyle: 'italic' }}>Tool call result</span>;
    }

    const contentMessage = message as Extract<Message, { content: any }>;
    if (!contentMessage.content || contentMessage.content.length === 0) {
      return <span style={{ color: '#6f6f6f', fontStyle: 'italic' }}>No content</span>;
    }

    return contentMessage.content.map((content, index) => {
      if (content.type === 'text') {
        return (
          <div key={index} style={{ whiteSpace: 'pre-wrap' }}>
            {content.text}
          </div>
        );
      }
      return (
        <div key={index} style={{ color: '#6f6f6f', fontStyle: 'italic' }}>
          [{content.type} content]
        </div>
      );
    });
  };

  const renderToolCalls = () => {
    // Only assistant messages have toolCalls
    if (message.role !== 'assistant') {
      return null;
    }

    const assistantMessage = message as Extract<Message, { role: 'assistant' }>;
    if (!assistantMessage.toolCalls || assistantMessage.toolCalls.length === 0) {
      return null;
    }

    return (
      <div style={{ marginTop: '0.75rem', borderTop: '1px solid #e0e0e0', paddingTop: '0.5rem' }}>
        <div style={{ fontSize: '0.75rem', color: '#525252', marginBottom: '0.5rem', fontWeight: '600' }}>Tool Calls:</div>
        {assistantMessage.toolCalls.map((toolCall, index) => (
          <Tile key={index} style={{ marginBottom: '0.5rem', backgroundColor: '#edf4ff' }}>
            <div style={{ fontSize: '0.875rem', fontWeight: '500', color: '#0043ce', marginBottom: '0.25rem' }}>{toolCall.name}</div>
            <CodeSnippet type="multi">
              {JSON.stringify(toolCall.arguments, null, 2)}
            </CodeSnippet>
          </Tile>
        ))}
      </div>
    );
  };

  const renderToolResults = () => {
    // Only tool call result messages have results
    if (message.role !== 'tool_call_result') {
      return null;
    }

    const toolResultMessage = message as Extract<Message, { role: 'tool_call_result' }>;
    if (!toolResultMessage.results || toolResultMessage.results.length === 0) {
      return null;
    }

    return (
      <div style={{ marginTop: '0.75rem' }}>
        <div style={{ fontSize: '0.75rem', color: '#525252', marginBottom: '0.5rem', fontWeight: '600' }}>Tool Results:</div>
        {toolResultMessage.results.map((result, index) => (
          <Tile key={index} style={{ marginBottom: '0.5rem', backgroundColor: '#f4ffed' }}>
            <div style={{ fontSize: '0.875rem', fontWeight: '500', color: '#198038', marginBottom: '0.25rem' }}>{result.name}</div>
            <CodeSnippet type="multi">
              {typeof result.content === 'string' 
                ? result.content 
                : JSON.stringify(result.content, null, 2)}
            </CodeSnippet>
          </Tile>
        ))}
      </div>
    );
  };

  const getMessageAlignment = () => {
    switch (message.role) {
      case 'user':
        return { display: 'flex', justifyContent: 'flex-end' };
      case 'system':
        return { display: 'flex', justifyContent: 'center' };
      default:
        return { display: 'flex', justifyContent: 'flex-start' };
    }
  };

  const getMessageStyle = () => {
    switch (message.role) {
      case 'user':
        return { backgroundColor: '#0f62fe', color: 'white' };
      case 'system':
        return { backgroundColor: '#f4f4f4', color: '#161616' };
      case 'tool_call_result':
        return { backgroundColor: '#fff2cc', color: '#161616' };
      default:
        return { backgroundColor: '#ffffff', color: '#161616', border: '1px solid #e0e0e0' };
    }
  };

  return (
    <div style={{ marginBottom: '1rem', ...getMessageAlignment() }}>
      <div style={{ maxWidth: '100%', width: '100%' }}>
        <div style={{ fontSize: '0.75rem', color: '#6f6f6f', marginBottom: '0.25rem', paddingLeft: '0.25rem', textTransform: 'capitalize' }}>
          {message.role.replace('_', ' ')}
        </div>
        <Tile style={{ ...getMessageStyle(), padding: '1rem' }}>
          {renderContent()}
          {renderToolCalls()}
          {renderToolResults()}
          <MessageMetadataComponent metadata={message.metadata} />
        </Tile>
      </div>
    </div>
  );
}