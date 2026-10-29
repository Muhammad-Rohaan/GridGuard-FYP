package com.fyp.gridguard.Repositories;

import com.fyp.gridguard.Models.LoadReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoadReadingRepo extends JpaRepository<LoadReading, Long> {
//    Optional<LoadReading> findByAreaIdAndRecordedAt(Long id, LocalDateTime currentSimTime);
//

    /// /    @Query(value = """
    /// /            select * from load_readings ;
    /// /            """,
    /// /            nativeQuery = true)
    /// /    Optional<LoadReadingRepo> findByAreaIdAndRecordedAt(Long id, LocalDateTime currentSimTime);
//
//    Double getLoadKw();
//
//    Optional<LocalDateTime> findMinRecordedAt();
// --------
//    Optional<LoadReading> findByAreaIdAndRecordedAt(Long areaId, LocalDateTime recordedAt);
//
//    @Query("SELECT MIN(lr.recordedAt) FROM LoadReading lr")
//    Optional<LocalDateTime> findMinRecordedAt();

    Optional<LoadReading> findByAreaIdAndRecordedAt(Long areaId, LocalDateTime recordedAt);

    @Query("SELECT MIN(l.recordedAt) FROM LoadReading l")
    Optional<LocalDateTime> findMinRecordedAt();

}
