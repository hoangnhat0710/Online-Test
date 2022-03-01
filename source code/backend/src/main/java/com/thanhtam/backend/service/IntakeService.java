package com.thanhtam.backend.service;

import java.util.Optional;

import com.thanhtam.backend.entity.Intake;
import com.thanhtam.backend.repository.IntakeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntakeService {

    @Autowired
    private IntakeRepository intakeRepository;

    public Optional<Intake> findById(Long id) {

        return intakeRepository.findById(id);

    }

}
