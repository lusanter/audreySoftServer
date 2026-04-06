package com.audrey.soft.billing.infrastructure.config;

import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.billing.app.usecases.Venta.ListVentasUseCase;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingUseCaseConfig {

    @Bean
    public ListVentasUseCase listVentasUseCase(VentaRepositoryPort ventaRepository) {
        return new ListVentasUseCase(ventaRepository);
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
    public ListMetodosCobroUseCase listMetodosCobroUseCase(MetodoCobroRepositoryPort metodoCobroRepository) {
        return new ListMetodosCobroUseCase(metodoCobroRepository);
    }
}
