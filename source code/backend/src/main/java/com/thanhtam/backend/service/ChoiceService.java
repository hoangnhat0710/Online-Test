package com.thanhtam.backend.service;

import com.thanhtam.backend.entity.Choice;
import com.thanhtam.backend.repository.ChoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepository;

    public Integer findIsCorrectById(Long id) {
        return choiceRepository.findIsCorrectById(id);
    }

    public Choice findById(Long id) {
        return choiceRepository.findById(id).get();
    }
    
}
