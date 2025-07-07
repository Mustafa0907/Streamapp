package com.yourcompany.streamapp.streamservice.service;

import com.yourcompany.streamapp.streamservice.model.Stream;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LiveKitTokenService {

    private final String livekitApiKey;
    private final String livekitApiSecret;
    private final StreamService streamService;

    public LiveKitTokenService(
            @Value("${live.api.key}") String livekitApiKey,
            @Value("${live.api.secret}") String livekitApiSecret,
            StreamService streamService) {
        this.livekitApiKey = livekitApiKey;
        this.livekitApiSecret = livekitApiSecret;
        this.streamService = streamService;
    }

    public String createJoinToken(String roomName, String participantName) {
        // Convert the roomName (which is our streamId) to a Long to find the stream
        Long streamId;
        try {
            streamId = Long.parseLong(roomName);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid room name format. Must be a numeric Stream ID.");
        }

        // This call ensures the stream exists before we generate a token
        Stream stream = streamService.findStreamById(streamId);

        // The host is the only one who can publish (share screen/camera)
        boolean canPublish = stream.getHostUsername().equals(participantName);

        AccessToken token = new AccessToken(livekitApiKey, livekitApiSecret);
        token.setName(participantName);
        token.setIdentity(participantName);

        RoomJoin roomJoinGrant = new RoomJoin(true);
        RoomName roomNameGrant = new RoomName(roomName);

        token.addGrants(roomJoinGrant, roomNameGrant);

        // You can add more specific permissions if needed, e.g.,
        // VideoGrant videoGrant = new VideoGrant();
        // videoGrant.setCanPublish(canPublish);
        // token.addGrants(videoGrant);

        return token.toJwt();
    }
}