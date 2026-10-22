import React from 'react';
import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="not-found-page page-container text-center">
      <div className="not-found-card">
        <div className="not-found-icon">404</div>
        <h1 className="page-title">Page Not Found</h1>
        <p className="page-subtitle">
          The page or resource you are looking for does not exist on LifeSaver.
        </p>
        <div className="not-found-actions">
          <Link to="/" className="btn btn-primary btn-lg">
            Return to Home Page
          </Link>
          <Link to="/donors" className="btn btn-secondary btn-lg">
            Search Voluntary Donors
          </Link>
        </div>
      </div>
    </div>
  );
}
