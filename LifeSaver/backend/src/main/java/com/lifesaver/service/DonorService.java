package com.lifesaver.service;

import com.lifesaver.dto.DonorRegistrationDto;
import com.lifesaver.dto.DonorUpdateDto;
import com.lifesaver.model.Donor;

import java.util.List;

/**
 * DonorService Interface
 * Defines business logic and transaction boundaries for donor operations.
 */
public interface DonorService {

    Donor registerDonor(DonorRegistrationDto dto);

    Donor getDonorById(int id);

    List<Donor> searchDonors(String bloodGroup, String city, Boolean available, int page, int size);

    long countSearch(String bloodGroup, String city, Boolean available);

    Donor updateDonor(int id, DonorUpdateDto dto);

    Donor updateAvailability(int id, boolean available);

    void deleteDonor(int id);
}
