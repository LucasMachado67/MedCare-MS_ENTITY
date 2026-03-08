package com.ms.patient.config;

import com.ms.patient.tenant.TenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateTenantConfig {

    private final TenantIdentifierResolver tenantIdentifierResolver;

    public HibernateTenantConfig(TenantIdentifierResolver tenantIdentifierResolver){
        this.tenantIdentifierResolver = tenantIdentifierResolver;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(){
        return props -> props.put(
                "hibernate.tenant_Identifier_resolver",
                tenantIdentifierResolver
        );
    }
}
