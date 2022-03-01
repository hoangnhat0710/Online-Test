package com.thanhtam.backend.service;

import java.util.Optional;

import com.thanhtam.backend.entity.Part;
import com.thanhtam.backend.repository.PartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    public Page<Part> getPartLisByCourse(Pageable pageable, Long courseId) {
        return partRepository.findByCourseId(courseId, pageable);
    }

    public void savePart(Part part) {
        partRepository.save(part);
    }

    public Optional<Part> findPartById(Long id) {
        return partRepository.findById(id);
    }


    
    
}
