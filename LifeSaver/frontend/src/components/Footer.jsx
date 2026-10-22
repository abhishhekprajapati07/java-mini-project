import React from 'react';
import { Link } from 'react-router-dom';

const CURRENT_YEAR = new Date().getFullYear();

/**
 * Footer Component
 * Includes project information, quick navigation links, privacy statement, and educational disclaimer.
 */
export default function Footer() {
  return (
    <footer className="footer-section">
      <div className="footer-container">
        <div className="footer-grid">
          {/* Brand Info */}
          <div className="footer-col footer-col-brand">
            <div className="footer-brand">
              <span className="brand-drop">🩸</span>
              <span className="footer-brand-name">LifeSaver</span>
            </div>
            <p className="footer-desc">
              A comprehensive Blood Donation and Voluntary Donor Management System built as a college full-stack Java mini project.
            </p>
            <div className="footer-tech-stack">
              <span>Spring Boot</span> • <span>React</span> • <span>JDBC</span> • <span>MySQL</span>
            </div>
          </div>

          {/* Quick Links */}
          <div className="footer-col">
            <h4 className="footer-heading">Quick Actions</h4>
            <ul className="footer-links">
              <li><Link to="/donors">Find Voluntary Donors</Link></li>
              <li><Link to="/register-donor">Register as Donor</Link></li>
              <li><Link to="/request-blood">Submit Blood Request</Link></li>
              <li><Link to="/about">Eligibility & Guidelines</Link></li>
              <li><Link to="/admin/login">Administrator Login</Link></li>
            </ul>
          </div>

          {/* Medical Notice */}
          <div className="footer-col footer-col-wide">
            <h4 className="footer-heading">Medical & Safety Notice</h4>
            <p className="footer-notice">
              <strong>LifeSaver is an educational mini project, not a certified clinical blood bank or medical service.</strong>
              Matching ABO or Rh blood groups does not guarantee biological compatibility or donation eligibility.
              All blood products must be officially collected, screened, crossmatched, and issued by qualified healthcare personnel at certified facilities.
            </p>
          </div>
        </div>

        <div className="footer-bottom">
          <p>© {CURRENT_YEAR} LifeSaver — Blood Donation and Donor Management System. For educational evaluation only.</p>
          <p className="footer-subtext">No real donor data or contact information is collected or shared.</p>
        </div>
      </div>
    </footer>
  );
}
