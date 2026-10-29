package com.fyp.gridguard.Repositories;

import com.fyp.gridguard.Models.Areas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepo extends JpaRepository<Areas, Long> {
}
