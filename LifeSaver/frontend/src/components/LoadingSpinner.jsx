import React from 'react';

/**
 * LoadingSpinner Component
 * Elegant crimson animated pulse loader.
 */
export default function LoadingSpinner({ message = 'Loading LifeSaver records...' }) {
  return (
    <div className="spinner-container" role="status" aria-live="polite">
      <div className="spinner-ring">
        <div className="spinner-dot"></div>
      </div>
      {message && <p className="spinner-text">{message}</p>}
    </div>
  );
}
