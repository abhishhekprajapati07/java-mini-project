import React from 'react';
import { Link } from 'react-router-dom';
import MedicalDisclaimer from '../components/MedicalDisclaimer';

export default function AboutPage() {
  return (
    <div className="about-page page-container">
      <div className="page-header text-center">
        <h1 className="page-title">How Blood Donation Works</h1>
        <p className="page-subtitle">
          Essential facts, voluntary donor eligibility criteria, and academic architectural overview of LifeSaver.
        </p>
      </div>

      <MedicalDisclaimer />

      {/* Eligibility Grid */}
      <section className="about-card-section">
        <h2 className="section-title">Donor Eligibility Guidelines</h2>
        <div className="eligibility-grid">
          <div className="eligibility-card eligible">
            <div className="card-header-badge">✓ Eligible Criteria</div>
            <h3>Who Can Donate?</h3>
            <ul>
              <li><strong>Age:</strong> Between 18 and 65 years old.</li>
              <li><strong>Weight:</strong> At least 45 kg (50 kg for double red cell donations).</li>
              <li><strong>Hemoglobin:</strong> Minimum 12.5 g/dL measured at the blood bank.</li>
              <li><strong>Pulse & Blood Pressure:</strong> Normal resting pulse (60-100 bpm) and normal BP.</li>
              <li><strong>Donation Interval:</strong> At least 12 weeks (3 months) since the last whole blood donation for males, 16 weeks for females.</li>
              <li><strong>General Health:</strong> Feeling well, well-rested, and well-hydrated on the day of donation.</li>
            </ul>
          </div>

          <div className="eligibility-card deferral">
            <div className="card-header-badge">⚠️ Temporary Deferrals</div>
            <h3>When to Defer Donation?</h3>
            <ul>
              <li><strong>Recent Infection:</strong> Active cold, flu, sore throat, or fever in the past 7 days.</li>
              <li><strong>Medications:</strong> Currently taking certain antibiotics or blood thinners.</li>
              <li><strong>Tattoo / Piercing:</strong> Undergone tattooing, body piercing, or dental surgery within the last 6 months.</li>
              <li><strong>Vaccination:</strong> Specific recent immunizations require a 14 to 28-day waiting period.</li>
              <li><strong>Alcohol:</strong> Consumption of alcohol within 24 hours prior to donation.</li>
              <li><strong>Pregnancy / Lactation:</strong> Deferred during pregnancy and until 6 months after delivery/lactation.</li>
            </ul>
          </div>
        </div>
      </section>

      {/* Donation Process Timeline */}
      <section className="about-card-section">
        <h2 className="section-title">The 4 Stages of Donation</h2>
        <div className="process-timeline">
          <div className="timeline-item">
            <div className="timeline-badge">1</div>
            <div className="timeline-content">
              <h4>Registration & Health History</h4>
              <p>You present identification, verify voluntary consent, and fill out a confidential medical questionnaire.</p>
            </div>
          </div>

          <div className="timeline-item">
            <div className="timeline-badge">2</div>
            <div className="timeline-content">
              <h4>Mini Physical Examination</h4>
              <p>A trained nurse checks your hemoglobin level, pulse, blood pressure, and temperature to confirm eligibility.</p>
            </div>
          </div>

          <div className="timeline-item">
            <div className="timeline-badge">3</div>
            <div className="timeline-content">
              <h4>The Blood Donation (8-10 Minutes)</h4>
              <p>You relax comfortably while approximately 350-450 mL of blood is collected using single-use, sterile equipment.</p>
            </div>
          </div>

          <div className="timeline-item">
            <div className="timeline-badge">4</div>
            <div className="timeline-content">
              <h4>Rest & Refreshment (15 Minutes)</h4>
              <p>You enjoy juice and a light snack while your body begins replenishing lost fluids before heading home.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Academic Project Architecture Section */}
      <section className="about-tech-section">
        <h2 className="section-title">Academic Mini Project Architecture</h2>
        <p className="section-subtitle">
          LifeSaver is engineered strictly according to the college Full-Stack Java syllabus without third-party shortcuts.
        </p>

        <div className="tech-cards-grid">
          <div className="tech-spec-card">
            <div className="tech-spec-icon">☕</div>
            <h3>Backend (Java Spring Boot)</h3>
            <p>
              Monolithic Spring Boot application organized in strict MVC architecture: REST Controllers, Service Layer interfaces, and pure JDBC DAO repositories.
            </p>
          </div>

          <div className="tech-spec-card">
            <div className="tech-spec-icon">🗄️</div>
            <h3>Database (MySQL & JDBC)</h3>
            <p>
              Relational tables (<code>donors</code>, <code>blood_requests</code>, <code>admin_users</code>) managed strictly with JDBC <code>PreparedStatement</code> to prevent SQL injection. Zero JPA/Hibernate.
            </p>
          </div>

          <div className="tech-spec-card">
            <div className="tech-spec-icon">⚛️</div>
            <h3>Frontend (React SPA)</h3>
            <p>
              Modern React 18 Single-Page Application using functional components, custom hooks, React Router, controlled forms, and pure vanilla CSS3.
            </p>
          </div>

          <div className="tech-spec-card">
            <div className="tech-spec-icon">🔐</div>
            <h3>Security & Hashing</h3>
            <p>
              Server-side session management with <code>LIFESAVER_SESSION</code> HTTP cookies and Java built-in <code>PBKDF2WithHmacSHA256</code> (65,536 rounds) cryptographic password hashing.
            </p>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <div className="about-cta-card">
        <h2>Ready to Make an Impact?</h2>
        <p>Register as a voluntary donor or find compatible donors in your hospital network.</p>
        <div className="about-cta-buttons">
          <Link to="/register-donor" className="btn btn-primary btn-lg">Become a Donor</Link>
          <Link to="/donors" className="btn btn-secondary btn-lg">Find Donors</Link>
        </div>
      </div>
    </div>
  );
}
