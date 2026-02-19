package com.ms.patient.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.patient.models.Person;
/**
 * Interface de repositório responsável por operações de acesso a dados (CRUD)
 * para a entidade {@link Person}.
 *
 * <p>Estende {@link org.springframework.data.jpa.repository.JpaRepository},
 * fornecendo implementações padrão para métodos de persistência.</p>
 *
 * @author Lucas Edson Machado
 * @since 2025-11-17
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    Optional<Person> findPersonByEmail(String email);

    /*
     * . A consulta para de rodar assim que encontra o primeiro registro e retorna apenas um
     */

    Boolean existsByCpf(String cpf);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

}
