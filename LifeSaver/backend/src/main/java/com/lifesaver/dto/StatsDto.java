package com.lifesaver.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * StatsDto
 * Aggregated statistics calculated from real database records for the Home page and Admin dashboard.
 */
public class StatsDto {

    private long totalDonors;
    private long availableDonors;
    private long totalRequests;
    private long pendingRequests;
    private long verifiedRequests;
    private long fulfilledRequests;
    private long rejectedRequests;
    private Map<String, Integer> donorsByBloodGroup = new HashMap<>();
    private Map<String, Integer> requestsByBloodGroup = new HashMap<>();

    public StatsDto() {
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public long getAvailableDonors() {
        return availableDonors;
    }

    public void setAvailableDonors(long availableDonors) {
        this.availableDonors = availableDonors;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getVerifiedRequests() {
        return verifiedRequests;
    }

    public void setVerifiedRequests(long verifiedRequests) {
        this.verifiedRequests = verifiedRequests;
    }

    public long getFulfilledRequests() {
        return fulfilledRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(long rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public Map<String, Integer> getDonorsByBloodGroup() {
        return donorsByBloodGroup;
    }

    public void setDonorsByBloodGroup(Map<String, Integer> donorsByBloodGroup) {
        this.donorsByBloodGroup = donorsByBloodGroup;
    }

    public Map<String, Integer> getRequestsByBloodGroup() {
        return requestsByBloodGroup;
    }

    public void setRequestsByBloodGroup(Map<String, Integer> requestsByBloodGroup) {
        this.requestsByBloodGroup = requestsByBloodGroup;
    }
}
