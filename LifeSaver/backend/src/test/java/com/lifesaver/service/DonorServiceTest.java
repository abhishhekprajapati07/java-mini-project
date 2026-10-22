package com.lifesaver.service;

import com.lifesaver.dao.DonorDao;
import com.lifesaver.dto.DonorRegistrationDto;
import com.lifesaver.exception.DuplicateResourceException;
import com.lifesaver.exception.InvalidDataException;
import com.lifesaver.model.Donor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonorServiceTest {

    @Mock
    private DonorDao donorDao;

    private DonorService donorService;

    @BeforeEach
    void setUp() {
        donorService = new DonorServiceImpl(donorDao);
    }

    @Test
    @DisplayName("Should successfully register donor when data is valid")
    void testRegisterDonorSuccess() {
        DonorRegistrationDto dto = new DonorRegistrationDto(
                "Karan Verma", "karan@example.com", "9876543210",
                "O+", "Bengaluru", "Whitefield", true
        );

        when(donorDao.findByEmail("karan@example.com")).thenReturn(Optional.empty());
        when(donorDao.save(any(Donor.class))).thenAnswer(inv -> {
            Donor d = inv.getArgument(0);
            d.setId(101);
            return d;
        });

        Donor registered = donorService.registerDonor(dto);

        assertNotNull(registered);
        assertEquals(101, registered.getId());
        assertEquals("O+", registered.getBloodGroup());
        assertEquals("karan@example.com", registered.getEmail());
        verify(donorDao, times(1)).save(any(Donor.class));
    }

    @Test
    @DisplayName("Should reject donor registration with duplicate email")
    void testRegisterDonorDuplicateEmail() {
        DonorRegistrationDto dto = new DonorRegistrationDto(
                "Duplicate Person", "existing@example.com", "9876543210",
                "A+", "Delhi", "Dwarka", true
        );

        when(donorDao.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new Donor("Old User", "existing@example.com", "9876543210", "A+", "Delhi", null, true)));

        assertThrows(DuplicateResourceException.class, () -> donorService.registerDonor(dto));
        verify(donorDao, never()).save(any(Donor.class));
    }

    @Test
    @DisplayName("Should reject donor registration with invalid blood group")
    void testRegisterDonorInvalidBloodGroup() {
        DonorRegistrationDto dto = new DonorRegistrationDto(
                "Invalid Group", "invalid.bg@example.com", "9876543210",
                "Z-Positive", "Pune", null, true
        );

        assertThrows(InvalidDataException.class, () -> donorService.registerDonor(dto));
        verify(donorDao, never()).save(any(Donor.class));
    }

    @Test
    @DisplayName("Should reject donor registration with invalid contact number")
    void testRegisterDonorInvalidPhone() {
        DonorRegistrationDto dto = new DonorRegistrationDto(
                "Invalid Phone", "bad.phone@example.com", "123",
                "B+", "Mumbai", null, true
        );

        assertThrows(InvalidDataException.class, () -> donorService.registerDonor(dto));
        verify(donorDao, never()).save(any(Donor.class));
    }
}
