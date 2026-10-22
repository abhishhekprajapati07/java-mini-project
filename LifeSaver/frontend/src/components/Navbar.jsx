import React, { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Navbar Component
 * Responsive navigation bar with LifeSaver branding and active state styling.
 */
export default function Navbar() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { adminUser, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
    setMobileMenuOpen(false);
  };

  const closeMenu = () => setMobileMenuOpen(false);

  return (
    <header className="navbar-header">
      <div className="navbar-container">
        {/* Brand Logo */}
        <Link to="/" className="navbar-brand" onClick={closeMenu}>
          <div className="brand-logo-icon">
            <span className="brand-drop">🩸</span>
          </div>
          <div className="brand-text">
            <span className="brand-title">LifeSaver</span>
            <span className="brand-subtitle">Blood Donation & Donor Management</span>
          </div>
        </Link>

        {/* Mobile Menu Hamburger */}
        <button
          type="button"
          className="mobile-toggle-btn"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-expanded={mobileMenuOpen}
          aria-label="Toggle navigation menu"
        >
          {mobileMenuOpen ? '✕' : '☰'}
        </button>

        {/* Navigation Links */}
        <nav className={`navbar-nav ${mobileMenuOpen ? 'navbar-nav-open' : ''}`}>
          <NavLink to="/" end className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`} onClick={closeMenu}>
            Home
          </NavLink>
          <NavLink to="/donors" className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`} onClick={closeMenu}>
            Find Donors
          </NavLink>
          <NavLink to="/register-donor" className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`} onClick={closeMenu}>
            Become a Donor
          </NavLink>
          <NavLink to="/request-blood" className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`} onClick={closeMenu}>
            Request Blood
          </NavLink>
          <NavLink to="/about" className={({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`} onClick={closeMenu}>
            How It Works
          </NavLink>

          <div className="nav-auth-section">
            {isAuthenticated ? (
              <div className="admin-nav-logged-in">
                <NavLink
                  to="/admin/dashboard"
                  className={({ isActive }) => `btn btn-sm btn-outline-admin ${isActive ? 'btn-admin-active' : ''}`}
                  onClick={closeMenu}
                >
                  Admin Dashboard ({adminUser?.username})
                </NavLink>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-danger"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </div>
            ) : (
              <NavLink
                to="/admin/login"
                className={({ isActive }) => `btn btn-sm btn-outline-admin ${isActive ? 'btn-admin-active' : ''}`}
                onClick={closeMenu}
              >
                Admin Portal
              </NavLink>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
}
