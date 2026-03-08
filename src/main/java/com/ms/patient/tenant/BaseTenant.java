package com.ms.patient.tenant;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
/**
 * Classe base para suporte a Multi-Tenancy na aplicação.
 *
 * <p>Esta classe define o campo {@code tenantId}, responsável por identificar
 * o tenant ao qual a entidade pertence. Todas as entidades que
 * estendem esta classe passam automaticamente a possuir controle de segregação
 * de dados por tenant.</p>
 *
 * <p>A anotação {@link TenantId} do Hibernate é utilizada para indicar que
 * o campo será usado como identificador do tenant no contexto de multi-tenancy
 * baseado em coluna.</p>
 *
 * <p>A anotação {@link MappedSuperclass} indica que esta classe não é uma
 * entidade por si só, mas que seus atributos serão mapeados nas entidades
 * filhas que a estenderem.</p>
 *
 * <p><b>Objetivo:</b> Garantir isolamento de dados entre diferentes clientes
 * (tenants) dentro da mesma base de dados.</p>
 *
 * @author Lucas Edson Machado
 */
@MappedSuperclass
public abstract class BaseTenant {
    
    @Column(name = "tenant_id")
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
