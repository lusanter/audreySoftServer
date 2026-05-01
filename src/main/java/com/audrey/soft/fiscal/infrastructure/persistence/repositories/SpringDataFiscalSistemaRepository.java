package com.audrey.soft.fiscal.infrastructure.persistence.repositories;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.FiscalSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFiscalSistemaRepository extends JpaRepository<FiscalSistemaEntity, String> {
}
