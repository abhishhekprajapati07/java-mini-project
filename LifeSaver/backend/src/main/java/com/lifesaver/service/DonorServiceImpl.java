package com.lifesaver.service;

import com.lifesaver.dao.DonorDao;
import com.lifesaver.dto.DonorRegistrationDto;
import com.lifesaver.dto.DonorUpdateDto;
import com.lifesaver.exception.DuplicateResourceException;
import com.lifesaver.exception.InvalidDataException;
import com.lifesaver.exception.ResourceNotFoundException;
import com.lifesaver.model.BloodGroup;
import com.lifesaver.model.Donor;
import com.lifesaver.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DonorServiceImpl
 * Implements business validation, data normalization, and delegates to DonorDao.
 */
@Service
public class DonorServiceImpl implements DonorService {

    private static final Logger log = LoggerFactory.getLogger(DonorServiceImpl.class);
    private final DonorDao donorDao;

    public DonorServiceImpl(DonorDao donorDao) {
        this.donorDao = donorDao;
    }

    @Override
    public Donor registerDonor(DonorRegistrationDto dto) {
        log.info("Processing registration for email: {}", dto.getEmail());

        // 1. Blood group format validation
        if (!ValidationUtil.isValidBloodGroup(dto.getBloodGroup())) {
            throw new InvalidDataException("Invalid blood group '" + dto.getBloodGroup() +
                    "'. Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }

        // 2. Email format validation
        if (!ValidationUtil.isValidEmail(dto.getEmail())) {
            throw new InvalidDataException("Invalid email format: " + dto.getEmail());
        }

        // 3. Contact number format validation
        if (!ValidationUtil.isValidPhone(dto.getContactNumber())) {
            throw new InvalidDataException("Contact number must be a valid 10-digit phone number");
        }

        // 4. Check for duplicate email
        if (donorDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("A donor with email '" + dto.getEmail() + "' is already registered");
        }

        BloodGroup bg = BloodGroup.fromString(dto.getBloodGroup());
        Donor donor = new Donor(
                dto.getFullName().trim(),
                dto.getEmail().trim().toLowerCase(),
                dto.getContactNumber().trim(),
                bg != null ? bg.getLabel() : dto.getBloodGroup().trim().toUpperCase(),
                dto.getCity().trim(),
                dto.getArea() != null ? dto.getArea().trim() : null,
                dto.getAvailable() != null ? dto.getAvailable() : true
        );

        return donorDao.save(donor);
    }

    @Override
    public Donor getDonorById(int id) {
        return donorDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor", id));
    }

    @Override
    public List<Donor> searchDonors(String bloodGroup, String city, Boolean available, int page, int size) {
        int validPage = Math.max(1, page);
        int validSize = Math.max(1, Math.min(100, size));
        int offset = (validPage - 1) * validSize;

        String cleanBloodGroup = (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL"))
                ? bloodGroup.trim().toUpperCase() : null;
        String cleanCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;

        return donorDao.search(cleanBloodGroup, cleanCity, available, validSize, offset);
    }

    @Override
    public long countSearch(String bloodGroup, String city, Boolean available) {
        String cleanBloodGroup = (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL"))
                ? bloodGroup.trim().toUpperCase() : null;
        String cleanCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;

        return donorDao.countSearch(cleanBloodGroup, cleanCity, available);
    }

    @Override
    public Donor updateDonor(int id, DonorUpdateDto dto) {
        Donor existing = getDonorById(id);

        if (!ValidationUtil.isValidBloodGroup(dto.getBloodGroup())) {
            throw new InvalidDataException("Invalid blood group '" + dto.getBloodGroup() + "'");
        }
        if (!ValidationUtil.isValidPhone(dto.getContactNumber())) {
            throw new InvalidDataException("Contact number must be a valid 10-digit phone number");
        }

        BloodGroup bg = BloodGroup.fromString(dto.getBloodGroup());
        existing.setFullName(dto.getFullName().trim());
        existing.setContactNumber(dto.getContactNumber().trim());
        existing.setBloodGroup(bg != null ? bg.getLabel() : dto.getBloodGroup().trim().toUpperCase());
        existing.setCity(dto.getCity().trim());
        existing.setArea(dto.getArea() != null ? dto.getArea().trim() : null);
        if (dto.getAvailable() != null) {
            existing.setAvailable(dto.getAvailable());
        }

        boolean updated = donorDao.update(existing);
        if (!updated) {
            throw new RuntimeException("Failed to update donor id: " + id);
        }
        return getDonorById(id);
    }

    @Override
    public Donor updateAvailability(int id, boolean available) {
        // Verify donor exists
        getDonorById(id);
        boolean ok = donorDao.updateAvailability(id, available);
        if (!ok) {
            throw new RuntimeException("Failed to update availability for donor id: " + id);
        }
        return getDonorById(id);
    }

    @Override
    public void deleteDonor(int id) {
        getDonorById(id);
        boolean ok = donorDao.deleteById(id);
        if (!ok) {
            throw new RuntimeException("Failed to delete donor id: " + id);
        }
        log.info("Donor id {} successfully deleted by admin", id);
    }
}
