package com.yourcompany.streamapp.streamservice.service;

import com.yourcompany.streamapp.streamservice.model.Stream;
import com.yourcompany.streamapp.streamservice.repository.StreamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class StreamService {

    private final StreamRepository streamRepository;

    public StreamService(StreamRepository streamRepository) {
        this.streamRepository = streamRepository;
    }

    @Transactional
    public Stream createStream(String title, String hostUsername) {
        Stream stream = new Stream();
        stream.setTitle(title);
        stream.setHostUsername(hostUsername); // Correctly uses the String username
        stream.setStatus(Stream.StreamStatus.PENDING);
        stream.setCreatedAt(Instant.now());
        return streamRepository.save(stream);
    }

    @Transactional
    public Stream startStream(Long streamId, String username) {
        Stream stream = findStreamById(streamId);
        if (!stream.getHostUsername().equals(username)) {
            throw new SecurityException("Only the host can start the stream.");
        }
        stream.setStatus(Stream.StreamStatus.LIVE);
        return streamRepository.save(stream);
    }

    @Transactional
    public Stream endStream(Long streamId, String username) {
        Stream stream = findStreamById(streamId);
        if (!stream.getHostUsername().equals(username)) {
            throw new SecurityException("Only the host can end the stream.");
        }
        stream.setStatus(Stream.StreamStatus.ENDED);
        stream.setEndedAt(Instant.now());
        return streamRepository.save(stream);
    }

    public Stream findStreamById(Long streamId) {
        return streamRepository.findById(streamId)
                .orElseThrow(() -> new RuntimeException("Stream not found with ID: " + streamId));
    }

    public List<Stream> findAllLiveStreams() {
        return streamRepository.findByStatus(Stream.StreamStatus.LIVE);
    }
}