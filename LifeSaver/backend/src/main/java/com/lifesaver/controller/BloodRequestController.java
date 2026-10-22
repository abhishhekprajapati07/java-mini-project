package com.lifesaver.controller;

import com.lifesaver.dto.ApiResponse;
import com.lifesaver.dto.BloodRequestCreateDto;
import com.lifesaver.dto.StatusUpdateDto;
import com.lifesaver.model.BloodRequest;
import com.lifesaver.service.BloodRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BloodRequestController
 * Public request submission & administrative request workflow management.
 */
@RestController
@RequestMapping("/api/requests")
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;

    public BloodRequestController(BloodRequestService bloodRequestService) {
        this.bloodRequestService = bloodRequestService;
    }

    /**
     * POST /api/requests
     * Publicly accessible submission. Requests are initially marked as PENDING (unverified).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BloodRequest>> createRequest(@Valid @RequestBody BloodRequestCreateDto dto) {
        BloodRequest created = bloodRequestService.createRequest(dto);
        return new ResponseEntity<>(
                ApiResponse.ok("Blood requirement submitted successfully. Notice: All incoming requests remain unverified until verified by hospital staff or administrative review.", created),
                HttpStatus.CREATED
        );
    }

    /**
     * GET /api/requests
     * Protected: Admin only.
     * Filter by status, city, bloodGroup with pagination.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<BloodRequest> list = bloodRequestService.listRequests(status, city, bloodGroup, page, size);
        long totalElements = bloodRequestService.countRequests(status, city, bloodGroup);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", list);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequest> getRequestById(@PathVariable int id) {
        return ResponseEntity.ok(bloodRequestService.getRequestById(id));
    }

    /**
     * PATCH /api/requests/{id}/status
     * Protected: Admin only.
     * Updates request status among PENDING, VERIFIED, FULFILLED, and REJECTED.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BloodRequest>> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody StatusUpdateDto dto
    ) {
        BloodRequest updated = bloodRequestService.updateRequestStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Request status updated to " + updated.getStatus(), updated));
    }
}
