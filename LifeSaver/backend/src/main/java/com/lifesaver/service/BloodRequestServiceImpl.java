package com.lifesaver.service;

import com.lifesaver.dao.BloodRequestDao;
import com.lifesaver.dto.BloodRequestCreateDto;
import com.lifesaver.dto.StatusUpdateDto;
import com.lifesaver.exception.InvalidDataException;
import com.lifesaver.exception.ResourceNotFoundException;
import com.lifesaver.model.BloodGroup;
import com.lifesaver.model.BloodRequest;
import com.lifesaver.model.RequestStatus;
import com.lifesaver.model.UrgencyLevel;
import com.lifesaver.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * BloodRequestServiceImpl
 * Implements blood request creation, listing, and state lifecycle transitions.
 */
@Service
public class BloodRequestServiceImpl implements BloodRequestService {

    private static final Logger log = LoggerFactory.getLogger(BloodRequestServiceImpl.class);
    private final BloodRequestDao bloodRequestDao;

    public BloodRequestServiceImpl(BloodRequestDao bloodRequestDao) {
        this.bloodRequestDao = bloodRequestDao;
    }

    @Override
    public BloodRequest createRequest(BloodRequestCreateDto dto) {
        log.info("Creating blood request for patient needing {} at hospital {}",
                dto.getPatientBloodGroup(), dto.getHospitalName());

        // Validate blood group
        if (!ValidationUtil.isValidBloodGroup(dto.getPatientBloodGroup())) {
            throw new InvalidDataException("Invalid blood group '" + dto.getPatientBloodGroup() + "'");
        }

        // Validate contact number
        if (!ValidationUtil.isValidPhone(dto.getContactNumber())) {
            throw new InvalidDataException("Contact number must be a valid 10-digit phone number");
        }

        // Validate units
        if (dto.getUnitsRequested() == null || dto.getUnitsRequested() < 1 || dto.getUnitsRequested() > 50) {
            throw new InvalidDataException("Units requested must be between 1 and 50");
        }

        // Validate urgency
        UrgencyLevel urgency = UrgencyLevel.fromString(dto.getUrgency());
        BloodGroup bg = BloodGroup.fromString(dto.getPatientBloodGroup());
        LocalDate reqDate = dto.getRequestDate() != null ? dto.getRequestDate() : LocalDate.now();

        BloodRequest req = new BloodRequest(
                dto.getRequesterName().trim(),
                dto.getContactNumber().trim(),
                bg != null ? bg.getLabel() : dto.getPatientBloodGroup().trim().toUpperCase(),
                dto.getHospitalName().trim(),
                dto.getCity().trim(),
                dto.getUnitsRequested(),
                urgency.name(),
                RequestStatus.PENDING.name(), // All public submissions start as unverified PENDING
                dto.getNotes() != null ? dto.getNotes().trim() : null,
                reqDate
        );

        return bloodRequestDao.save(req);
    }

    @Override
    public BloodRequest getRequestById(int id) {
        return bloodRequestDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", id));
    }

    @Override
    public List<BloodRequest> listRequests(String status, String city, String bloodGroup, int page, int size) {
        int validPage = Math.max(1, page);
        int validSize = Math.max(1, Math.min(100, size));
        int offset = (validPage - 1) * validSize;

        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL"))
                ? status.trim().toUpperCase() : null;
        String cleanCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String cleanBg = (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL"))
                ? bloodGroup.trim().toUpperCase() : null;

        return bloodRequestDao.findAll(cleanStatus, cleanCity, cleanBg, validSize, offset);
    }

    @Override
    public long countRequests(String status, String city, String bloodGroup) {
        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL"))
                ? status.trim().toUpperCase() : null;
        String cleanCity = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String cleanBg = (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL"))
                ? bloodGroup.trim().toUpperCase() : null;

        return bloodRequestDao.count(cleanStatus, cleanCity, cleanBg);
    }

    @Override
    public BloodRequest updateRequestStatus(int id, StatusUpdateDto dto) {
        BloodRequest existing = getRequestById(id);

        RequestStatus newStatus = RequestStatus.fromString(dto.getStatus());
        if (newStatus == null) {
            throw new InvalidDataException("Invalid status '" + dto.getStatus() +
                    "'. Allowed values: PENDING, VERIFIED, FULFILLED, REJECTED");
        }

        boolean updated = bloodRequestDao.updateStatus(id, newStatus.name(), dto.getNotes());
        if (!updated) {
            throw new RuntimeException("Failed to update status for blood request id: " + id);
        }
        log.info("Updated blood request id {} status from {} to {}", id, existing.getStatus(), newStatus.name());
        return getRequestById(id);
    }
}
