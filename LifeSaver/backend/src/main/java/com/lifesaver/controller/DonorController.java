package com.lifesaver.controller;

import com.lifesaver.dto.ApiResponse;
import com.lifesaver.dto.DonorRegistrationDto;
import com.lifesaver.dto.DonorUpdateDto;
import com.lifesaver.model.Donor;
import com.lifesaver.service.DonorService;
import com.lifesaver.util.AdminAuthInterceptor;
import com.lifesaver.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DonorController
 * REST controller for donor registration, profile searches, updates, and availability.
 */
@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    /**
     * GET /api/donors
     * Public search with pagination and filtering by bloodGroup, city, and availability.
     * Donor contact numbers are masked for anonymous visitors to protect privacy.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchDonors(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        List<Donor> donors = donorService.searchDonors(bloodGroup, city, available, page, size);
        long totalElements = donorService.countSearch(bloodGroup, city, available);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Check if an authenticated admin is making the request
        HttpSession session = request.getSession(false);
        boolean isAdmin = session != null && session.getAttribute(AdminAuthInterceptor.SESSION_ADMIN_KEY) != null;

        List<Map<String, Object>> content = donors.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("fullName", d.getFullName());
            map.put("bloodGroup", d.getBloodGroup());
            map.put("city", d.getCity());
            map.put("area", d.getArea());
            map.put("available", d.isAvailable());
            map.put("createdAt", d.getCreatedAt());

            if (isAdmin) {
                map.put("email", d.getEmail());
                map.put("contactNumber", d.getContactNumber());
                map.put("isContactMasked", false);
            } else {
                map.put("email", ValidationUtil.maskEmail(d.getEmail()));
                map.put("contactNumber", ValidationUtil.maskPhoneNumber(d.getContactNumber()));
                map.put("isContactMasked", true);
            }
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("medicalDisclaimer", "Compatibility must be medically verified by certified hospital blood bank staff prior to transfusion.");

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/donors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Donor> getDonorById(@PathVariable int id, HttpServletRequest request) {
        Donor donor = donorService.getDonorById(id);

        HttpSession session = request.getSession(false);
        boolean isAdmin = session != null && session.getAttribute(AdminAuthInterceptor.SESSION_ADMIN_KEY) != null;

        if (!isAdmin) {
            // Mask contact details and email for single lookup if not admin to protect privacy
            donor.setEmail(ValidationUtil.maskEmail(donor.getEmail()));
            donor.setContactNumber(ValidationUtil.maskPhoneNumber(donor.getContactNumber()));
        }

        return ResponseEntity.ok(donor);
    }

    /**
     * POST /api/donors
     * Register a new donor.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Donor>> registerDonor(@Valid @RequestBody DonorRegistrationDto dto) {
        Donor created = donorService.registerDonor(dto);
        return new ResponseEntity<>(
                ApiResponse.ok("Donor registered successfully! Thank you for volunteering to save lives.", created),
                HttpStatus.CREATED
        );
    }

    /**
     * PUT /api/donors/{id}
     * Update an existing donor record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Donor>> updateDonor(
            @PathVariable int id,
            @Valid @RequestBody DonorUpdateDto dto
    ) {
        Donor updated = donorService.updateDonor(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Donor profile updated successfully.", updated));
    }

    /**
     * PATCH /api/donors/{id}/availability
     * Quick toggle of donor availability.
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Donor>> updateAvailability(
            @PathVariable int id,
            @RequestParam boolean available
    ) {
        Donor updated = donorService.updateAvailability(id, available);
        String msg = available ? "Donor status marked as AVAILABLE." : "Donor status marked as CURRENTLY UNAVAILABLE.";
        return ResponseEntity.ok(ApiResponse.ok(msg, updated));
    }

    /**
     * DELETE /api/donors/{id}
     * Admin-only protected endpoint.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDonor(@PathVariable int id) {
        donorService.deleteDonor(id);
        return ResponseEntity.ok(ApiResponse.ok("Donor record removed successfully."));
    }
}
