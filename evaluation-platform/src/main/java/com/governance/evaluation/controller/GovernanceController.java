package com.governance.evaluation.controller;

import com.governance.evaluation.entity.Criterion;
import com.governance.evaluation.entity.Practice;
import com.governance.evaluation.entity.Principle;
import com.governance.evaluation.repository.CriterionRepository;
import com.governance.evaluation.repository.PracticeRepository;
import com.governance.evaluation.repository.PrincipleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/governance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GovernanceController {

    private final PrincipleRepository principleRepository;
    private final PracticeRepository practiceRepository;
    private final CriterionRepository criterionRepository;

    /**
     * Get complete governance framework (Principles → Practices → Criteria)
     * GET /api/governance/framework
     */
    @GetMapping("/framework")
    public ResponseEntity<?> getCompleteFramework() {
        try {
            List<Principle> principles = principleRepository.findByIsActiveTrueOrderByOrderIndexAsc();
            
            List<Map<String, Object>> frameworkData = principles.stream()
                .map(principle -> {
                    Map<String, Object> principleMap = new HashMap<>();
                    principleMap.put("principleId", principle.getPrincipleId());
                    principleMap.put("name", principle.getName());
                    principleMap.put("description", principle.getDescription());
                    principleMap.put("orderIndex", principle.getOrderIndex());
                    
                    // Get practices for this principle
                    List<Practice> practices = practiceRepository
                        .findByPrinciple_PrincipleIdAndIsActiveTrueOrderByOrderIndexAsc(principle.getPrincipleId());
                    
                    List<Map<String, Object>> practicesData = practices.stream()
                        .map(practice -> {
                            Map<String, Object> practiceMap = new HashMap<>();
                            practiceMap.put("practiceId", practice.getPracticeId());
                            practiceMap.put("name", practice.getName());
                            practiceMap.put("description", practice.getDescription());
                            practiceMap.put("orderIndex", practice.getOrderIndex());
                            
                            // Get criteria for this practice
                            List<Criterion> criteria = criterionRepository
                                .findByPractice_PracticeIdAndIsActiveTrueOrderByOrderIndexAsc(practice.getPracticeId());
                            
                            List<Map<String, Object>> criteriaData = criteria.stream()
                                .map(criterion -> {
                                    Map<String, Object> criterionMap = new HashMap<>();
                                    criterionMap.put("criterionId", criterion.getCriterionId());
                                    criterionMap.put("description", criterion.getDescription());
                                    criterionMap.put("orderIndex", criterion.getOrderIndex());
                                    criterionMap.put("level0Description", criterion.getLevel0Description());
                                    criterionMap.put("level1Description", criterion.getLevel1Description());
                                    criterionMap.put("level2Description", criterion.getLevel2Description());
                                    criterionMap.put("level3Description", criterion.getLevel3Description());
                                    return criterionMap;
                                })
                                .collect(Collectors.toList());
                            
                            practiceMap.put("criteria", criteriaData);
                            return practiceMap;
                        })
                        .collect(Collectors.toList());
                    
                    principleMap.put("practices", practicesData);
                    return principleMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(frameworkData);
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching governance framework: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch governance framework"));
        }
    }

    /**
     * Get all principles
     * GET /api/governance/principles
     */
    @GetMapping("/principles")
    public ResponseEntity<?> getAllPrinciples() {
        try {
            List<Principle> principles = principleRepository.findByIsActiveTrueOrderByOrderIndexAsc();
            return ResponseEntity.ok(principles);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch principles"));
        }
    }

    /**
     * Get practices for a principle
     * GET /api/governance/principles/{id}/practices
     */
    @GetMapping("/principles/{id}/practices")
    public ResponseEntity<?> getPracticesByPrinciple(@PathVariable Long id) {
        try {
            List<Practice> practices = practiceRepository
                .findByPrinciple_PrincipleIdAndIsActiveTrueOrderByOrderIndexAsc(id);
            return ResponseEntity.ok(practices);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch practices"));
        }
    }

    /**
     * Get criteria for a practice
     * GET /api/governance/practices/{id}/criteria
     */
    @GetMapping("/practices/{id}/criteria")
    public ResponseEntity<?> getCriteriaByPractice(@PathVariable Long id) {
        try {
            List<Criterion> criteria = criterionRepository
                .findByPractice_PracticeIdAndIsActiveTrueOrderByOrderIndexAsc(id);
            return ResponseEntity.ok(criteria);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch criteria"));
        }
    }
}