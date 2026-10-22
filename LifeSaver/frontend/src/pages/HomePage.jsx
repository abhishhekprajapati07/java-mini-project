import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import MedicalDisclaimer from '../components/MedicalDisclaimer';

export default function HomePage() {
  const [stats, setStats] = useState(null);
  const [loadingStats, setLoadingStats] = useState(true);

  useEffect(() => {
    async function fetchStats() {
      try {
        const data = await api.getStats();
        setStats(data);
      } catch (err) {
        console.warn('Could not fetch real-time stats:', err.message);
      } finally {
        setLoadingStats(false);
      }
    }
    fetchStats();
  }, []);

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="hero-pill">
            <span className="pill-dot"></span> Voluntary Blood Donation Network
          </div>
          <h1 className="hero-title">
            Every Drop Counts.<br />
            <span className="hero-title-highlight">Be a LifeSaver Today.</span>
          </h1>
          <p className="hero-description">
            Connecting altruistic voluntary blood donors with patients and hospitals facing urgent medical needs.
            Join our transparent, community-driven database or quickly locate available donors in your city.
          </p>

          <div className="hero-cta-group">
            <Link to="/donors" className="btn btn-primary btn-lg">
              🔍 Find Donors
            </Link>
            <Link to="/register-donor" className="btn btn-secondary btn-lg">
              🩸 Become a Donor
            </Link>
            <Link to="/request-blood" className="btn btn-outline btn-lg">
              🚑 Request Blood
            </Link>
          </div>
        </div>

        <div className="hero-graphic-card">
          <div className="blood-drop-emblem-large">
            <span className="emblem-symbol">🩸</span>
          </div>
          <div className="hero-card-meta">
            <h3>Safe & Voluntary</h3>
            <p>1 donation can save up to 3 lives.</p>
          </div>
        </div>
      </section>

      {/* Mandatory Medical Disclaimer Banner */}
      <section className="disclaimer-container">
        <MedicalDisclaimer />
      </section>

      {/* Real-time Statistics Strip Calculated from Database */}
      <section className="stats-section">
        <div className="stats-header">
          <h2 className="section-title">Live Database Statistics</h2>
          <p className="section-subtitle">Real-time metrics computed directly from our MySQL database</p>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon-wrap stat-icon-donors">👥</div>
            <div className="stat-number">
              {loadingStats ? '...' : (stats?.totalDonors ?? 0)}
            </div>
            <div className="stat-label">Registered Donors</div>
          </div>

          <div className="stat-card">
            <div className="stat-icon-wrap stat-icon-available">🟢</div>
            <div className="stat-number">
              {loadingStats ? '...' : (stats?.availableDonors ?? 0)}
            </div>
            <div className="stat-label">Available Right Now</div>
          </div>

          <div className="stat-card">
            <div className="stat-icon-wrap stat-icon-requests">📋</div>
            <div className="stat-number">
              {loadingStats ? '...' : (stats?.totalRequests ?? 0)}
            </div>
            <div className="stat-label">Blood Requests Raised</div>
          </div>

          <div className="stat-card">
            <div className="stat-icon-wrap stat-icon-fulfilled">💖</div>
            <div className="stat-number">
              {loadingStats ? '...' : (stats?.fulfilledRequests ?? 0)}
            </div>
            <div className="stat-label">Fulfilled Requirements</div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="how-it-works-section">
        <div className="text-center">
          <h2 className="section-title">How LifeSaver Works</h2>
          <p className="section-subtitle">A simple, transparent 3-step voluntary coordination process</p>
        </div>

        <div className="steps-grid">
          <div className="step-card">
            <div className="step-number">1</div>
            <div className="step-icon">📝</div>
            <h3 className="step-title">Register or Search</h3>
            <p className="step-desc">
              Volunteers register their blood group and city. Patients and caregivers search active donors by blood type and location.
            </p>
          </div>

          <div className="step-card">
            <div className="step-number">2</div>
            <div className="step-icon">🏥</div>
            <h3 className="step-title">Hospital Crossmatching</h3>
            <p className="step-desc">
              Hospitals and medical coordinators verify patient blood requirements, perform official crossmatching, and check donor eligibility.
            </p>
          </div>

          <div className="step-card">
            <div className="step-number">3</div>
            <div className="step-icon">🤝</div>
            <h3 className="step-title">Safe Donation</h3>
            <p className="step-desc">
              Eligible donors visit accredited blood banks, donate under certified supervision, and help save lives in their local communities.
            </p>
          </div>
        </div>
      </section>

      {/* Blood Group Compatibility Educational Chart */}
      <section className="compatibility-section">
        <div className="compatibility-header">
          <h2 className="section-title">Blood Group Compatibility Reference</h2>
          <p className="section-subtitle">
            Educational guide to ABO and Rh blood compatibility. Note that certified clinical laboratory crossmatching is always required prior to transfusion.
          </p>
        </div>

        <div className="table-responsive">
          <table className="compatibility-table">
            <thead>
              <tr>
                <th>Blood Type</th>
                <th>Can Donate To</th>
                <th>Can Receive From</th>
                <th>Special Trait</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><span className="table-badge table-badge-red">O-</span></td>
                <td>Everyone (All Blood Types)</td>
                <td>O- only</td>
                <td><strong>Universal Red Cell Donor</strong></td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">O+</span></td>
                <td>O+, A+, B+, AB+</td>
                <td>O+, O-</td>
                <td>Most Common Blood Group</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">A-</span></td>
                <td>A-, A+, AB-, AB+</td>
                <td>A-, O-</td>
                <td>Rare Rh-Negative Group</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">A+</span></td>
                <td>A+, AB+</td>
                <td>A+, A-, O+, O-</td>
                <td>High Clinical Demand</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">B-</span></td>
                <td>B-, B+, AB-, AB+</td>
                <td>B-, O-</td>
                <td>Rare Rh-Negative Group</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">B+</span></td>
                <td>B+, AB+</td>
                <td>B+, B-, O+, O-</td>
                <td>Common in South Asia</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">AB-</span></td>
                <td>AB-, AB+</td>
                <td>AB-, A-, B-, O-</td>
                <td>Rarest Blood Type</td>
              </tr>
              <tr>
                <td><span className="table-badge table-badge-red">AB+</span></td>
                <td>AB+ only</td>
                <td>Everyone (All Blood Types)</td>
                <td><strong>Universal Red Cell Recipient</strong></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      {/* Urgent Callout CTA Banner */}
      <section className="cta-banner">
        <div className="cta-banner-content">
          <h2>Have an Immediate Blood Requirement?</h2>
          <p>Submit an emergency request to notify administrators and coordinators in your area.</p>
        </div>
        <div className="cta-banner-action">
          <Link to="/request-blood" className="btn btn-light btn-lg">
            Submit Blood Request →
          </Link>
        </div>
      </section>
    </div>
  );
}
