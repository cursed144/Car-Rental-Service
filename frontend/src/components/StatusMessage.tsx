type Props = {
  kind?: 'success' | 'error' | 'info';
  message?: string;
};

function StatusMessage({ kind = 'info', message }: Props) {
  if (!message) return null;
  return <div className={`status-message ${kind}`}>{message}</div>;
}

export default StatusMessage;
