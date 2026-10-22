package com.lifesaver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DonorUpdateDto
 * Used when a donor or admin edits an existing donor record.
 */
public class DonorUpdateDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^(\\+?\\d{1,4}[-\\s]?)?(\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}|\\d{5}[-\\s]?\\d{5}|\\d{10})$", message = "Contact number must be a valid 10-digit phone number")
    private String contactNumber;

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 60, message = "City must be between 2 and 60 characters")
    private String city;

    @Size(max = 80, message = "Area must not exceed 80 characters")
    private String area;

    private Boolean available;

    public DonorUpdateDto() {
    }

    public DonorUpdateDto(String fullName, String contactNumber, String bloodGroup, String city, String area, Boolean available) {
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.area = area;
        this.available = available;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
