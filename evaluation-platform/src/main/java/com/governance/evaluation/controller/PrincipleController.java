package com.governance.evaluation.controller;

import com.governance.evaluation.entity.Principle;
import com.governance.evaluation.service.PrincipleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/principles")
@RequiredArgsConstructor
public class PrincipleController {
    
    private final PrincipleService principleService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION', 'EVALUATOR')")
    public ResponseEntity<List<Principle>> getAllPrinciples() {
        List<Principle> principles = principleService.getAllPrinciples();
        return ResponseEntity.ok(principles);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION', 'EVALUATOR')")
    public ResponseEntity<Principle> getPrincipleById(@PathVariable Long id) {
        Principle principle = principleService.getPrincipleById(id);
        return ResponseEntity.ok(principle);
    }
}