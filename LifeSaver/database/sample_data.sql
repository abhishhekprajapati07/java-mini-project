-- ============================================================================
-- LifeSaver - Blood Donation and Donor Management System
-- Sample Test Data (Clearly marked fictional demo data)
-- ============================================================================
-- NOTE: All donor records, contact numbers, and patient requests below are
-- completely FICTIONAL test entries created for educational demonstration only.
-- Real medical service requires verification by certified hospital staff.
-- ============================================================================

USE lifesaver_db;

-- ----------------------------------------------------------------------------
-- 1. Default Administrator Account
-- Username: admin
-- Password: AdminPassword@123 (Hashed via PBKDF2WithHmacSHA256, 65,536 rounds)
-- ----------------------------------------------------------------------------
INSERT INTO admin_users (id, username, email, password_hash, password_salt, full_name, role)
VALUES (
    1,
    'admin',
    'admin@lifesaver.demo',
    'b0ab6c63d5f30863aaf0d4c5ce4cce6787dd857acefcd60c4d8340f94670baf1',
    'db7cae2b68e37fec58a68d1cbe509633',
    'System Administrator',
    'ADMIN'
) ON DUPLICATE KEY UPDATE id=id;

-- ----------------------------------------------------------------------------
-- 2. Fictional Donor Records (Demonstrates all 8 blood groups across cities)
-- ----------------------------------------------------------------------------
INSERT INTO donors (id, full_name, email, contact_number, blood_group, city, area, is_available) VALUES
(1, 'Aarav Sharma', 'aarav.sharma@demo.mail', '+91 98765 01001', 'O+', 'Delhi', 'Connaught Place', TRUE),
(2, 'Priya Patel', 'priya.patel@demo.mail', '+91 98765 01002', 'A+', 'Mumbai', 'Andheri West', TRUE),
(3, 'Rohan Iyer', 'rohan.iyer@demo.mail', '+91 98765 01003', 'B+', 'Bengaluru', 'Indiranagar', TRUE),
(4, 'Ananya Gupta', 'ananya.gupta@demo.mail', '+91 98765 01004', 'AB+', 'Pune', 'Kothrud', TRUE),
(5, 'Karthik Reddy', 'karthik.reddy@demo.mail', '+91 98765 01005', 'O-', 'Hyderabad', 'Banjara Hills', TRUE),
(6, 'Sneha Nair', 'sneha.nair@demo.mail', '+91 98765 01006', 'A-', 'Chennai', 'Adyar', FALSE),
(7, 'Vikram Das', 'vikram.das@demo.mail', '+91 98765 01007', 'B-', 'Kolkata', 'Salt Lake', TRUE),
(8, 'Deepa Mehta', 'deepa.mehta@demo.mail', '+91 98765 01008', 'AB-', 'Ahmedabad', 'Navrangpura', TRUE),
(9, 'Rahul Verma', 'rahul.verma@demo.mail', '+91 98765 01009', 'O+', 'Jaipur', 'Malviya Nagar', TRUE),
(10, 'Pooja Kulkarni', 'pooja.kulkarni@demo.mail', '+91 98765 01010', 'B+', 'Mumbai', 'Bandra East', TRUE)
ON DUPLICATE KEY UPDATE id=id;

-- ----------------------------------------------------------------------------
-- 3. Fictional Blood Requests (Various urgency levels and statuses)
-- ----------------------------------------------------------------------------
INSERT INTO blood_requests (id, requester_name, contact_number, patient_blood_group, hospital_name, city, units_requested, urgency, status, notes, request_date) VALUES
(1, 'Dr. Sandeep Rao', '+91 98765 92001', 'O-', 'City Care Super Speciality Hospital', 'Delhi', 2, 'CRITICAL', 'VERIFIED', 'Emergency surgery requirement in ICU Ward 4', CURDATE() - INTERVAL 1 DAY),
(2, 'Meera Joshi', '+91 98765 92002', 'A+', 'Apex General Hospital', 'Mumbai', 1, 'NORMAL', 'PENDING', 'Scheduled thalassemia transfusion support', CURDATE()),
(3, 'Ramesh Kumar', '+91 98765 92003', 'B+', 'Fortis Life Clinic', 'Bengaluru', 3, 'URGENT', 'FULFILLED', 'Trauma care unit patient, units received from community donors', CURDATE() - INTERVAL 3 DAY),
(4, 'Sunita Deshmukh', '+91 98765 92004', 'AB+', 'Ruby Hall Medical Center', 'Pune', 1, 'NORMAL', 'PENDING', 'Dialysis support requirement', CURDATE()),
(5, 'Dr. Farooq Ali', '+91 98765 92005', 'O+', 'Care Hospitals', 'Hyderabad', 2, 'URGENT', 'VERIFIED', 'Cardiac bypass surgical reserve', CURDATE() - INTERVAL 2 DAY)
ON DUPLICATE KEY UPDATE id=id;
