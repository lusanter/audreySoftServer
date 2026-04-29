package com.audrey.soft.billing.infrastructure.config;

import com.audrey.soft.billing.app.usecases.MetodoCobro.CreateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.UpdateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.billing.app.usecases.Serie.UpdateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase;
import com.audrey.soft.billing.app.usecases.Venta.CreateVentaDirectaUseCase;
import com.audrey.soft.billing.app.usecases.Venta.ListVentasUseCase;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
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
                                                                StockMovementRepositoryPort stockMovementRepository) {
        return new CreateVentaDirectaUseCase(ventaRepository, serieRepository, productoRepository, stockMovementRepository);
    }

    @Bean
    public UpdateComprobanteSerieUseCase updateComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        return new UpdateComprobanteSerieUseCase(serieRepository);
    }

    @Bean
    public com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase buscarVentasUseCase(VentaRepositoryPort ventaRepository) {
        return new com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase(ventaRepository);
    }

    @Bean
    public CreateComprobanteSerieUseCase createComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        return new CreateComprobanteSerieUseCase(serieRepository);
    }

    @Bean
    public ListComprobanteSeriesUseCase listComprobanteSeriesUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        return new ListComprobanteSeriesUseCase(serieRepository);
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
