package com.governance.evaluation.service;

import com.governance.evaluation.entity.Principle;
import com.governance.evaluation.exception.ResourceNotFoundException;
import com.governance.evaluation.repository.PrincipleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrincipleService {
    
    private final PrincipleRepository principleRepository;
    
    @Transactional(readOnly = true)
    public List<Principle> getAllPrinciples() {
        return principleRepository.findAllByOrderByNumberAsc();
    }
    
    @Transactional(readOnly = true)
    public Principle getPrincipleById(Long id) {
        return principleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Principle not found with id: " + id));
    }
}