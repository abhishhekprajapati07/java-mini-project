import React from 'react';

/**
 * StatusBadge Component
 * Color-coded visual badges for blood request statuses and donor availability.
 */
export default function StatusBadge({ status, type = 'request' }) {
  if (type === 'availability') {
    const isAvailable = Boolean(status);
    return (
      <span className={`badge ${isAvailable ? 'badge-available' : 'badge-unavailable'}`}>
        <span className="badge-dot"></span>
        {isAvailable ? 'Available to Donate' : 'Currently Unavailable'}
      </span>
    );
  }

  const s = (status || '').toUpperCase();
  let badgeClass = 'badge-pending';
  let label = s;

  switch (s) {
    case 'VERIFIED':
      badgeClass = 'badge-verified';
      label = 'Verified by Staff';
      break;
    case 'FULFILLED':
      badgeClass = 'badge-fulfilled';
      label = 'Fulfilled';
      break;
    case 'REJECTED':
      badgeClass = 'badge-rejected';
      label = 'Closed / Rejected';
      break;
    case 'PENDING':
    default:
      badgeClass = 'badge-pending';
      label = 'Unverified / Pending';
      break;
  }

  return (
    <span className={`badge ${badgeClass}`}>
      <span className="badge-dot"></span>
      {label}
    </span>
  );
}
