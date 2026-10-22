package com.lifesaver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifesaver.dto.AdminLoginDto;
import com.lifesaver.dto.BloodRequestCreateDto;
import com.lifesaver.dto.DonorRegistrationDto;
import com.lifesaver.dto.DonorUpdateDto;
import com.lifesaver.dto.StatusUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LifeSaverIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("1. Health endpoint should return UP status")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.application", containsString("LifeSaver")));
    }

    @Test
    @DisplayName("2. Stats endpoint should return aggregated counts from database")
    void testStatsEndpoint() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDonors", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.totalRequests", greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("3. Donor registration: success, duplicate email, and validation failures")
    void testDonorRegistrationFlow() throws Exception {
        String uniqueEmail = "integration.test." + System.currentTimeMillis() + "@demo.mail";

        DonorRegistrationDto validDto = new DonorRegistrationDto(
                "Rohit Sharma", uniqueEmail, "9876543210",
                "O+", "Delhi", "Connaught Place", true
        );

        // A. Successful registration -> HTTP 201 Created
        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is(uniqueEmail.toLowerCase())))
                .andExpect(jsonPath("$.data.bloodGroup", is("O+")));

        // B. Duplicate email -> HTTP 409 Conflict
        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already registered")));

        // C. Invalid blood group -> HTTP 400 Bad Request
        DonorRegistrationDto invalidBgDto = new DonorRegistrationDto(
                "Rohit Sharma", "another" + System.currentTimeMillis() + "@demo.mail", "9876543210",
                "INVALID_BG", "Delhi", "Area", true
        );
        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBgDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Public donor search should mask contact details for anonymous visitors")
    void testPublicDonorSearchMasking() throws Exception {
        mockMvc.perform(get("/api/donors?bloodGroup=O+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.content[0].isContactMasked", is(true)))
                .andExpect(jsonPath("$.content[0].contactNumber", containsString("******")));
    }

    @Test
    @DisplayName("5. Blood request submission should default to PENDING status")
    void testBloodRequestSubmission() throws Exception {
        BloodRequestCreateDto dto = new BloodRequestCreateDto(
                "Dr. Neha Kapoor", "9876543210", "A-",
                "Max Super Speciality Hospital", "Delhi", 2, "CRITICAL",
                "Urgent transfusion needed", LocalDate.now()
        );

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.urgency", is("CRITICAL")));
    }

    @Test
    @DisplayName("6. Admin authentication, session protection, and request status workflow")
    void testAdminSecurityAndWorkflow() throws Exception {
        // A. Anonymous visitor accessing GET /api/requests -> HTTP 401 Unauthorized
        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isUnauthorized());

        // B. Admin login with bad password -> HTTP 401 Unauthorized
        AdminLoginDto badLogin = new AdminLoginDto("admin", "WrongPassword#999");
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());

        // C. Admin login with correct password -> HTTP 200 OK & returns session
        AdminLoginDto validLogin = new AdminLoginDto("admin", "AdminPassword@123");
        MvcResult loginResult = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("admin")))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session, "Session should be created after successful login");

        // D. GET /api/admin/me with session -> HTTP 200 OK
        mockMvc.perform(get("/api/admin/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")));

        // E. Access GET /api/requests with session -> HTTP 200 OK
        mockMvc.perform(get("/api/requests").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));

        // F. Update request status with session -> HTTP 200 OK
        StatusUpdateDto updateStatus = new StatusUpdateDto("VERIFIED", "Verified via direct hospital contact");
        mockMvc.perform(patch("/api/requests/1/status")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("VERIFIED")));

        // G. Admin logout -> HTTP 200 OK
        mockMvc.perform(post("/api/admin/logout").session(session))
                .andExpect(status().isOk());

        // H. Accessing /api/admin/me after logout -> HTTP 401 Unauthorized
        mockMvc.perform(get("/api/admin/me").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("7. Donor update with formatted phone number should succeed")
    void testDonorUpdateWithFormattedPhone() throws Exception {
        DonorUpdateDto updateDto = new DonorUpdateDto(
                "Aarav Sharma Updated", "+91 98765 01001", "O+", "Delhi", "Connaught Place", true
        );
        mockMvc.perform(put("/api/donors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName", is("Aarav Sharma Updated")));
    }

    @Test
    @DisplayName("8. Single donor lookup should mask email and phone for anonymous visitors to protect privacy")
    void testSingleDonorLookupMasking() throws Exception {
        // Anonymous lookup: email and phone must both be masked
        mockMvc.perform(get("/api/donors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", containsString("***")))
                .andExpect(jsonPath("$.contactNumber", containsString("******")));
    }

    @Test
    @DisplayName("9. Blood request submission with formatted phone number should succeed")
    void testBloodRequestWithFormattedPhone() throws Exception {
        BloodRequestCreateDto dto = new BloodRequestCreateDto(
                "Dr. Priya Sen", "+91 98765 92001", "B+",
                "Apollo Hospital", "Mumbai", 1, "URGENT",
                "Urgent transfusion needed", LocalDate.now()
        );

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PENDING")));
    }

    @Test
    @DisplayName("10. Donor deletion should require admin authentication")
    void testDonorDeletionSecurity() throws Exception {
        // A. Anonymous deletion attempt -> HTTP 401 Unauthorized
        mockMvc.perform(delete("/api/donors/10"))
                .andExpect(status().isUnauthorized());

        // B. Login as admin and delete -> HTTP 200 OK
        AdminLoginDto loginDto = new AdminLoginDto("admin", "AdminPassword@123");
        MvcResult loginResult = mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(delete("/api/donors/10").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("removed successfully")));

        // Verify donor 10 is gone
        mockMvc.perform(get("/api/donors/10"))
                .andExpect(status().isNotFound());
    }
}
