//package com.yourcompany.streamapp.streamservice.controller;
//
//import com.yourcompany.streamapp.streamservice.service.LiveKitTokenService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//
//@RestController("/api/livekit")
//public class LiveKitController {
//
//    private final LiveKitTokenService liveKitTokenService;
//
//    public LiveKitController(LiveKitTokenService liveKitTokenService) {
//        this.liveKitTokenService = liveKitTokenService;
//    }
//
//    @PostMapping("/token")
//    public ResponseEntity<Map<String, String>> createToken(@RequestBody Map<String, String> params) {
//        String roomName = params.get("roomName");
//        String participantName = params.get("participantName");
//
//        if (roomName == null || participantName == null) {
//            return ResponseEntity.badRequest().body(Map.of("errorMessage", "roomName and participantName are required"));
//        }
//
//        // We will adapt the service method to match this contract
//        try {
//            String token = liveKitTokenService.createJoinToken(roomName, participantName);
//            return ResponseEntity.ok(Map.of("token", token));
//        } catch (RuntimeException e) {
//            // This can happen if the roomName (streamId) is not a valid number or doesn't exist
//            return ResponseEntity.status(405).body(Map.of("errorMessage", e.getMessage()));
//        }
//    }
//}