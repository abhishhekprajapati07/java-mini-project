package com.lifesaver.service;

import com.lifesaver.dto.BloodRequestCreateDto;
import com.lifesaver.dto.StatusUpdateDto;
import com.lifesaver.model.BloodRequest;

import java.util.List;

/**
 * BloodRequestService Interface
 * Business operations for blood requests.
 */
public interface BloodRequestService {

    BloodRequest createRequest(BloodRequestCreateDto dto);

    BloodRequest getRequestById(int id);

    List<BloodRequest> listRequests(String status, String city, String bloodGroup, int page, int size);

    long countRequests(String status, String city, String bloodGroup);

    BloodRequest updateRequestStatus(int id, StatusUpdateDto dto);
}
