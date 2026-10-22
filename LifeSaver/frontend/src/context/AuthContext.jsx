import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [adminUser, setAdminUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check if an existing server session is valid on initial app mount
  const checkAuth = useCallback(async () => {
    try {
      const user = await api.getAdminMe();
      setAdminUser(user);
    } catch {
      // 401 Unauthorized or no active session - expected for anonymous visitors
      setAdminUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const login = async (username, password) => {
    const res = await api.adminLogin(username, password);
    if (res.data) {
      setAdminUser(res.data);
    }
    return res;
  };

  const logout = async () => {
    try {
      await api.adminLogout();
    } finally {
      setAdminUser(null);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        adminUser,
        isAuthenticated: !!adminUser,
        loading,
        login,
        logout,
        checkAuth,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
