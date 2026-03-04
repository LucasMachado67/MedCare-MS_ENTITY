package com.ms.patient.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.patient.dto.CompanyCreationDTO;
import com.ms.patient.dto.StatusResponse;
import com.ms.patient.models.CompanyProfile;
import com.ms.patient.repositories.CompanyProfileRepository;
import com.ms.patient.tenant.TenantContext;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("company")
public class CompanyController {

    @Autowired
    private CompanyProfileRepository repository;

    @GetMapping("/status")
    public ResponseEntity<?> checkCompanyStatus(){
        String tenantId = TenantContext.getCurrentTenant();

        Optional<CompanyProfile> profile = repository.findById(tenantId);
        
        if(profile.isEmpty() || profile.get().getCnpj() == null){
            return ResponseEntity.ok(new StatusResponse("INCOMPLETE_PROFILE"));
        }
        return ResponseEntity.ok(new StatusResponse("READY"));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(@RequestBody CompanyCreationDTO dto) {
        //String tenantId = TenantContext.getCurrentTenant();
        // Lógica para salvar CNPJ, Address, etc no Entity Service
        return ResponseEntity.ok().build();
    }
}
