import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import Modal from './Modal';

/**
 * DonorCard Component
 * Displays donor summary with masked contact number, blood group badge, and contact modal.
 */
export default function DonorCard({ donor, isAdmin = false, onToggleAvailability, onDelete, onEdit }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [revealedContact, setRevealedContact] = useState(false);
  const navigate = useNavigate();

  const handleOpenModal = () => {
    setIsModalOpen(true);
    setRevealedContact(false);
  };

  const handleRequestRedirect = () => {
    setIsModalOpen(false);
    navigate(`/request-blood?bloodGroup=${encodeURIComponent(donor.bloodGroup)}&city=${encodeURIComponent(donor.city)}`);
  };

  return (
    <>
      <div className={`donor-card ${!donor.available ? 'donor-card-inactive' : ''}`}>
        <div className="donor-card-header">
          <div className="blood-group-badge">
            <span className="blood-drop-icon">🩸</span>
            <span className="blood-group-text">{donor.bloodGroup}</span>
          </div>
          <StatusBadge status={donor.available} type="availability" />
        </div>

        <div className="donor-card-body">
          <h3 className="donor-name">{donor.fullName}</h3>
          <p className="donor-location">
            <span className="location-icon">📍</span>
            <strong>{donor.city}</strong>
            {donor.area ? `, ${donor.area}` : ''}
          </p>

          <div className="donor-contact-preview">
            <span className="contact-label">Contact:</span>
            <span className="contact-value">{donor.contactNumber}</span>
            {donor.isContactMasked && (
              <span className="contact-privacy-pill">Privacy Protected</span>
            )}
          </div>
        </div>

        <div className="donor-card-footer">
          <button
            type="button"
            className="btn btn-primary btn-sm btn-full"
            onClick={handleOpenModal}
            aria-label={`Contact donor ${donor.fullName}`}
          >
            Request Contact
          </button>

          {isAdmin && (
            <div className="admin-card-actions">
              <button
                type="button"
                className={`btn btn-xs ${donor.available ? 'btn-outline-warning' : 'btn-outline-success'}`}
                onClick={() => onToggleAvailability && onToggleAvailability(donor.id, !donor.available)}
              >
                {donor.available ? 'Deactivate' : 'Activate'}
              </button>
              {onEdit && (
                <button
                  type="button"
                  className="btn btn-outline btn-xs"
                  onClick={() => onEdit(donor)}
                >
                  Edit
                </button>
              )}
              {onDelete && (
                <button
                  type="button"
                  className="btn btn-danger btn-xs"
                  onClick={() => onDelete(donor.id, donor.fullName)}
                >
                  Delete
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Controlled Contact & Demo Policy Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Donor Contact Protocol"
      >
        <div className="contact-modal-content">
          <div className="contact-donor-summary">
            <div className="donor-quick-pill">
              <span className="pill-bg">{donor.bloodGroup}</span>
              <div>
                <strong>{donor.fullName}</strong>
                <p>{donor.city}{donor.area ? ` (${donor.area})` : ''}</p>
              </div>
            </div>
          </div>

          <div className="medical-banner medical-banner-compact">
            <p>
              ⚕️ <strong>Patient Notice:</strong> A matching blood group does not guarantee biological compatibility or donation eligibility. Certified hospital blood bank staff must perform mandatory crossmatching and screening.
            </p>
          </div>

          <div className="contact-privacy-policy-box">
            <h4>Demo Contact Protection Policy</h4>
            <p>
              To protect registered voluntary donors from automated web scraping and unsolicited calls, LifeSaver masks full contact details for anonymous visitors.
            </p>

            {revealedContact ? (
              <div className="revealed-contact-box">
                <span className="revealed-label">Donor Demo Contact Number:</span>
                <span className="revealed-phone">{donor.contactNumber.replace(/\*/g, '7')}</span>
                <p className="revealed-note">
                  (Note: This is a fictional demo record for college evaluation. Please do not attempt real emergency calls to this number.)
                </p>
              </div>
            ) : (
              <div className="reveal-actions">
                <button
                  type="button"
                  className="btn btn-outline btn-sm"
                  onClick={() => setRevealedContact(true)}
                >
                  Show Demo Contact Details
                </button>
              </div>
            )}
          </div>

          <div className="modal-actions-row">
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleRequestRedirect}
            >
              Submit Official Blood Request for {donor.bloodGroup} in {donor.city} →
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsModalOpen(false)}
            >
              Close
            </button>
          </div>
        </div>
      </Modal>
    </>
  );
}
