package com.yourcompany.streamapp.streamservice.repository;

import com.yourcompany.streamapp.streamservice.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {

    /**
     * Finds all streams with a given status.
     * Spring Data JPA automatically implements this method based on its name.
     * Used by the "/live" endpoint.
     * @param status The status to filter by (e.g., Stream.StreamStatus.LIVE)
     * @return A list of streams matching the status.
     */
    List<Stream> findByStatus(Stream.StreamStatus status);
}