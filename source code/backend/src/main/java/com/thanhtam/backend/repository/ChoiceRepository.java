package com.thanhtam.backend.repository;

import com.thanhtam.backend.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
  
@Query(value = "select corrected from choice where choice.id = ?1")
    Integer findIsCorrectById(Long id);
}
