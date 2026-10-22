package com.lifesaver.dao;

import com.lifesaver.model.BloodRequest;

import java.util.List;
import java.util.Optional;

/**
 * BloodRequestDao Interface
 * Abstracts JDBC data access operations for blood requests.
 */
public interface BloodRequestDao {

    Optional<BloodRequest> findById(int id);

    List<BloodRequest> findAll(String status, String city, String bloodGroup, int limit, int offset);

    long count(String status, String city, String bloodGroup);

    BloodRequest save(BloodRequest request);

    boolean updateStatus(int id, String status, String notes);
}
