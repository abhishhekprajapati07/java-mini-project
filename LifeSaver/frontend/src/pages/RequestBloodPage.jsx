import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import api from '../api/client';
import Alert from '../components/Alert';
import MedicalDisclaimer from '../components/MedicalDisclaimer';

const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
const URGENCY_LEVELS = [
  { value: 'NORMAL', label: 'Normal (Elective Surgery / Planned Need)' },
  { value: 'URGENT', label: 'Urgent (Required within 24 Hours)' },
  { value: 'CRITICAL', label: 'Critical (Immediate ICU / Emergency Trauma)' },
];

const getTodayDateString = () => new Date().toISOString().split('T')[0];

export default function RequestBloodPage() {
  const [searchParams] = useSearchParams();

  const [formData, setFormData] = useState(() => ({
    requesterName: '',
    contactNumber: '',
    patientBloodGroup: searchParams.get('bloodGroup') || 'O+',
    hospitalName: '',
    city: searchParams.get('city') || '',
    unitsRequested: 1,
    urgency: 'NORMAL',
    notes: '',
    requestDate: getTodayDateString(),
  }));

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  useEffect(() => {
    const bg = searchParams.get('bloodGroup');
    const ct = searchParams.get('city');
    if (bg || ct) {
      setFormData((prev) => ({
        ...prev,
        patientBloodGroup: bg || prev.patientBloodGroup,
        city: ct || prev.city,
      }));
    }
  }, [searchParams]);

  const validate = () => {
    const errs = {};
    if (!formData.requesterName.trim() || formData.requesterName.trim().length < 2) {
      errs.requesterName = 'Please enter requester or coordinator name';
    }

    const cleanPhone = formData.contactNumber.replace(/[\s\-()]/g, '');
    const phoneRegex = /^(\+?\d{1,4})?\d{10}$/;
    if (!formData.contactNumber.trim() || !phoneRegex.test(cleanPhone)) {
      errs.contactNumber = 'Please enter a valid 10-digit contact number';
    }

    if (!formData.patientBloodGroup) {
      errs.patientBloodGroup = 'Please select the required blood group';
    }

    if (!formData.hospitalName.trim() || formData.hospitalName.trim().length < 2) {
      errs.hospitalName = 'Please enter the hospital or medical center name';
    }

    if (!formData.city.trim() || formData.city.trim().length < 2) {
      errs.city = 'Please enter city';
    }

    const units = parseInt(formData.unitsRequested, 10);
    if (isNaN(units) || units < 1 || units > 50) {
      errs.unitsRequested = 'Units requested must be between 1 and 50';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
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
      const res = await api.createBloodRequest({
        requesterName: formData.requesterName.trim(),
        contactNumber: formData.contactNumber.trim(),
        patientBloodGroup: formData.patientBloodGroup,
        hospitalName: formData.hospitalName.trim(),
        city: formData.city.trim(),
        unitsRequested: parseInt(formData.unitsRequested, 10),
        urgency: formData.urgency,
        notes: formData.notes.trim() || null,
        requestDate: formData.requestDate,
      });

      setSuccessMessage(
        res.message || 'Blood request submitted successfully. It has been marked as unverified (PENDING) until reviewed.'
      );
      setFormData({
        requesterName: '',
        contactNumber: '',
        patientBloodGroup: 'O+',
        hospitalName: '',
        city: '',
        unitsRequested: 1,
        urgency: 'NORMAL',
        notes: '',
        requestDate: new Date().toISOString().split('T')[0],
      });
    } catch (err) {
      console.error('Request creation failed:', err);
      setServerError(err.message || 'Failed to submit blood request. Please check inputs.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="form-page page-container">
      <div className="form-card-wrapper">
        <div className="page-header text-center">
          <div className="form-badge form-badge-emergency">🚑 Emergency & Clinical Requirement</div>
          <h1 className="page-title">Submit a Blood Request</h1>
          <p className="page-subtitle">
            Post an urgent blood requirement for patient transfusion support at local hospitals.
          </p>
        </div>

        <MedicalDisclaimer compact />

        <div className="unverified-request-notice" role="note">
          ⚠️ <strong>Verification Notice:</strong> All blood requests submitted through this form are labeled as <em>Unverified (PENDING)</em> until hospital blood bank staff or system administrators verify the clinical requirement.
        </div>

        {serverError && (
          <Alert type="error" message={serverError} onDismiss={() => setServerError(null)} />
        )}

        {successMessage ? (
          <div className="success-confirmation-card">
            <div className="success-icon">🏥</div>
            <h2>Blood Request Submitted</h2>
            <p>{successMessage}</p>
            <p className="success-sub">
              Your request is now logged in the system. Hospital coordinators and voluntary donors in {formData.city} can review the requirement.
            </p>
            <div className="success-actions">
              <Link to="/donors" className="btn btn-primary">
                Search Matching Donors Now →
              </Link>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setSuccessMessage(null)}
              >
                Submit Another Request
              </button>
            </div>
          </div>
        ) : (
          <div className="form-card">
            <form onSubmit={handleSubmit} noValidate>
              <div className="form-row">
                {/* Requester Name */}
                <div className="form-group flex-1">
                  <label htmlFor="req-name" className="form-label required">
                    Requester / Attendant Name
                  </label>
                  <input
                    id="req-name"
                    name="requesterName"
                    type="text"
                    className={`form-control ${errors.requesterName ? 'is-invalid' : ''}`}
                    placeholder="e.g. Dr. Ramesh Gupta or Suman Sen"
                    value={formData.requesterName}
                    onChange={handleChange}
                    required
                  />
                  {errors.requesterName && <div className="error-text">{errors.requesterName}</div>}
                </div>

                {/* Contact Number */}
                <div className="form-group flex-1">
                  <label htmlFor="req-phone" className="form-label required">
                    Contact Mobile Number
                  </label>
                  <input
                    id="req-phone"
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
              </div>

              <div className="form-row">
                {/* Patient Blood Group */}
                <div className="form-group flex-1">
                  <label htmlFor="req-bg" className="form-label required">
                    Required Patient Blood Group
                  </label>
                  <select
                    id="req-bg"
                    name="patientBloodGroup"
                    className={`form-control ${errors.patientBloodGroup ? 'is-invalid' : ''}`}
                    value={formData.patientBloodGroup}
                    onChange={handleChange}
                    required
                  >
                    {BLOOD_GROUPS.map((bg) => (
                      <option key={bg} value={bg}>
                        {bg} (ABO / Rh Type)
                      </option>
                    ))}
                  </select>
                  {errors.patientBloodGroup && <div className="error-text">{errors.patientBloodGroup}</div>}
                </div>

                {/* Units Requested */}
                <div className="form-group flex-1">
                  <label htmlFor="req-units" className="form-label required">
                    Units of Blood Needed
                  </label>
                  <input
                    id="req-units"
                    name="unitsRequested"
                    type="number"
                    min="1"
                    max="50"
                    className={`form-control ${errors.unitsRequested ? 'is-invalid' : ''}`}
                    value={formData.unitsRequested}
                    onChange={handleChange}
                    required
                  />
                  {errors.unitsRequested && <div className="error-text">{errors.unitsRequested}</div>}
                </div>
              </div>

              <div className="form-row">
                {/* Hospital Name */}
                <div className="form-group flex-1">
                  <label htmlFor="req-hospital" className="form-label required">
                    Hospital / Blood Bank Name
                  </label>
                  <input
                    id="req-hospital"
                    name="hospitalName"
                    type="text"
                    className={`form-control ${errors.hospitalName ? 'is-invalid' : ''}`}
                    placeholder="e.g. Apollo Super Speciality, Ward 3"
                    value={formData.hospitalName}
                    onChange={handleChange}
                    required
                  />
                  {errors.hospitalName && <div className="error-text">{errors.hospitalName}</div>}
                </div>

                {/* City */}
                <div className="form-group flex-1">
                  <label htmlFor="req-city" className="form-label required">
                    City / Hospital Location
                  </label>
                  <input
                    id="req-city"
                    name="city"
                    type="text"
                    className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                    placeholder="e.g. Pune"
                    value={formData.city}
                    onChange={handleChange}
                    required
                  />
                  {errors.city && <div className="error-text">{errors.city}</div>}
                </div>
              </div>

              <div className="form-row">
                {/* Urgency Level */}
                <div className="form-group flex-1">
                  <label htmlFor="req-urgency" className="form-label required">
                    Urgency Priority
                  </label>
                  <select
                    id="req-urgency"
                    name="urgency"
                    className="form-control"
                    value={formData.urgency}
                    onChange={handleChange}
                  >
                    {URGENCY_LEVELS.map((lvl) => (
                      <option key={lvl.value} value={lvl.value}>
                        {lvl.label}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Request Date */}
                <div className="form-group flex-1">
                  <label htmlFor="req-date" className="form-label required">
                    Date of Requirement
                  </label>
                  <input
                    id="req-date"
                    name="requestDate"
                    type="date"
                    className="form-control"
                    value={formData.requestDate}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* Notes */}
              <div className="form-group">
                <label htmlFor="req-notes" className="form-label">
                  Additional Notes / Case Details (Optional)
                </label>
                <textarea
                  id="req-notes"
                  name="notes"
                  rows="3"
                  className="form-control"
                  placeholder="e.g. Pre-operative bypass reserve, Bed number 14, ICU block B..."
                  value={formData.notes}
                  onChange={handleChange}
                ></textarea>
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="btn btn-primary btn-lg btn-block"
                  disabled={submitting}
                >
                  {submitting ? 'Submitting Blood Request...' : 'Submit Blood Request →'}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
