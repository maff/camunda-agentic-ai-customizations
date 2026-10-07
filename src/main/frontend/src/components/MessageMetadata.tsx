import { useState } from 'react';
import type { Message } from '@/types/conversation';
import { Button, CodeSnippet, Tile } from '@carbon/react';
import { ChevronDown, ChevronRight } from '@carbon/icons-react';

interface MessageMetadataProps {
  message: Message;
}

export function MessageMetadataComponent({ message }: MessageMetadataProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  const { timestamp, ...providerMetadata } = message.metadata ?? {};
  const modelId = message.role === 'assistant' ? message.modelId : undefined;
  const stopReason = message.role === 'assistant' ? message.stopReason : undefined;
  const hasProviderMetadata = Object.keys(providerMetadata).length > 0;

  if (!timestamp && !modelId && !stopReason && !hasProviderMetadata) {
    return null;
  }

  return (
    <div style={{ marginTop: '0.5rem' }}>
      <Button
        kind="ghost"
        size="sm"
        onClick={() => setIsExpanded(!isExpanded)}
        renderIcon={isExpanded ? ChevronDown : ChevronRight}
      >
        Metadata
      </Button>

      {isExpanded && (
        <Tile style={{ marginTop: '0.5rem', fontSize: '0.75rem', backgroundColor: '#f4f4f4' }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '0.5rem' }}>
            {modelId && (
              <div>
                <span style={{ fontWeight: '600' }}>Model:</span> {modelId}
              </div>
            )}
            {stopReason && (
              <div>
                <span style={{ fontWeight: '600' }}>Stop Reason:</span> {stopReason}
              </div>
            )}
            {(typeof timestamp === 'number' || typeof timestamp === 'string') && (
              <div>
                <span style={{ fontWeight: '600' }}>Time:</span> {new Date(typeof timestamp === 'number' ? timestamp * 1000 : timestamp).toLocaleString()}
              </div>
            )}
            {hasProviderMetadata && (
              <div style={{ gridColumn: 'span 2' }}>
                <span style={{ fontWeight: '600' }}>Provider:</span>
                <CodeSnippet type="multi">{JSON.stringify(providerMetadata, null, 2)}</CodeSnippet>
              </div>
            )}
          </div>
        </Tile>
      )}
    </div>
  );
}
