package com.fyp.gridguard.Repositories;

import com.fyp.gridguard.Models.LoadReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoadReadingRepo extends JpaRepository<LoadReading, Long> {

////    @Query(value = """
////            select * from load_readings ;
////            """,
////            nativeQuery = true)
//    Optional<LoadReadingRepo> findByAreaIdAndRecordedAt(Long id, LocalDateTime currentSimTime);
//
//    Double getLoadKw();
}
