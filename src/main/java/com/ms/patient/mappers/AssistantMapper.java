package com.ms.patient.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.ComponentScan;

import com.ms.patient.dto.AssistantCreationDTO;
import com.ms.patient.dto.AssistantResponseDTO;
import com.ms.patient.models.Assistant;

@ComponentScan
@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface AssistantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(source = "address", target = "address") // Mapeia AddressRequestDTO -> Address Entity
    Assistant toAssistant(AssistantCreationDTO dto);

    
    @Mapping(source = "address", target = "address") // Mapeia Address Entity -> AddressResponseDTO
    AssistantResponseDTO toAssistantResponseDTO(Assistant assistant);

    @Mapping(target = "tenantId", ignore = true)
    Assistant toEntity(AssistantResponseDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    List<Assistant> toAssistantResponse(List<AssistantResponseDTO> dtos);
    List<AssistantResponseDTO> toDtoResponse(List<Assistant> assistants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateAssistantFromDto(AssistantCreationDTO dto, @MappingTarget Assistant assistant);

}
