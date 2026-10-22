/**
 * LifeSaver REST API Client
 * Uses native fetch with credentials: 'include' for session cookie support.
 * Configured via Vite environment variable VITE_API_BASE_URL.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

async function request(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint}`;

  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
    credentials: 'include', // Ensures LIFESAVER_SESSION cookie is sent/received
  };

  try {
    const res = await fetch(url, config);

    // Read response body as JSON if available
    let data = null;
    const contentType = res.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      data = await res.json();
    } else {
      const text = await res.text();
      data = text ? { message: text } : null;
    }

    if (!res.ok) {
      const errorMsg = data?.message || data?.error || `HTTP ${res.status}: ${res.statusText}`;
      const err = new Error(errorMsg);
      err.status = res.status;
      err.data = data;
      throw err;
    }

    return data;
  } catch (error) {
    if (!error.status) {
      error.message = error.message || 'Network error: Unable to connect to LifeSaver server';
    }
    throw error;
  }
}

export const api = {
  // System Health
  getHealth: () => request('/health'),

  // Statistics
  getStats: () => request('/stats'),

  // Donors
  searchDonors: ({ bloodGroup = '', city = '', available = null, page = 1, size = 10 } = {}) => {
    const params = new URLSearchParams();
    if (bloodGroup && bloodGroup !== 'ALL') params.append('bloodGroup', bloodGroup);
    if (city) params.append('city', city);
    if (available !== null && available !== '') params.append('available', available);
    params.append('page', page);
    params.append('size', size);
    return request(`/donors?${params.toString()}`);
  },

  getDonor: (id) => request(`/donors/${id}`),

  registerDonor: (donorData) =>
    request('/donors', {
      method: 'POST',
      body: JSON.stringify(donorData),
    }),

  updateDonor: (id, donorData) =>
    request(`/donors/${id}`, {
      method: 'PUT',
      body: JSON.stringify(donorData),
    }),

  updateDonorAvailability: (id, available) =>
    request(`/donors/${id}/availability?available=${available}`, {
      method: 'PATCH',
    }),

  deleteDonor: (id) =>
    request(`/donors/${id}`, {
      method: 'DELETE',
    }),

  // Blood Requests
  createBloodRequest: (requestData) =>
    request('/requests', {
      method: 'POST',
      body: JSON.stringify(requestData),
    }),

  listBloodRequests: ({ status = '', city = '', bloodGroup = '', page = 1, size = 10 } = {}) => {
    const params = new URLSearchParams();
    if (status && status !== 'ALL') params.append('status', status);
    if (city) params.append('city', city);
    if (bloodGroup && bloodGroup !== 'ALL') params.append('bloodGroup', bloodGroup);
    params.append('page', page);
    params.append('size', size);
    return request(`/requests?${params.toString()}`);
  },

  getBloodRequest: (id) => request(`/requests/${id}`),

  updateBloodRequestStatus: (id, status, notes = '') =>
    request(`/requests/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status, notes }),
    }),

  // Admin Authentication
  adminLogin: (username, password) =>
    request('/admin/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  adminLogout: () =>
    request('/admin/logout', {
      method: 'POST',
    }),

  getAdminMe: () => request('/admin/me'),
};

export default api;
