package com.ms.patient.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.patient.models.CompanyProfile;

@Repository
public interface CompanyProfileRepository  extends JpaRepository<CompanyProfile, String>{


}
