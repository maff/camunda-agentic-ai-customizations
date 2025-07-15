import { useState } from 'react';
import type { MessageMetadata } from '@/types/conversation';
import { Button, Tile } from '@carbon/react';
import { ChevronDown, ChevronRight } from '@carbon/icons-react';

interface MessageMetadataProps {
  metadata?: MessageMetadata;
}

export function MessageMetadataComponent({ metadata }: MessageMetadataProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  if (!metadata?.framework) {
    return null;
  }

  const { framework } = metadata;

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
            <div>
              <span style={{ fontWeight: '600' }}>Model:</span> {framework.modelName}
            </div>
            <div>
              <span style={{ fontWeight: '600' }}>Finish Reason:</span> {framework.finishReason}
            </div>
            {framework.tokenUsage && (
              <>
                <div>
                  <span style={{ fontWeight: '600' }}>Input Tokens:</span> {framework.tokenUsage.inputTokenCount}
                </div>
                <div>
                  <span style={{ fontWeight: '600' }}>Output Tokens:</span> {framework.tokenUsage.outputTokenCount}
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                  <span style={{ fontWeight: '600' }}>Total Tokens:</span> {framework.tokenUsage.totalTokenCount}
                </div>
              </>
            )}
          </div>
        </Tile>
      )}
    </div>
  );
}