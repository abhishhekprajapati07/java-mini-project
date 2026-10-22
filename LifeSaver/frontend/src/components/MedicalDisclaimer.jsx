import React from 'react';

/**
 * MedicalDisclaimer Component
 * Required medical safety notice reminding users that this is an educational mini project,
 * not a hospital service, and blood compatibility must be certified by medical staff.
 */
export default function MedicalDisclaimer({ compact = false }) {
  if (compact) {
    return (
      <div className="medical-banner medical-banner-compact" role="alert">
        <span className="medical-icon">⚕️</span>
        <p>
          <strong>Medical Notice:</strong> Blood group compatibility and donor eligibility must be verified by certified hospital blood bank staff prior to donation or transfusion.
        </p>
      </div>
    );
  }

  return (
    <div className="medical-banner" role="alert">
      <div className="medical-banner-inner">
        <div className="medical-icon-wrap">
          <span className="medical-icon">⚕️</span>
        </div>
        <div className="medical-content">
          <h4>Important Medical & Educational Disclaimer</h4>
          <p>
            <strong>LifeSaver</strong> is an educational college mini project designed to demonstrate full-stack Java (Spring Boot, React, JDBC, and MySQL).
            A matching blood group recorded here <em>never guarantees</em> biological compatibility or donation eligibility.
            Certified medical specialists must perform crossmatching, antibody screening, and donor health verification at an accredited blood bank prior to any transfusion.
          </p>
        </div>
      </div>
    </div>
  );
}
