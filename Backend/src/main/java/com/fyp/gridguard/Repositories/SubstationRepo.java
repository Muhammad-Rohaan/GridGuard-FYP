package com.fyp.gridguard.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyp.gridguard.Models.Substations;

@Repository 
public interface SubstationRepo extends JpaRepository<Substations, Long>{
    
    

}
