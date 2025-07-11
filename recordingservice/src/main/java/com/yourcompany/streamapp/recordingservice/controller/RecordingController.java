package com.yourcompany.streamapp.recordingservice.controller;

import com.yourcompany.streamapp.recordingservice.service.RecordingService;
import livekit.LivekitEgress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recordings")
public class RecordingController {

    private final RecordingService recordingService;

    public RecordingController(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @PostMapping("/stream/{streamId}/start")
    public ResponseEntity<String> startRecording(@PathVariable Long streamId,
                                                 @RequestHeader("X-Authenticated-Username") String username) {
        // In a real app, you would add logic here to verify 'username' is the host.
        try {
            LivekitEgress.EgressInfo info = recordingService.startRecording(streamId);
            // The toJson method from the SDK is deprecated, let's just return the ID.
            return ResponseEntity.ok("{\"egressId\": \"" + info.getEgressId() + "\"}");
        } catch (Exception e) {
            // This will now catch both IllegalStateException and IOException
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/stream/{streamId}/stop")
    public ResponseEntity<String> stopRecording(@PathVariable Long streamId,
                                                @RequestHeader("X-Authenticated-Username") String username) {
        try {
            LivekitEgress.EgressInfo info = recordingService.stopRecording(streamId);
            return ResponseEntity.ok("{\"egressId\": \"" + info.getEgressId() + "\", \"status\": \"STOPPING\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}