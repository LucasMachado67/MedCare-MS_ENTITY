package com.ms.patient.repositories;

import com.ms.patient.models.Medic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 'Interface' de repositório responsável por operações de acesso a dados (CRUD)
 * para a entidade {@link Medic}.
 *
 * <p>Estende {@link org.springframework.data.jpa.repository.JpaRepository},
 * fornecendo implementações padrão para métodos de persistência.</p>
 *
 * @author Lucas Edson Machado
 * @since 2025-11-17
 */
@Repository
public interface MedicRepository extends JpaRepository<Medic, Long> {

    Boolean existsByCrm(String crm);
    @Query(value = "SELECT * FROM medics m JOIN person p ON m.person_id = p.id WHERE p.tenant_id = :tid", nativeQuery = true)
    List<Medic> findAllByTenantId(@Param("tid") String tenantId);
}
