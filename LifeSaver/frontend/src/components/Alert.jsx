import React from 'react';

/**
 * Alert Banner Component
 * Displays success, error, warning, or information messages.
 */
export default function Alert({ type = 'info', message, onDismiss }) {
  if (!message) return null;

  const icons = {
    success: '✓',
    error: '⚠️',
    warning: '!',
    info: 'ℹ',
  };

  return (
    <div className={`alert-banner alert-${type}`} role="alert">
      <span className="alert-icon">{icons[type] || 'ℹ'}</span>
      <div className="alert-message">{message}</div>
      {onDismiss && (
        <button
          type="button"
          className="alert-dismiss-btn"
          onClick={onDismiss}
          aria-label="Dismiss alert"
        >
          ✕
        </button>
      )}
    </div>
  );
}
