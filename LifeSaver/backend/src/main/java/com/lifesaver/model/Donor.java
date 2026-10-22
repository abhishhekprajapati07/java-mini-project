package com.lifesaver.model;

import java.time.LocalDateTime;

/**
 * Donor Entity
 * Demonstrates Java OOP encapsulation: all instance variables are private,
 * accessed via public getters and setters.
 */
public class Donor {

    private Integer id;
    private String fullName;
    private String email;
    private String contactNumber;
    private String bloodGroup;
    private String city;
    private String area;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Donor() {
        this.available = true;
    }

    // Parameterized constructor for creation
    public Donor(String fullName, String email, String contactNumber, String bloodGroup, String city, String area, boolean available) {
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.area = area;
        this.available = available;
    }

    // Full constructor including ID and timestamps
    public Donor(Integer id, String fullName, String email, String contactNumber, String bloodGroup,
                 String city, String area, boolean available, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.area = area;
        this.available = available;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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
        return "Donor{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", city='" + city + '\'' +
                ", available=" + available +
                '}';
    }
}
