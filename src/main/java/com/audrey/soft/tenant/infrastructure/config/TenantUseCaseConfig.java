package com.audrey.soft.tenant.infrastructure.config;

import com.audrey.soft.tenant.app.mappers.EmpresaMapper;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.app.usecases.CreateEnterpriseUseCase;
import com.audrey.soft.tenant.app.usecases.CreateBranchUseCase;
import com.audrey.soft.tenant.app.usecases.ListEnterprisesUseCase;
import com.audrey.soft.tenant.app.usecases.ListBranchesUseCase;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantUseCaseConfig {

    @Bean
    public CreateEnterpriseUseCase createEnterpriseUseCase(EmpresaRepositoryPort empresaRepository,
                                                   EmpresaMapper empresaMapper) {
        return new CreateEnterpriseUseCase(empresaRepository, empresaMapper);
    }

    @Bean
    public ListEnterprisesUseCase listEnterprisesUseCase(EmpresaRepositoryPort empresaRepository,
                                                       EmpresaMapper empresaMapper) {
        return new ListEnterprisesUseCase(empresaRepository, empresaMapper);
    }

    @Bean
    public CreateBranchUseCase createBranchUseCase(SucursalRepositoryPort sucursalRepository,
                                                     EmpresaRepositoryPort empresaRepository,
                                                     SucursalMapper sucursalMapper) {
        return new CreateBranchUseCase(sucursalRepository, empresaRepository, sucursalMapper);
    }

    @Bean
    public ListBranchesUseCase listBranchesUseCase(SucursalRepositoryPort sucursalRepository,
                                                           SucursalMapper sucursalMapper) {
        return new ListBranchesUseCase(sucursalRepository, sucursalMapper);
    }
}
