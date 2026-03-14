package com.governance.evaluation.service;

import com.governance.evaluation.entity.Organization;
import com.governance.evaluation.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public Map<String, Object> registerOrganization(Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");
        
        if (organizationRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Organization organization = new Organization();
        organization.setEmail(email);
        organization.setPassword(passwordEncoder.encode(password));
        organization.setName(name);
        organization.setIsActive(true);
        
        if (request.containsKey("sector")) {
            organization.setSector(request.get("sector"));
        }
        if (request.containsKey("address")) {
            organization.setAddress(request.get("address"));
        }
        if (request.containsKey("phone")) {
            organization.setPhone(request.get("phone"));
        }
        
        organizationRepository.save(organization);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", organization.getUserId());
        response.put("email", organization.getEmail());
        response.put("name", organization.getName());
        response.put("role", organization.getRole());
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getOrganizationById(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", org.getUserId());
        response.put("email", org.getEmail());
        response.put("name", org.getName());
        response.put("role", org.getRole());
        response.put("sector", org.getSector());
        response.put("address", org.getAddress());
        response.put("phone", org.getPhone());
        response.put("isActive", org.getIsActive());
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(org -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("userId", org.getUserId());
                    dto.put("email", org.getEmail());
                    dto.put("name", org.getName());
                    dto.put("role", org.getRole());
                    dto.put("sector", org.getSector());
                    dto.put("address", org.getAddress());
                    dto.put("phone", org.getPhone());
                    dto.put("isActive", org.getIsActive());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Map<String, Object> updateOrganization(Long id, Map<String, String> request) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        if (request.containsKey("name")) {
            org.setName(request.get("name"));
        }
        if (request.containsKey("sector")) {
            org.setSector(request.get("sector"));
        }
        if (request.containsKey("address")) {
            org.setAddress(request.get("address"));
        }
        if (request.containsKey("phone")) {
            org.setPhone(request.get("phone"));
        }
        
        organizationRepository.save(org);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", org.getUserId());
        response.put("email", org.getEmail());
        response.put("name", org.getName());
        response.put("role", org.getRole());
        
        return response;
    }
    
    @Transactional
    public void deleteOrganization(Long id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found");
        }
        organizationRepository.deleteById(id);
    }
    
    @Transactional
    public void activateOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.setIsActive(true);
        organizationRepository.save(org);
    }
    
    @Transactional
    public void deactivateOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        org.setIsActive(false);
        organizationRepository.save(org);
    }
}