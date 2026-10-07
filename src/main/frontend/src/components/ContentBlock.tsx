import type { ReactNode } from 'react';
import { Accordion, AccordionItem, CodeSnippet } from '@carbon/react';
import { isRecord } from './contentUtils';

const mutedStyle = { color: '#6f6f6f', fontStyle: 'italic' } as const;

function Json({ value }: { value: unknown }) {
  return <CodeSnippet type="multi">{JSON.stringify(value, null, 2)}</CodeSnippet>;
}

function Collapsible({ title, children }: { title: string; children: ReactNode }) {
  return (
    <Accordion size="sm" isFlush>
      <AccordionItem title={title}>{children}</AccordionItem>
    </Accordion>
  );
}



// Documents are references (id, content hash, metadata), not the document data itself
function documentLabel(document: unknown): string {
  if (isRecord(document) && isRecord(document.metadata)) {
    const { fileName, contentType, size } = document.metadata;
    const details = [contentType, typeof size === 'number' ? `${size} bytes` : undefined].filter(Boolean);
    return [fileName ?? 'Document', details.length > 0 ? `(${details.join(', ')})` : undefined]
      .filter(Boolean)
      .join(' ');
  }
  return 'Document';
}

/**
 * Renders one content block of a message or tool call result. Unknown block types fall back to a
 * collapsed JSON view, so new block types never break the view.
 */
export function ContentBlock({ content }: { content: { type: string } & Record<string, unknown> }) {
  switch (content.type) {
    case 'text':
      return <div style={{ whiteSpace: 'pre-wrap' }}>{String(content.text ?? '')}</div>;

    case 'object':
      return <Json value={content.content} />;

    case 'document':
      return (
        <Collapsible title={`📎 ${documentLabel(content.document)}`}>
          <Json value={content.document} />
        </Collapsible>
      );

    case 'reasoning': {
      const provider = typeof content.provider === 'string' ? ` (${content.provider})` : '';
      return (
        <Collapsible title={`Reasoning${provider}`}>
          {typeof content.text === 'string' && content.text && (
            <div style={{ whiteSpace: 'pre-wrap', marginBottom: '0.5rem' }}>{content.text}</div>
          )}
          <div style={{ ...mutedStyle, marginBottom: '0.5rem' }}>
            Provider specific reasoning payload (may be encrypted)
          </div>
          <Json value={content.payload} />
        </Collapsible>
      );
    }

    case 'provider': {
      const provider = typeof content.provider === 'string' ? ` (${content.provider})` : '';
      return (
        <Collapsible title={`Provider content${provider}`}>
          <div style={{ ...mutedStyle, marginBottom: '0.5rem' }}>
            Opaque provider specific content which is passed back to the provider
          </div>
          <Json value={content.payload} />
        </Collapsible>
      );
    }

    default:
      return (
        <Collapsible title={`${content.type} content`}>
          <Json value={content} />
        </Collapsible>
      );
  }
}
