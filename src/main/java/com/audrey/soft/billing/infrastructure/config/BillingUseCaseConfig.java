package com.audrey.soft.billing.infrastructure.config;

import com.audrey.soft.billing.app.usecases.MetodoCobro.CreateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.UpdateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase;
import com.audrey.soft.billing.app.usecases.Venta.CreateVentaDirectaUseCase;
import com.audrey.soft.billing.app.usecases.Venta.GetVentaByIdUseCase;
import com.audrey.soft.billing.app.usecases.Venta.ListVentasUseCase;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataImpuestoTipoRepository;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingUseCaseConfig {

    @Bean
    public ListVentasUseCase listVentasUseCase(VentaRepositoryPort ventaRepository) {
        return new ListVentasUseCase(ventaRepository);
    }

    @Bean
    public CreateVentaDirectaUseCase createVentaDirectaUseCase(VentaRepositoryPort ventaRepository,
                                                                ComprobanteSerieRepositoryPort serieRepository,
                                                                ProductoRepositoryPort productoRepository,
                                                                StockMovementRepositoryPort stockMovementRepository,
                                                                SpringDataFiscalConfigRepository fiscalConfigRepository,
                                                                SpringDataImpuestoTipoRepository impuestoTipoRepository) {
        return new CreateVentaDirectaUseCase(ventaRepository, serieRepository, productoRepository,
                stockMovementRepository, fiscalConfigRepository, impuestoTipoRepository);
    }

    @Bean
    public BuscarVentasUseCase buscarVentasUseCase(VentaRepositoryPort ventaRepository) {
        return new BuscarVentasUseCase(ventaRepository);
    }

    @Bean
    public GetVentaByIdUseCase getVentaByIdUseCase(VentaRepositoryPort ventaRepository) {
        return new GetVentaByIdUseCase(ventaRepository);
    }

    @Bean
    public ListMetodosCobroUseCase listMetodosCobroUseCase(MetodoCobroRepositoryPort repo) {
        return new ListMetodosCobroUseCase(repo);
    }

    @Bean
    public CreateMetodoCobroUseCase createMetodoCobroUseCase(MetodoCobroRepositoryPort repo) {
        return new CreateMetodoCobroUseCase(repo);
    }

    @Bean
    public UpdateMetodoCobroUseCase updateMetodoCobroUseCase(MetodoCobroRepositoryPort repo) {
        return new UpdateMetodoCobroUseCase(repo);
    }
}
