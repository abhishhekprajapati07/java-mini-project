package com.lifesaver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * StatusUpdateDto
 * Used by administrators to update a blood request status
 * (PENDING, VERIFIED, FULFILLED, REJECTED).
 */
public class StatusUpdateDto {

    @NotBlank(message = "Status is required")
    private String status;

    private String notes;

    public StatusUpdateDto() {
    }

    public StatusUpdateDto(String status) {
        this.status = status;
    }

    public StatusUpdateDto(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
