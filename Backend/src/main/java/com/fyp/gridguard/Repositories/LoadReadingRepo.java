package com.fyp.gridguard.Repositories;

import com.fyp.gridguard.Models.LoadReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface LoadReadingRepo extends JpaRepository<LoadReading, Long> {
    // Optional<LoadReading> findByAreaIdAndRecordedAt(Long id, LocalDateTime
    // currentSimTime);
    //

    /// / @Query(value = """
    /// / select * from load_readings ;
    /// / """,
    /// / nativeQuery = true)
    /// / Optional<LoadReadingRepo> findByAreaIdAndRecordedAt(Long id, LocalDateTime
    /// currentSimTime);
    //
    // Double getLoadKw();
    //
    // Optional<LocalDateTime> findMinRecordedAt();
    // --------
    // Optional<LoadReading> findByAreaIdAndRecordedAt(Long areaId, LocalDateTime
    // recordedAt);
    //
    // @Query("SELECT MIN(lr.recordedAt) FROM LoadReading lr")
    // Optional<LocalDateTime> findMinRecordedAt();

    Optional<LoadReading> findByAreaIdAndRecordedAt(Long areaId, LocalDateTime recordedAt);

//    /// send current timestamp as a param
//    @Query("SELECT MIN(l.recordedAt) FROM LoadReading l")
//    Optional<LocalDateTime> findMinRecordedAt();

    @Query(value = """
            SELECT * FROM load_readings WHERE TO_CHAR(recorded_at, 'MM-DD HH24') = :dateTime;
            """, nativeQuery = true)
    List<LoadReading> findByDateTime(@Param("dateTime") String dateTime);

}
