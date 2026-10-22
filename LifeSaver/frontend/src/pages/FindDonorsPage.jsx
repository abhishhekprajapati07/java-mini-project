import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import api from '../api/client';
import DonorCard from '../components/DonorCard';
import MedicalDisclaimer from '../components/MedicalDisclaimer';
import LoadingSpinner from '../components/LoadingSpinner';
import Alert from '../components/Alert';

const BLOOD_GROUPS = ['ALL', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

export default function FindDonorsPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const [bloodGroup, setBloodGroup] = useState(searchParams.get('bloodGroup') || 'ALL');
  const [city, setCity] = useState(searchParams.get('city') || '');
  const [availableFilter, setAvailableFilter] = useState(searchParams.get('available') || 'true');
  const [currentPage, setCurrentPage] = useState(1);

  const [donors, setDonors] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDonors = useCallback(async (page = 1) => {
    setLoading(true);
    setError(null);
    try {
      const availParam = availableFilter === 'ALL' ? '' : availableFilter;
      const data = await api.searchDonors({
        bloodGroup: bloodGroup === 'ALL' ? '' : bloodGroup,
        city: city.trim(),
        available: availParam,
        page,
        size: 9,
      });

      setDonors(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
      setCurrentPage(data.page || 1);
    } catch (err) {
      console.error('Error searching donors:', err);
      setError(err.message || 'Failed to search donors. Please verify server connection.');
    } finally {
      setLoading(false);
    }
  }, [bloodGroup, city, availableFilter]);

  useEffect(() => {
    fetchDonors(1);
  }, [fetchDonors]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    // Update URL query parameters
    const params = {};
    if (bloodGroup && bloodGroup !== 'ALL') params.bloodGroup = bloodGroup;
    if (city.trim()) params.city = city.trim();
    if (availableFilter !== 'ALL') params.available = availableFilter;
    setSearchParams(params);

    fetchDonors(1);
  };

  const handleResetFilters = () => {
    setBloodGroup('ALL');
    setCity('');
    setAvailableFilter('true');
    setSearchParams({});
  };

  return (
    <div className="find-donors-page page-container">
      <div className="page-header">
        <h1 className="page-title">Find Voluntary Blood Donors</h1>
        <p className="page-subtitle">
          Filter verified voluntary donors by blood type and location. Contact details are masked for donor privacy.
        </p>
      </div>

      <MedicalDisclaimer compact />

      {/* Filter Toolbar Card */}
      <div className="filter-card">
        <form onSubmit={handleSearchSubmit} className="filter-form">
          <div className="form-group">
            <label htmlFor="filter-blood-group" className="form-label">Blood Group</label>
            <select
              id="filter-blood-group"
              className="form-control"
              value={bloodGroup}
              onChange={(e) => setBloodGroup(e.target.value)}
            >
              {BLOOD_GROUPS.map((bg) => (
                <option key={bg} value={bg}>
                  {bg === 'ALL' ? 'All Blood Types' : `${bg} Blood Type`}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="filter-city" className="form-label">City / Town</label>
            <input
              id="filter-city"
              type="text"
              className="form-control"
              placeholder="e.g. Delhi, Mumbai, Bengaluru..."
              value={city}
              onChange={(e) => setCity(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="filter-availability" className="form-label">Availability</label>
            <select
              id="filter-availability"
              className="form-control"
              value={availableFilter}
              onChange={(e) => setAvailableFilter(e.target.value)}
            >
              <option value="true">Available Donors Only</option>
              <option value="ALL">All Donors (Active & Inactive)</option>
              <option value="false">Currently Unavailable Only</option>
            </select>
          </div>

          <div className="filter-actions">
            <button type="submit" className="btn btn-primary">
              Filter Donors
            </button>
            <button type="button" className="btn btn-secondary" onClick={handleResetFilters}>
              Reset
            </button>
          </div>
        </form>
      </div>

      {/* Results Header */}
      <div className="results-meta-row">
        <span className="results-count">
          Showing <strong>{donors.length}</strong> of <strong>{totalElements}</strong> matching donor{totalElements === 1 ? '' : 's'}
        </span>
        <span className="privacy-pill">
          🔒 Contact numbers obfuscated for privacy
        </span>
      </div>

      {error && (
        <Alert
          type="error"
          message={error}
          onDismiss={() => setError(null)}
        />
      )}

      {/* Donors Grid */}
      {loading ? (
        <LoadingSpinner message="Searching voluntary donors database..." />
      ) : donors.length === 0 ? (
        <div className="empty-state-card">
          <div className="empty-icon">🔍</div>
          <h3>No Donors Found</h3>
          <p>
            We couldn't find any donors matching your criteria ({bloodGroup !== 'ALL' ? `Group ${bloodGroup}` : ''}{city ? ` in ${city}` : ''}).
          </p>
          <div className="empty-actions">
            <button type="button" className="btn btn-secondary" onClick={handleResetFilters}>
              Clear Search Filters
            </button>
            <Link to="/register-donor" className="btn btn-primary">
              Register as the First Donor in this Area
            </Link>
          </div>
        </div>
      ) : (
        <div className="donors-grid">
          {donors.map((donor) => (
            <DonorCard key={donor.id} donor={donor} />
          ))}
        </div>
      )}

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="pagination-bar">
          <button
            type="button"
            className="btn btn-outline btn-sm"
            disabled={currentPage <= 1 || loading}
            onClick={() => fetchDonors(currentPage - 1)}
          >
            ← Previous
          </button>
          <span className="pagination-label">
            Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong>
          </span>
          <button
            type="button"
            className="btn btn-outline btn-sm"
            disabled={currentPage >= totalPages || loading}
            onClick={() => fetchDonors(currentPage + 1)}
          >
            Next →
          </button>
        </div>
      )}
    </div>
  );
}
