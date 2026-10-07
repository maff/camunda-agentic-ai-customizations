export function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

export function isContentBlock(value: unknown): value is { type: string } & Record<string, unknown> {
  return isRecord(value) && typeof value.type === 'string';
}
