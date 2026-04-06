package com.audrey.soft.restaurant.infrastructure.config;

import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.app.mappers.MesaMapper;
import com.audrey.soft.restaurant.app.usecases.Comanda.*;
import com.audrey.soft.restaurant.app.usecases.Mesa.CreateMesaUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.DeleteMesaUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.ListMesasUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.UpdateMesaUseCase;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantUseCaseConfig {

    @Bean
    public CreateMesaUseCase createMesaUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        return new CreateMesaUseCase(mesaRepository, mesaMapper);
    }

    @Bean
    public ListMesasUseCase listMesasUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        return new ListMesasUseCase(mesaRepository, mesaMapper);
    }

    @Bean
    public UpdateMesaUseCase updateMesaUseCase(MesaRepositoryPort mesaRepository, MesaMapper mesaMapper) {
        return new UpdateMesaUseCase(mesaRepository, mesaMapper);
    }

    @Bean
    public DeleteMesaUseCase deleteMesaUseCase(MesaRepositoryPort mesaRepository) {
        return new DeleteMesaUseCase(mesaRepository);
    }

    @Bean
    public AbrirComandaUseCase abrirComandaUseCase(ComandaRepositoryPort comandaRepository,
                                                   MesaRepositoryPort mesaRepository,
                                                   ComandaMapper comandaMapper) {
        return new AbrirComandaUseCase(comandaRepository, mesaRepository, comandaMapper);
    }

    @Bean
    public AgregarItemUseCase agregarItemUseCase(ComandaRepositoryPort comandaRepository,
                                                 ProductoRepositoryPort productoRepository,
                                                 ComandaMapper comandaMapper) {
        return new AgregarItemUseCase(comandaRepository, productoRepository, comandaMapper);
    }

    @Bean
    public CerrarComandaUseCase cerrarComandaUseCase(ComandaRepositoryPort comandaRepository,
                                                     MesaRepositoryPort mesaRepository,
                                                     VentaRepositoryPort ventaRepository,
                                                     ComprobanteSerieRepositoryPort serieRepository,
                                                     ProductoRepositoryPort productoRepository,
                                                     StockMovementRepositoryPort stockMovementRepository,
                                                     ComandaMapper comandaMapper) {
        return new CerrarComandaUseCase(comandaRepository, mesaRepository, ventaRepository,
                serieRepository, productoRepository, stockMovementRepository, comandaMapper);
    }

    @Bean
    public MoverItemSubcuentaUseCase moverItemSubcuentaUseCase(ComandaRepositoryPort comandaRepository,
                                                               ComandaMapper comandaMapper) {
        return new MoverItemSubcuentaUseCase(comandaRepository, comandaMapper);
    }

    @Bean
    public ListComandasUseCase listComandasUseCase(ComandaRepositoryPort comandaRepository,
                                                   ComandaMapper comandaMapper) {
        return new ListComandasUseCase(comandaRepository, comandaMapper);
    }
}
