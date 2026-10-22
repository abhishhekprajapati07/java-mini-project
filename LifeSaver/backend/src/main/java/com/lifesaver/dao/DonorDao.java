package com.lifesaver.dao;

import com.lifesaver.model.Donor;

import java.util.List;
import java.util.Optional;

/**
 * DonorDao Interface
 * Demonstrates Java interface abstraction in MVC architecture.
 * Decouples service layer business logic from database JDBC implementation.
 */
public interface DonorDao {

    Optional<Donor> findById(int id);

    Optional<Donor> findByEmail(String email);

    List<Donor> search(String bloodGroup, String city, Boolean available, int limit, int offset);

    long countSearch(String bloodGroup, String city, Boolean available);

    Donor save(Donor donor);

    boolean update(Donor donor);

    boolean updateAvailability(int id, boolean available);

    boolean deleteById(int id);
}
