package com.ms.patient.mappers;

import com.ms.patient.dto.MedicCreationDTO;
import com.ms.patient.dto.MedicResponseDTO;
import com.ms.patient.models.Medic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ComponentScan
@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface MedicMapper {

    /**
     * Mapeia MedicRequestDTO -> Entidade Medic.
     * * O MapStruct automaticamente lida com:
     * 1. Herança: Mapeia campos da Person (name, cpf, etc.) para a classe Medic.
     * 2. Aninhamento: O 'address' é mapeado usando o AddressMapper.
     * * NOTA: Se você tiver campos no DTO que não têm setter correspondente
     * na Entidade (ex: passwords), use @Mapping(target = "password", ignore = true)
     * mas neste caso não é necessário.
     */
    @Mapping(source = "address", target = "address") // Mapeia AddressRequestDTO -> Address Entity
    Medic toMedic(MedicCreationDTO dto);

    /**
     * Mapeia Entidade Medic -> MedicResponseDTO.
     * * O MapStruct automaticamente lida com:
     * 1. Herança: Obtém todos os campos da Person (herdada).
     * 2. Aninhamento: O 'address' é mapeado usando o AddressMapper.
     */
    @Mapping(source = "address", target = "address") // Mapeia Address Entity -> AddressResponseDTO
    MedicResponseDTO toMedicResponseDTO(Medic medic);

    List<Medic> toMedicResponse(List<MedicResponseDTO> dtos);
    List<MedicResponseDTO> toDtoResponse(List<Medic> medics);
    void updateMedicFromDto(MedicCreationDTO dto, @MappingTarget Medic medic);
}
