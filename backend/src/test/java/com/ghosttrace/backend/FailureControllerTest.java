package com.ghosttrace.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FailureControllerTest {
    @Autowired MockMvc mvc;

    @Test
    void createsAndFindsFailure() throws Exception {
        mvc.perform(post("/api/failures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""" 
                    {"failureId":"ERR-101","serviceName":"PaymentService",
                     "errorMessage":"Database timeout","severity":"HIGH"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.failureId").value("ERR-101"));

        mvc.perform(get("/api/failures/ERR-101"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.serviceName").value("PaymentService"));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mvc.perform(post("/api/failures")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"failureId":"","serviceName":"PaymentService",
                     "errorMessage":"Timeout","severity":"HIGH"}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownId() throws Exception {
        mvc.perform(get("/api/failures/UNKNOWN"))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectsDuplicateIds() throws Exception {
        String payload = """
            {"failureId":"DUP-1","serviceName":"LoginService",
             "errorMessage":"Bad token","severity":"MEDIUM"}
            """;
        mvc.perform(post("/api/failures").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated());
        mvc.perform(post("/api/failures").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isConflict());
    }
}
