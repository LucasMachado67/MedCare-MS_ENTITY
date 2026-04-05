package com.ms.patient.utils;

import com.ms.patient.models.Person;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {
    public static <T extends Person> Specification<T> filterUsers(String search, String filterBy, String tenantId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro Obrigatório: Tenant
            if (tenantId != null) {
                predicates.add(cb.equal(root.get("tenantId"), tenantId));
            }
            // 2. Filtro Opcional: Search
            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get(filterBy)), searchLower));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
