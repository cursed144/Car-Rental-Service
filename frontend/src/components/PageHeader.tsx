import type { ReactNode } from 'react';

type Props = {
  title: string;
  subtitle: string;
  actions?: ReactNode;
};

function PageHeader({ title, subtitle, actions }: Props) {
  return (
    <div className="page-header">
      <div>
        <h2>{title}</h2>
        <p className="section-subtitle">{subtitle}</p>
      </div>
      {actions ? <div className="page-header-actions">{actions}</div> : null}
    </div>
  );
}

export default PageHeader;
