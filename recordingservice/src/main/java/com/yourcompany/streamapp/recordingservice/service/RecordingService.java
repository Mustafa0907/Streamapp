package com.yourcompany.streamapp.recordingservice.service;

import io.livekit.server.EgressServiceClient;
import livekit.LivekitEgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RecordingService {

    private static final Logger logger = LoggerFactory.getLogger(RecordingService.class);
    private final EgressServiceClient egressClient;
    private final Map<Long, String> activeRecordings = new ConcurrentHashMap<>();

    public RecordingService(
            @Value("${livekit.host}") String livekitHost,
            @Value("${livekit.api.key}") String livekitApiKey,
            @Value("${livekit.api.secret}") String livekitApiSecret) {

        // --- ADDING LOGGING FOR DEBUGGING ---
        logger.info("--- RECORDING SERVICE INITIALIZATION ---");
        logger.info("Received livekit.host from config: {}", livekitHost);

        String cleanHost = livekitHost.replace("wss://", "").replace("ws://", "").replace("https://", "").replace("http://", "");

        // 2. Construct the correct HTTPS URL required by the Egress SDK.
        String egressApiUrl = "https://" + cleanHost;

        logger.info("Constructed Egress API URL: {}", egressApiUrl);
        logger.info("Using API Key: {}", livekitApiKey);
        logger.info("--------------------------------------");

        this.egressClient = EgressServiceClient.createClient("https://streamapp-uto3aoff.livekit.cloud", livekitApiKey, livekitApiSecret);
    }

    public LivekitEgress.EgressInfo startRecording(Long streamId) throws IOException {
        if (activeRecordings.containsKey(streamId)) {
            throw new IllegalStateException("Stream " + streamId + " is already being recorded.");
        }

        String roomName = String.valueOf(streamId);
        String filepath = "/recordings/" + roomName + "-" + System.currentTimeMillis() + ".mp4";
        logger.info("Starting recording for room '{}' to file '{}'", roomName, filepath);

        // --- THIS IS THE CORRECT IMPLEMENTATION FOR SDK v0.10.0 ---

        // 1. Build the FileOutput object for MP4 recording.
        LivekitEgress.EncodedFileOutput fileOutput = LivekitEgress.EncodedFileOutput.newBuilder()
                .setFilepath(filepath)
                .setFileType(LivekitEgress.EncodedFileType.MP4)
                .setS3(LivekitEgress.S3Upload.newBuilder()
                        .setAccessKey("AKIASTDCWLRK4CPQEK73")
                        .setSecret("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")
                        .setBucket("aws-awn")
                        .setEndpoint("https://s3.us-east-1.amazonaws.com")
                        .build())
                .build();

        // 2. Call the version of the method that takes multiple arguments, as shown in the docs.
        Call<LivekitEgress.EgressInfo> call = egressClient.startRoomCompositeEgress(
                roomName,
                fileOutput
        );
        // -------------------------------------------------------------------------

        Response<LivekitEgress.EgressInfo> response = call.execute();

        if (!response.isSuccessful() || response.body() == null) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            throw new IOException("Egress request failed with code " + response.code() + ": " + errorBody);
        }

        LivekitEgress.EgressInfo egressInfo = response.body();
        activeRecordings.put(streamId, egressInfo.getEgressId());
        logger.info("Recording started for room '{}' with Egress ID: {}", roomName, egressInfo.getEgressId());

        return egressInfo;
    }

    // The stopRecording method is correct and needs no changes.
    public LivekitEgress.EgressInfo stopRecording(Long streamId) throws IOException {
        String egressId = activeRecordings.get(streamId);
        if (egressId == null) {
            throw new IllegalStateException("Stream " + streamId + " is not being recorded.");
        }
        logger.info("Stopping recording for Egress ID: {}", egressId);
        Call<LivekitEgress.EgressInfo> call = egressClient.stopEgress(egressId);
        Response<LivekitEgress.EgressInfo> response = call.execute();
        if (!response.isSuccessful() || response.body() == null) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            throw new IOException("Failed to stop egress with code " + response.code() + ": " + errorBody);
        }
        activeRecordings.remove(streamId);
        logger.info("Recording stopped for Egress ID: {}", egressId);
        return response.body();
    }
}