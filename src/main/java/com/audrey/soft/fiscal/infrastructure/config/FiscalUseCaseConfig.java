package com.audrey.soft.fiscal.infrastructure.config;

import com.audrey.soft.fiscal.app.usecases.FiscalConfig.GetFiscalConfigUseCase;
import com.audrey.soft.fiscal.app.usecases.FiscalConfig.UpdateFiscalConfigUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.UpdateComprobanteSerieUseCase;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalSistemaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiscalUseCaseConfig {

    @Bean
    public GetFiscalConfigUseCase getFiscalConfigUseCase(SpringDataFiscalConfigRepository fiscalConfigRepository) {
        return new GetFiscalConfigUseCase(fiscalConfigRepository);
    }

    @Bean
    public UpdateFiscalConfigUseCase updateFiscalConfigUseCase(SpringDataFiscalConfigRepository fiscalConfigRepository) {
        return new UpdateFiscalConfigUseCase(fiscalConfigRepository);
    }

    @Bean
    public CreateComprobanteSerieUseCase createComprobanteSerieUseCase(
            ComprobanteSerieRepositoryPort serieRepository,
            SpringDataFiscalConfigRepository fiscalConfigRepository,
            SpringDataFiscalSistemaRepository fiscalSistemaRepository) {
        return new CreateComprobanteSerieUseCase(serieRepository, fiscalConfigRepository, fiscalSistemaRepository);
    }

    @Bean
    public ListComprobanteSeriesUseCase listComprobanteSeriesUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        return new ListComprobanteSeriesUseCase(serieRepository);
    }

    @Bean
    public UpdateComprobanteSerieUseCase updateComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        return new UpdateComprobanteSerieUseCase(serieRepository);
    }
}
