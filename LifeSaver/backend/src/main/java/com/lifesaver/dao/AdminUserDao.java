package com.lifesaver.dao;

import com.lifesaver.model.AdminUser;

import java.util.Optional;

/**
 * AdminUserDao Interface
 * Abstracts JDBC data access operations for administrative credentials.
 */
public interface AdminUserDao {

    Optional<AdminUser> findByUsername(String username);

    Optional<AdminUser> findById(int id);

    AdminUser save(AdminUser adminUser);

    long count();
}
