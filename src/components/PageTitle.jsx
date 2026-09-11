import { Plus, Search } from 'lucide-react';

export function PageTitle({ title, subtitle, actionLabel, onAction, searchValue, onSearch, searchPlaceholder }) {
  return (
    <div className="page-title">
      <div>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
      <div className="page-title-actions">
        {onSearch && (
          <div className="mini-search">
            <Search size={16} />
            <input
              value={searchValue}
              onChange={(e) => onSearch(e.target.value)}
              placeholder={searchPlaceholder || 'Buscar...'}
            />
          </div>
        )}
        {actionLabel && (
          <button type="button" className="primary-small" onClick={onAction}>
            <Plus size={17} />{actionLabel}
          </button>
        )}
      </div>
    </div>
  );
}
