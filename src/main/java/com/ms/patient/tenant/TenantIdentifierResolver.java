package com.ms.patient.tenant;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Implementação de {@link CurrentTenantIdentifierResolver} responsável por
 * informar ao Hibernate qual é o tenant atual da aplicação.
 *
 * <p>Essa classe integra o Hibernate ao {@link TenantContext}, permitindo
 * que o identificador do tenant seja resolvido dinamicamente a cada requisição.</p>
 *
 * <p>Fluxo de funcionamento:</p>
 * <ol>
 *     <li>Um filtro extrai o tenant do JWT ou header.</li>
 *     <li>O tenant é armazenado no {@link TenantContext} (ThreadLocal).</li>
 *     <li>O Hibernate chama {@code resolveCurrentTenantIdentifier()}.</li>
 *     <li>O tenant atual é retornado e aplicado automaticamente nas queries.</li>
 * </ol>
 *
 * <p>Utilizado em conjunto com {@code @TenantId} nas entidades.</p>
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Object>{

    @Override
    public @Nullable String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        return (tenantId != null) ? tenantId : "BOOTSTRAP";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    
}
