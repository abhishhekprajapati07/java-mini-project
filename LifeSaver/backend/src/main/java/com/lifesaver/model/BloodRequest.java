package com.lifesaver.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * BloodRequest Entity
 * Represents an emergency or regular blood requirement submitted by a patient/requester.
 */
public class BloodRequest {

    private Integer id;
    private String requesterName;
    private String contactNumber;
    private String patientBloodGroup;
    private String hospitalName;
    private String city;
    private int unitsRequested;
    private String urgency; // NORMAL, URGENT, CRITICAL
    private String status;  // PENDING, VERIFIED, FULFILLED, REJECTED
    private String notes;
    private LocalDate requestDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BloodRequest() {
        this.unitsRequested = 1;
        this.urgency = UrgencyLevel.NORMAL.name();
        this.status = RequestStatus.PENDING.name();
        this.requestDate = LocalDate.now();
    }

    public BloodRequest(String requesterName, String contactNumber, String patientBloodGroup,
                        String hospitalName, String city, int unitsRequested, String urgency,
                        String status, String notes, LocalDate requestDate) {
        this.requesterName = requesterName;
        this.contactNumber = contactNumber;
        this.patientBloodGroup = patientBloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.unitsRequested = unitsRequested;
        this.urgency = urgency != null ? urgency : UrgencyLevel.NORMAL.name();
        this.status = status != null ? status : RequestStatus.PENDING.name();
        this.notes = notes;
        this.requestDate = requestDate != null ? requestDate : LocalDate.now();
    }

    public BloodRequest(Integer id, String requesterName, String contactNumber, String patientBloodGroup,
                        String hospitalName, String city, int unitsRequested, String urgency,
                        String status, String notes, LocalDate requestDate,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requesterName = requesterName;
        this.contactNumber = contactNumber;
        this.patientBloodGroup = patientBloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.unitsRequested = unitsRequested;
        this.urgency = urgency;
        this.status = status;
        this.notes = notes;
        this.requestDate = requestDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getUnitsRequested() {
        return unitsRequested;
    }

    public void setUnitsRequested(int unitsRequested) {
        this.unitsRequested = unitsRequested;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
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

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BloodRequest{" +
                "id=" + id +
                ", requesterName='" + requesterName + '\'' +
                ", patientBloodGroup='" + patientBloodGroup + '\'' +
                ", city='" + city + '\'' +
                ", unitsRequested=" + unitsRequested +
                ", status='" + status + '\'' +
                '}';
    }
}
