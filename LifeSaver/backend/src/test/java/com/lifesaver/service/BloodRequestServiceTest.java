package com.lifesaver.service;

import com.lifesaver.dao.BloodRequestDao;
import com.lifesaver.dto.BloodRequestCreateDto;
import com.lifesaver.dto.StatusUpdateDto;
import com.lifesaver.exception.InvalidDataException;
import com.lifesaver.model.BloodRequest;
import com.lifesaver.model.RequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloodRequestServiceTest {

    @Mock
    private BloodRequestDao bloodRequestDao;

    private BloodRequestService bloodRequestService;

    @BeforeEach
    void setUp() {
        bloodRequestService = new BloodRequestServiceImpl(bloodRequestDao);
    }

    @Test
    @DisplayName("Should create blood request in PENDING status by default")
    void testCreateBloodRequestSuccess() {
        BloodRequestCreateDto dto = new BloodRequestCreateDto(
                "Hospital Staff", "9876543210", "AB-",
                "Metro Heart Institute", "Chennai", 2, "URGENT",
                "Patient admitted in CCU", LocalDate.now()
        );

        when(bloodRequestDao.save(any(BloodRequest.class))).thenAnswer(inv -> {
            BloodRequest req = inv.getArgument(0);
            req.setId(50);
            return req;
        });

        BloodRequest created = bloodRequestService.createRequest(dto);

        assertNotNull(created);
        assertEquals(50, created.getId());
        assertEquals("PENDING", created.getStatus(), "All public requests must be unverified PENDING by default");
        assertEquals("AB-", created.getPatientBloodGroup());
        assertEquals(2, created.getUnitsRequested());
        verify(bloodRequestDao, times(1)).save(any(BloodRequest.class));
    }

    @Test
    @DisplayName("Should reject blood request with invalid blood group")
    void testCreateBloodRequestInvalidBloodGroup() {
        BloodRequestCreateDto dto = new BloodRequestCreateDto(
                "Requester", "9876543210", "UNKNOWN",
                "Hospital", "City", 1, "NORMAL", null, LocalDate.now()
        );

        assertThrows(InvalidDataException.class, () -> bloodRequestService.createRequest(dto));
        verify(bloodRequestDao, never()).save(any(BloodRequest.class));
    }

    @Test
    @DisplayName("Should update blood request status to VERIFIED or FULFILLED")
    void testUpdateStatusSuccess() {
        BloodRequest existing = new BloodRequest();
        existing.setId(10);
        existing.setStatus(RequestStatus.PENDING.name());

        when(bloodRequestDao.findById(10)).thenReturn(Optional.of(existing));
        when(bloodRequestDao.updateStatus(eq(10), eq("VERIFIED"), any())).thenReturn(true);

        StatusUpdateDto updateDto = new StatusUpdateDto("VERIFIED", "Verified with hospital blood bank coordinator");
        BloodRequest updated = bloodRequestService.updateRequestStatus(10, updateDto);

        assertNotNull(updated);
        verify(bloodRequestDao, times(1)).updateStatus(eq(10), eq("VERIFIED"), any());
    }

    @Test
    @DisplayName("Should reject invalid request status")
    void testUpdateStatusInvalid() {
        BloodRequest existing = new BloodRequest();
        existing.setId(10);

        when(bloodRequestDao.findById(10)).thenReturn(Optional.of(existing));

        StatusUpdateDto invalidDto = new StatusUpdateDto("INVALID_STATUS");
        assertThrows(InvalidDataException.class, () -> bloodRequestService.updateRequestStatus(10, invalidDto));
        verify(bloodRequestDao, never()).updateStatus(anyInt(), anyString(), any());
    }
}
