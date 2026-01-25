package com.example.scheduler;

import com.example.scheduler.model.Meeting;
import com.example.scheduler.model.MeetingCreateRequest;
import com.example.scheduler.model.TimeSlotRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeetingSchedulerApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testFullMeetingWorkflow() {
        String baseUrl = "http://localhost:" + port + "/meetings";

        // 1. CREATE MEETING
        MeetingCreateRequest createReq = new MeetingCreateRequest();
        createReq.setTitle("Integration Test Meeting");
        createReq.setDescription("Testing the full flow");
        // Add one initial slot
        TimeSlotRequest slotReq = new TimeSlotRequest();
        slotReq.setStartTime(OffsetDateTime.parse("2026-02-01T10:00:00Z"));
        slotReq.setEndTime(OffsetDateTime.parse("2026-02-01T11:00:00Z"));
        createReq.setInitialSlots(Collections.singletonList(slotReq));

        ResponseEntity<Meeting> createResponse = restTemplate.postForEntity(baseUrl, createReq, Meeting.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("DRAFT", createResponse.getBody().getStatus().toString());
        String meetingId = createResponse.getBody().getId().toString();

        // 2. PUBLISH MEETING
        String publishUrl = baseUrl + "/" + meetingId + "/publish";
        ResponseEntity<Meeting> publishResponse = restTemplate.postForEntity(publishUrl, null, Meeting.class);
        
        assertEquals(HttpStatus.OK, publishResponse.getStatusCode());
        assertEquals("PUBLISHED", publishResponse.getBody().getStatus().toString());

        // 3. VERIFY GET
        ResponseEntity<Meeting> getResponse = restTemplate.getForEntity(baseUrl + "/" + meetingId, Meeting.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Meeting", getResponse.getBody().getTitle());
    }
}