import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import Alert from '../components/Alert';
import MedicalDisclaimer from '../components/MedicalDisclaimer';

const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

export default function RegisterDonorPage() {

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    contactNumber: '',
    bloodGroup: 'O+',
    city: '',
    area: '',
    available: true,
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const validate = () => {
    const errs = {};
    if (!formData.fullName.trim() || formData.fullName.trim().length < 2) {
      errs.fullName = 'Please enter your full name (at least 2 characters)';
    }

    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,10}$/;
    if (!formData.email.trim() || !emailRegex.test(formData.email.trim())) {
      errs.email = 'Please provide a valid email address';
    }

    const cleanPhone = formData.contactNumber.replace(/[\s\-()]/g, '');
    const phoneRegex = /^(\+?\d{1,4})?\d{10}$/;
    if (!formData.contactNumber.trim() || !phoneRegex.test(cleanPhone)) {
      errs.contactNumber = 'Please enter a valid 10-digit mobile number';
    }

    if (!formData.bloodGroup) {
      errs.bloodGroup = 'Please select your blood group';
    }

    if (!formData.city.trim() || formData.city.trim().length < 2) {
      errs.city = 'Please enter your city / town';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
    // Clear field-specific error as user types
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError(null);
    setSuccessMessage(null);

    if (!validate()) {
      return;
    }

    setSubmitting(true);
    try {
      const res = await api.registerDonor({
        fullName: formData.fullName.trim(),
        email: formData.email.trim().toLowerCase(),
        contactNumber: formData.contactNumber.trim(),
        bloodGroup: formData.bloodGroup,
        city: formData.city.trim(),
        area: formData.area.trim() || null,
        available: formData.available,
      });

      setSuccessMessage(res.message || 'Thank you for registering as a voluntary blood donor!');
      setFormData({
        fullName: '',
        email: '',
        contactNumber: '',
        bloodGroup: 'O+',
        city: '',
        area: '',
        available: true,
      });
    } catch (err) {
      console.error('Registration failed:', err);
      setServerError(err.message || 'Registration failed. Please check your inputs or try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="form-page page-container">
      <div className="form-card-wrapper">
        <div className="page-header text-center">
          <div className="form-badge">🩸 Voluntary Donor Registration</div>
          <h1 className="page-title">Register as a Blood Donor</h1>
          <p className="page-subtitle">
            Sign up to be listed as a voluntary donor. Your contact details remain privacy-masked for public visitors.
          </p>
        </div>

        <MedicalDisclaimer compact />

        {serverError && (
          <Alert type="error" message={serverError} onDismiss={() => setServerError(null)} />
        )}

        {successMessage ? (
          <div className="success-confirmation-card">
            <div className="success-icon">🎉</div>
            <h2>Registration Successful!</h2>
            <p>{successMessage}</p>
            <p className="success-sub">
              Your profile is now active. Voluntary coordinators and hospital staff in need of {formData.bloodGroup} in your area will be able to connect with you.
            </p>
            <div className="success-actions">
              <Link to="/donors" className="btn btn-primary">
                View Donor Directory →
              </Link>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setSuccessMessage(null)}
              >
                Register Another Volunteer
              </button>
            </div>
          </div>
        ) : (
          <div className="form-card">
            <form onSubmit={handleSubmit} noValidate>
              <div className="form-row">
                {/* Full Name */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-fullname" className="form-label required">
                    Full Name
                  </label>
                  <input
                    id="reg-fullname"
                    name="fullName"
                    type="text"
                    className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
                    placeholder="e.g. Rahul Sharma"
                    value={formData.fullName}
                    onChange={handleChange}
                    required
                  />
                  {errors.fullName && <div className="error-text">{errors.fullName}</div>}
                </div>

                {/* Email Address */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-email" className="form-label required">
                    Email Address
                  </label>
                  <input
                    id="reg-email"
                    name="email"
                    type="email"
                    className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                    placeholder="e.g. rahul@example.com"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                  {errors.email && <div className="error-text">{errors.email}</div>}
                </div>
              </div>

              <div className="form-row">
                {/* Contact Number */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-phone" className="form-label required">
                    10-Digit Mobile Number
                  </label>
                  <input
                    id="reg-phone"
                    name="contactNumber"
                    type="tel"
                    className={`form-control ${errors.contactNumber ? 'is-invalid' : ''}`}
                    placeholder="e.g. 9876543210"
                    value={formData.contactNumber}
                    onChange={handleChange}
                    required
                  />
                  {errors.contactNumber && <div className="error-text">{errors.contactNumber}</div>}
                </div>

                {/* Blood Group */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-bg" className="form-label required">
                    Blood Group
                  </label>
                  <select
                    id="reg-bg"
                    name="bloodGroup"
                    className={`form-control ${errors.bloodGroup ? 'is-invalid' : ''}`}
                    value={formData.bloodGroup}
                    onChange={handleChange}
                    required
                  >
                    {BLOOD_GROUPS.map((bg) => (
                      <option key={bg} value={bg}>
                        {bg} (ABO / Rh Type)
                      </option>
                    ))}
                  </select>
                  {errors.bloodGroup && <div className="error-text">{errors.bloodGroup}</div>}
                </div>
              </div>

              <div className="form-row">
                {/* City */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-city" className="form-label required">
                    City / Town
                  </label>
                  <input
                    id="reg-city"
                    name="city"
                    type="text"
                    className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                    placeholder="e.g. Bengaluru"
                    value={formData.city}
                    onChange={handleChange}
                    required
                  />
                  {errors.city && <div className="error-text">{errors.city}</div>}
                </div>

                {/* Area */}
                <div className="form-group flex-1">
                  <label htmlFor="reg-area" className="form-label">
                    Locality / Area (Optional)
                  </label>
                  <input
                    id="reg-area"
                    name="area"
                    type="text"
                    className="form-control"
                    placeholder="e.g. Koramangala"
                    value={formData.area}
                    onChange={handleChange}
                  />
                </div>
              </div>

              {/* Availability Checkbox */}
              <div className="form-check-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    name="available"
                    checked={formData.available}
                    onChange={handleChange}
                  />
                  <span>
                    <strong>I am currently available and willing to donate blood.</strong>
                    <br />
                    <small className="text-muted">
                      You can change this availability setting at any time if you are temporarily unavailable.
                    </small>
                  </span>
                </label>
              </div>

              {/* Privacy Notice */}
              <div className="form-privacy-box">
                🔒 <strong>Privacy Assurance:</strong> LifeSaver only collects minimum contact details needed for voluntary donation coordination. We never ask for or store sensitive medical records.
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="btn btn-primary btn-lg btn-block"
                  disabled={submitting}
                >
                  {submitting ? 'Registering Donor...' : 'Submit Voluntary Registration →'}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
