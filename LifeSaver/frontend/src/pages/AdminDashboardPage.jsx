import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import LoadingSpinner from '../components/LoadingSpinner';

const BLOOD_GROUPS = ['ALL', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
const REQUEST_STATUSES = ['ALL', 'PENDING', 'VERIFIED', 'FULFILLED', 'REJECTED'];

export default function AdminDashboardPage() {
  const { adminUser, isAuthenticated, loading: authLoading, logout } = useAuth();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('requests'); // 'requests' or 'donors'

  // Global Statistics
  const [stats, setStats] = useState(null);
  const [statsLoading, setStatsLoading] = useState(true);

  // Blood Requests state
  const [requests, setRequests] = useState([]);
  const [reqStatusFilter, setReqStatusFilter] = useState('ALL');
  const [reqCityFilter, setReqCityFilter] = useState('');
  const [reqBgFilter, setReqBgFilter] = useState('ALL');
  const [reqPage, setReqPage] = useState(1);
  const [reqTotalPages, setReqTotalPages] = useState(1);
  const [requestsLoading, setRequestsLoading] = useState(false);

  // Donors state
  const [donors, setDonors] = useState([]);
  const [donorBgFilter, setDonorBgFilter] = useState('ALL');
  const [donorCityFilter, setDonorCityFilter] = useState('');
  const [donorPage, setDonorPage] = useState(1);
  const [donorTotalPages, setDonorTotalPages] = useState(1);
  const [donorsLoading, setDonorsLoading] = useState(false);

  // Modals
  const [statusModalReq, setStatusModalReq] = useState(null);
  const [newStatus, setNewStatus] = useState('VERIFIED');
  const [statusNotes, setStatusNotes] = useState('');

  const [deleteDonorTarget, setDeleteDonorTarget] = useState(null);
  const [editDonorTarget, setEditDonorTarget] = useState(null);
  const [editFormData, setEditFormData] = useState({
    fullName: '',
    contactNumber: '',
    bloodGroup: 'O+',
    city: '',
    area: '',
    available: true,
  });

  // Notifications
  const [alert, setAlert] = useState(null);

  // Authentication check
  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      navigate('/admin/login', { replace: true });
    }
  }, [isAuthenticated, authLoading, navigate]);

  // Fetch real-time stats
  const fetchStats = useCallback(async () => {
    setStatsLoading(true);
    try {
      const data = await api.getStats();
      setStats(data);
    } catch (err) {
      console.warn('Error fetching stats:', err);
    } finally {
      setStatsLoading(false);
    }
  }, []);

  // Fetch blood requests
  const fetchRequests = useCallback(async (page = 1) => {
    setRequestsLoading(true);
    try {
      const data = await api.listBloodRequests({
        status: reqStatusFilter === 'ALL' ? '' : reqStatusFilter,
        city: reqCityFilter.trim(),
        bloodGroup: reqBgFilter === 'ALL' ? '' : reqBgFilter,
        page,
        size: 10,
      });
      setRequests(data.content || []);
      setReqTotalPages(data.totalPages || 1);
      setReqPage(data.page || 1);
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to load blood requests' });
    } finally {
      setRequestsLoading(false);
    }
  }, [reqStatusFilter, reqCityFilter, reqBgFilter]);

  // Fetch donors
  const fetchDonors = useCallback(async (page = 1) => {
    setDonorsLoading(true);
    try {
      const data = await api.searchDonors({
        bloodGroup: donorBgFilter === 'ALL' ? '' : donorBgFilter,
        city: donorCityFilter.trim(),
        available: '',
        page,
        size: 10,
      });
      setDonors(data.content || []);
      setDonorTotalPages(data.totalPages || 1);
      setDonorPage(data.page || 1);
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to load donors' });
    } finally {
      setDonorsLoading(false);
    }
  }, [donorBgFilter, donorCityFilter]);

  useEffect(() => {
    if (isAuthenticated) {
      fetchStats();
    }
  }, [isAuthenticated, fetchStats]);

  useEffect(() => {
    if (isAuthenticated && activeTab === 'requests') {
      fetchRequests(1);
    }
  }, [isAuthenticated, activeTab, fetchRequests]);

  useEffect(() => {
    if (isAuthenticated && activeTab === 'donors') {
      fetchDonors(1);
    }
  }, [isAuthenticated, activeTab, fetchDonors]);

  // Handle request status update
  const handleOpenStatusModal = (req) => {
    setStatusModalReq(req);
    setNewStatus(req.status);
    setStatusNotes(req.notes || '');
  };

  const handleSaveStatus = async () => {
    if (!statusModalReq) return;
    try {
      const res = await api.updateBloodRequestStatus(statusModalReq.id, newStatus, statusNotes);
      setAlert({ type: 'success', message: res.message || 'Request status updated successfully' });
      setStatusModalReq(null);
      fetchRequests(reqPage);
      fetchStats();
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to update request status' });
    }
  };

  // Handle donor availability quick-toggle
  const handleToggleAvailability = async (id, currentAvail) => {
    try {
      const res = await api.updateDonorAvailability(id, !currentAvail);
      setAlert({ type: 'success', message: res.message || 'Donor availability updated' });
      fetchDonors(donorPage);
      fetchStats();
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to update availability' });
    }
  };

  // Handle donor edit
  const handleOpenEditDonor = (donor) => {
    setEditDonorTarget(donor);
    setEditFormData({
      fullName: donor.fullName,
      contactNumber: donor.contactNumber,
      bloodGroup: donor.bloodGroup,
      city: donor.city,
      area: donor.area || '',
      available: donor.available,
    });
  };

  const handleSaveEditDonor = async (e) => {
    e.preventDefault();
    if (!editDonorTarget) return;
    try {
      const res = await api.updateDonor(editDonorTarget.id, editFormData);
      setAlert({ type: 'success', message: res.message || 'Donor record updated' });
      setEditDonorTarget(null);
      fetchDonors(donorPage);
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to update donor' });
    }
  };

  // Handle donor deletion with confirmation
  const handleConfirmDeleteDonor = async () => {
    if (!deleteDonorTarget) return;
    try {
      const res = await api.deleteDonor(deleteDonorTarget.id);
      setAlert({ type: 'success', message: res.message || 'Donor record deleted' });
      setDeleteDonorTarget(null);
      fetchDonors(donorPage);
      fetchStats();
    } catch (err) {
      setAlert({ type: 'error', message: err.message || 'Failed to delete donor' });
    }
  };

  if (authLoading) {
    return <LoadingSpinner message="Verifying administrative session..." />;
  }

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="admin-dashboard-page page-container">
      {/* Dashboard Top Header */}
      <div className="dashboard-header-row">
        <div>
          <div className="admin-status-pill">
            <span className="pill-dot"></span> Administrator Session Active
          </div>
          <h1 className="page-title">LifeSaver Management Dashboard</h1>
          <p className="page-subtitle">
            Welcome, <strong>{adminUser?.fullName}</strong> ({adminUser?.username}) • Manage blood requests, verified records, and volunteer donors.
          </p>
        </div>

        <button
          type="button"
          className="btn btn-outline-danger"
          onClick={logout}
        >
          Sign Out
        </button>
      </div>

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onDismiss={() => setAlert(null)}
        />
      )}

      {/* Metrics Row */}
      <div className="admin-metrics-grid">
        <div className="metric-box">
          <div className="metric-label">Total Donors</div>
          <div className="metric-value">{statsLoading ? '...' : (stats?.totalDonors ?? 0)}</div>
          <div className="metric-sub">{stats?.availableDonors ?? 0} currently available</div>
        </div>

        <div className="metric-box">
          <div className="metric-label">Total Blood Requests</div>
          <div className="metric-value">{statsLoading ? '...' : (stats?.totalRequests ?? 0)}</div>
          <div className="metric-sub">{stats?.pendingRequests ?? 0} unverified / pending</div>
        </div>

        <div className="metric-box metric-box-warning">
          <div className="metric-label">Pending Verification</div>
          <div className="metric-value">{statsLoading ? '...' : (stats?.pendingRequests ?? 0)}</div>
          <div className="metric-sub">Requires hospital check</div>
        </div>

        <div className="metric-box metric-box-success">
          <div className="metric-label">Fulfilled Transfusions</div>
          <div className="metric-value">{statsLoading ? '...' : (stats?.fulfilledRequests ?? 0)}</div>
          <div className="metric-sub">Verified & completed</div>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="dashboard-tabs">
        <button
          type="button"
          className={`tab-btn ${activeTab === 'requests' ? 'tab-btn-active' : ''}`}
          onClick={() => setActiveTab('requests')}
        >
          📋 Blood Requests ({stats?.totalRequests ?? requests.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'donors' ? 'tab-btn-active' : ''}`}
          onClick={() => setActiveTab('donors')}
        >
          👥 Donor Registry ({stats?.totalDonors ?? donors.length})
        </button>
      </div>

      {/* Tab 1: Blood Requests Management */}
      {activeTab === 'requests' && (
        <div className="tab-pane">
          {/* Requests Filter Toolbar */}
          <div className="filter-card">
            <div className="filter-form">
              <div className="form-group">
                <label className="form-label">Filter by Status</label>
                <select
                  className="form-control"
                  value={reqStatusFilter}
                  onChange={(e) => setReqStatusFilter(e.target.value)}
                >
                  {REQUEST_STATUSES.map((st) => (
                    <option key={st} value={st}>
                      {st === 'ALL' ? 'All Request Statuses' : st}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Blood Group</label>
                <select
                  className="form-control"
                  value={reqBgFilter}
                  onChange={(e) => setReqBgFilter(e.target.value)}
                >
                  {BLOOD_GROUPS.map((bg) => (
                    <option key={bg} value={bg}>
                      {bg === 'ALL' ? 'All Blood Types' : bg}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">City Search</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Filter by city..."
                  value={reqCityFilter}
                  onChange={(e) => setReqCityFilter(e.target.value)}
                />
              </div>

              <div className="filter-actions">
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => {
                    setReqStatusFilter('ALL');
                    setReqBgFilter('ALL');
                    setReqCityFilter('');
                  }}
                >
                  Reset
                </button>
              </div>
            </div>
          </div>

          {/* Requests Table */}
          {requestsLoading ? (
            <LoadingSpinner message="Fetching blood requests..." />
          ) : requests.length === 0 ? (
            <div className="empty-state-card">
              <p>No blood requests found matching the current filters.</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Requester</th>
                    <th>Contact</th>
                    <th>Blood Group</th>
                    <th>Hospital</th>
                    <th>City</th>
                    <th>Units</th>
                    <th>Urgency</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((r) => (
                    <tr key={r.id}>
                      <td><strong>#{r.id}</strong></td>
                      <td>{r.requesterName}</td>
                      <td><code>{r.contactNumber}</code></td>
                      <td>
                        <span className="table-badge table-badge-red">{r.patientBloodGroup}</span>
                      </td>
                      <td>{r.hospitalName}</td>
                      <td>{r.city}</td>
                      <td><strong>{r.unitsRequested}</strong></td>
                      <td>
                        <span className={`urgency-tag urgency-${r.urgency.toLowerCase()}`}>
                          {r.urgency}
                        </span>
                      </td>
                      <td>
                        <StatusBadge status={r.status} />
                      </td>
                      <td>{r.requestDate}</td>
                      <td>
                        <button
                          type="button"
                          className="btn btn-outline btn-xs"
                          onClick={() => handleOpenStatusModal(r)}
                        >
                          Update Status
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Requests Pagination */}
          {reqTotalPages > 1 && (
            <div className="pagination-bar">
              <button
                type="button"
                className="btn btn-outline btn-sm"
                disabled={reqPage <= 1 || requestsLoading}
                onClick={() => fetchRequests(reqPage - 1)}
              >
                ← Prev
              </button>
              <span className="pagination-label">
                Page {reqPage} of {reqTotalPages}
              </span>
              <button
                type="button"
                className="btn btn-outline btn-sm"
                disabled={reqPage >= reqTotalPages || requestsLoading}
                onClick={() => fetchRequests(reqPage + 1)}
              >
                Next →
              </button>
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Donor Records Management */}
      {activeTab === 'donors' && (
        <div className="tab-pane">
          {/* Donors Filter Toolbar */}
          <div className="filter-card">
            <div className="filter-form">
              <div className="form-group">
                <label className="form-label">Blood Group</label>
                <select
                  className="form-control"
                  value={donorBgFilter}
                  onChange={(e) => setDonorBgFilter(e.target.value)}
                >
                  {BLOOD_GROUPS.map((bg) => (
                    <option key={bg} value={bg}>
                      {bg === 'ALL' ? 'All Blood Types' : bg}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">City Search</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Filter by city..."
                  value={donorCityFilter}
                  onChange={(e) => setDonorCityFilter(e.target.value)}
                />
              </div>

              <div className="filter-actions">
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => {
                    setDonorBgFilter('ALL');
                    setDonorCityFilter('');
                  }}
                >
                  Reset
                </button>
              </div>
            </div>
          </div>

          {/* Donors Table */}
          {donorsLoading ? (
            <LoadingSpinner message="Fetching donor records..." />
          ) : donors.length === 0 ? (
            <div className="empty-state-card">
              <p>No donor records found matching search filters.</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Contact (Unmasked)</th>
                    <th>Blood Group</th>
                    <th>City</th>
                    <th>Area</th>
                    <th>Availability</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {donors.map((d) => (
                    <tr key={d.id}>
                      <td><strong>#{d.id}</strong></td>
                      <td>{d.fullName}</td>
                      <td><code>{d.email}</code></td>
                      <td><code>{d.contactNumber}</code></td>
                      <td>
                        <span className="table-badge table-badge-red">{d.bloodGroup}</span>
                      </td>
                      <td>{d.city}</td>
                      <td>{d.area || '—'}</td>
                      <td>
                        <button
                          type="button"
                          className={`btn btn-xs ${d.available ? 'btn-available' : 'btn-unavailable'}`}
                          onClick={() => handleToggleAvailability(d.id, d.available)}
                          title="Click to toggle availability"
                        >
                          {d.available ? '● Available' : '○ Unavailable'}
                        </button>
                      </td>
                      <td>
                        <div className="table-actions-cell">
                          <button
                            type="button"
                            className="btn btn-outline btn-xs"
                            onClick={() => handleOpenEditDonor(d)}
                          >
                            Edit
                          </button>
                          <button
                            type="button"
                            className="btn btn-danger btn-xs"
                            onClick={() => setDeleteDonorTarget(d)}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Donors Pagination */}
          {donorTotalPages > 1 && (
            <div className="pagination-bar">
              <button
                type="button"
                className="btn btn-outline btn-sm"
                disabled={donorPage <= 1 || donorsLoading}
                onClick={() => fetchDonors(donorPage - 1)}
              >
                ← Prev
              </button>
              <span className="pagination-label">
                Page {donorPage} of {donorTotalPages}
              </span>
              <button
                type="button"
                className="btn btn-outline btn-sm"
                disabled={donorPage >= donorTotalPages || donorsLoading}
                onClick={() => fetchDonors(donorPage + 1)}
              >
                Next →
              </button>
            </div>
          )}
        </div>
      )}

      {/* Modal: Update Blood Request Status */}
      <Modal
        isOpen={Boolean(statusModalReq)}
        onClose={() => setStatusModalReq(null)}
        title={`Update Request Status (Req #${statusModalReq?.id})`}
      >
        <div className="modal-form-content">
          <p>
            <strong>Requester:</strong> {statusModalReq?.requesterName} • <strong>Hospital:</strong> {statusModalReq?.hospitalName}
          </p>

          <div className="form-group">
            <label className="form-label required">Select New Status</label>
            <select
              className="form-control"
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
            >
              <option value="PENDING">PENDING (Unverified)</option>
              <option value="VERIFIED">VERIFIED (Hospital Confirmed)</option>
              <option value="FULFILLED">FULFILLED (Units Issued / Transfused)</option>
              <option value="REJECTED">REJECTED (Cancelled / Invalid)</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Administrative Notes / Action Taken</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="e.g. Verified with blood bank supervisor, units dispatched."
              value={statusNotes}
              onChange={(e) => setStatusNotes(e.target.value)}
            ></textarea>
          </div>

          <div className="modal-actions-row">
            <button type="button" className="btn btn-primary" onClick={handleSaveStatus}>
              Save Status Update
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => setStatusModalReq(null)}>
              Cancel
            </button>
          </div>
        </div>
      </Modal>

      {/* Modal: Edit Donor Record */}
      <Modal
        isOpen={Boolean(editDonorTarget)}
        onClose={() => setEditDonorTarget(null)}
        title={`Edit Donor Record (#${editDonorTarget?.id})`}
      >
        <form onSubmit={handleSaveEditDonor} className="modal-form-content">
          <div className="form-group">
            <label className="form-label required">Full Name</label>
            <input
              type="text"
              className="form-control"
              value={editFormData.fullName}
              onChange={(e) => setEditFormData({ ...editFormData, fullName: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label required">Contact Mobile Number</label>
            <input
              type="tel"
              className="form-control"
              value={editFormData.contactNumber}
              onChange={(e) => setEditFormData({ ...editFormData, contactNumber: e.target.value })}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group flex-1">
              <label className="form-label required">Blood Group</label>
              <select
                className="form-control"
                value={editFormData.bloodGroup}
                onChange={(e) => setEditFormData({ ...editFormData, bloodGroup: e.target.value })}
              >
                {BLOOD_GROUPS.filter((b) => b !== 'ALL').map((bg) => (
                  <option key={bg} value={bg}>{bg}</option>
                ))}
              </select>
            </div>

            <div className="form-group flex-1">
              <label className="form-label required">City</label>
              <input
                type="text"
                className="form-control"
                value={editFormData.city}
                onChange={(e) => setEditFormData({ ...editFormData, city: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Area (Optional)</label>
            <input
              type="text"
              className="form-control"
              value={editFormData.area}
              onChange={(e) => setEditFormData({ ...editFormData, area: e.target.value })}
            />
          </div>

          <div className="form-check-group">
            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={editFormData.available}
                onChange={(e) => setEditFormData({ ...editFormData, available: e.target.checked })}
              />
              <span>Available to Donate</span>
            </label>
          </div>

          <div className="modal-actions-row">
            <button type="submit" className="btn btn-primary">Save Changes</button>
            <button type="button" className="btn btn-secondary" onClick={() => setEditDonorTarget(null)}>
              Cancel
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal: Confirm Deletion */}
      <Modal
        isOpen={Boolean(deleteDonorTarget)}
        onClose={() => setDeleteDonorTarget(null)}
        title="Confirm Removal of Donor Record"
      >
        <div className="modal-form-content">
          <p>
            Are you sure you want to permanently delete voluntary donor{' '}
            <strong>{deleteDonorTarget?.fullName}</strong> (ID #{deleteDonorTarget?.id})?
          </p>
          <div className="warning-box">
            ⚠️ This action is permanent and removes the donor from the database. Use this to eliminate test records or inappropriate data.
          </div>
          <div className="modal-actions-row">
            <button type="button" className="btn btn-danger" onClick={handleConfirmDeleteDonor}>
              Yes, Permanently Delete
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => setDeleteDonorTarget(null)}>
              Cancel
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
