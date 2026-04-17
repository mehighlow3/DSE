package com.example.scheduler;

import com.example.scheduler.api.MeetingsApi;
import com.example.scheduler.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class MeetingsController implements MeetingsApi {

    private final List<Meeting> meetingStore = new ArrayList<>();
    private final List<Submission> submissionStore = new ArrayList<>();

    @Override
    public ResponseEntity<Meeting> createMeeting(MeetingCreateRequest request) {
        Meeting newMeeting = new Meeting();
        newMeeting.setId(UUID.randomUUID());
        newMeeting.setTitle(request.getTitle());
        newMeeting.setDescription(request.getDescription());
        newMeeting.setStatus(Meeting.StatusEnum.DRAFT);
        
        List<TimeSlot> slots = new ArrayList<>();
        if (request.getInitialSlots() != null) {
            for (TimeSlotRequest slotReq : request.getInitialSlots()) {
                TimeSlot slot = new TimeSlot();
                slot.setId(UUID.randomUUID());
                slot.setStartTime(slotReq.getStartTime());
                slot.setEndTime(slotReq.getEndTime());
                slots.add(slot);
            }
        }
        newMeeting.setSlots(slots);
        
        meetingStore.add(newMeeting);
        return new ResponseEntity<>(newMeeting, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Meeting> getMeeting(UUID meetingId) {
        return meetingStore.stream()
                .filter(m -> m.getId().equals(meetingId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Meeting> publishMeeting(UUID meetingId) {
        Meeting meeting = meetingStore.stream()
                .filter(m -> m.getId().equals(meetingId))
                .findFirst()
                .orElse(null);

        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        meeting.setStatus(Meeting.StatusEnum.PUBLISHED);
        
        return ResponseEntity.ok(meeting);
    }

    @Override
    public ResponseEntity<TimeSlot> addSlot(UUID meetingId, TimeSlotRequest timeSlotRequest) {
        Meeting meeting = meetingStore.stream()
                .filter(m -> m.getId().equals(meetingId))
                .findFirst()
                .orElse(null);

        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (meeting.getStatus() != Meeting.StatusEnum.DRAFT) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        TimeSlot newSlot = new TimeSlot();
        newSlot.setId(UUID.randomUUID());
        newSlot.setStartTime(timeSlotRequest.getStartTime());
        newSlot.setEndTime(timeSlotRequest.getEndTime());
        
        meeting.addSlotsItem(newSlot);
        
        return new ResponseEntity<>(newSlot, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> removeSlot(UUID meetingId, UUID slotId) {
        Meeting meeting = meetingStore.stream()
                .filter(m -> m.getId().equals(meetingId))
                .findFirst()
                .orElse(null);

        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (meeting.getStatus() != Meeting.StatusEnum.DRAFT) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean removed = meeting.getSlots().removeIf(slot -> slot.getId().equals(slotId));
        
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Submission> submitAvailability(UUID meetingId, SubmissionRequest request) {
        Meeting meeting = meetingStore.stream()
                .filter(m -> m.getId().equals(meetingId))
                .findFirst()
                .orElse(null);
                
        if (meeting == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if (meeting.getStatus() != Meeting.StatusEnum.PUBLISHED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Submission submission = new Submission();
        submission.setId(UUID.randomUUID());
        submission.setParticipantName(request.getParticipantName());
        submission.setSelections(request.getSelections());
        
        submissionStore.add(submission);

        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Submission>> getSubmissions(UUID meetingId) {
        return ResponseEntity.ok(submissionStore);
    }
}