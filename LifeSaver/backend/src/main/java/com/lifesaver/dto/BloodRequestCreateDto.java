package com.lifesaver.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * BloodRequestCreateDto
 * Carries and validates incoming blood request submission.
 */
public class BloodRequestCreateDto {

    @NotBlank(message = "Requester name is required")
    @Size(min = 2, max = 100, message = "Requester name must be between 2 and 100 characters")
    private String requesterName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^(\\+?\\d{1,4}[-\\s]?)?(\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}|\\d{5}[-\\s]?\\d{5}|\\d{10})$", message = "Contact number must be a valid 10-digit phone number")
    private String contactNumber;

    @NotBlank(message = "Patient's required blood group is required")
    private String patientBloodGroup;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 150, message = "Hospital name must be between 2 and 150 characters")
    private String hospitalName;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 60, message = "City must be between 2 and 60 characters")
    private String city;

    @NotNull(message = "Units requested is required")
    @Min(value = 1, message = "Units requested must be at least 1 unit")
    @Max(value = 50, message = "Units requested cannot exceed 50 units per single submission")
    private Integer unitsRequested;

    private String urgency; // NORMAL, URGENT, CRITICAL

    private String notes;

    private LocalDate requestDate;

    public BloodRequestCreateDto() {
        this.unitsRequested = 1;
        this.urgency = "NORMAL";
        this.requestDate = LocalDate.now();
    }

    public BloodRequestCreateDto(String requesterName, String contactNumber, String patientBloodGroup,
                                 String hospitalName, String city, Integer unitsRequested, String urgency,
                                 String notes, LocalDate requestDate) {
        this.requesterName = requesterName;
        this.contactNumber = contactNumber;
        this.patientBloodGroup = patientBloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.unitsRequested = unitsRequested != null ? unitsRequested : 1;
        this.urgency = urgency != null ? urgency : "NORMAL";
        this.notes = notes;
        this.requestDate = requestDate != null ? requestDate : LocalDate.now();
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPatientBloodGroup() {
        return patientBloodGroup;
    }

    public void setPatientBloodGroup(String patientBloodGroup) {
        this.patientBloodGroup = patientBloodGroup;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getUnitsRequested() {
        return unitsRequested;
    }

    public void setUnitsRequested(Integer unitsRequested) {
        this.unitsRequested = unitsRequested;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
