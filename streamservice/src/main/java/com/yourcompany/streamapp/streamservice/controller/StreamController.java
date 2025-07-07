package com.yourcompany.streamapp.streamservice.controller;

import com.yourcompany.streamapp.streamservice.dto.CreateStreamRequest;
import com.yourcompany.streamapp.streamservice.dto.StreamResponse;
import com.yourcompany.streamapp.streamservice.model.Stream;
import com.yourcompany.streamapp.streamservice.service.LiveKitTokenService;
import com.yourcompany.streamapp.streamservice.service.StreamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/streams")
public class StreamController {

    private final StreamService streamService;
    private final LiveKitTokenService liveKitTokenService;

    public StreamController(StreamService streamService, LiveKitTokenService liveKitTokenService) {
        this.streamService = streamService;
        this.liveKitTokenService = liveKitTokenService;
    }

    @PostMapping
    public ResponseEntity<StreamResponse> createStream(@Valid @RequestBody CreateStreamRequest request,
                                                       @RequestHeader("X-Authenticated-Username") String hostUsername) {
        Stream newStream = streamService.createStream(request.getTitle(), hostUsername);
        return new ResponseEntity<>(new StreamResponse(newStream), HttpStatus.CREATED);
    }

    @GetMapping("/{streamId}")
    public ResponseEntity<StreamResponse> getStream(@PathVariable Long streamId) {
        return ResponseEntity.ok(new StreamResponse(streamService.findStreamById(streamId)));
    }

    @GetMapping("/live")
    public ResponseEntity<List<StreamResponse>> getLiveStreams() {
        List<StreamResponse> liveStreams = streamService.findAllLiveStreams().stream()
                .map(StreamResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(liveStreams);
    }

    @PostMapping("/{streamId}/start")
    public ResponseEntity<Void> startStream(@PathVariable Long streamId,
                                            @RequestHeader("X-Authenticated-Username") String username) {
        streamService.startStream(streamId, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{streamId}/end")
    public ResponseEntity<Void> endStream(@PathVariable Long streamId,
                                          @RequestHeader("X-Authenticated-Username") String username) {
        streamService.endStream(streamId, username);
        return ResponseEntity.ok().build();
    }
    // NEW, CORRECT AND LOGICAL PATH
    @PostMapping("/{streamId}/token")
    public ResponseEntity<Map<String, String>> getJoinToken(
            @PathVariable Long streamId, // We get the streamId from the path now
            @RequestHeader("X-Authenticated-Username") String username) {

        // The body of the method changes slightly
        try {
            String token = liveKitTokenService.createJoinToken(String.valueOf(streamId), username);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}