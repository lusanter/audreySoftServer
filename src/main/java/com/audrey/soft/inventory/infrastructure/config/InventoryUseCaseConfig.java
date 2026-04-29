package com.audrey.soft.inventory.infrastructure.config;

import com.audrey.soft.inventory.app.mappers.CategoriaMapper;
import com.audrey.soft.inventory.app.mappers.ClienteMapper;
import com.audrey.soft.inventory.app.mappers.ProductoMapper;
import com.audrey.soft.inventory.app.usecases.Categoria.CreateCategoriaUseCase;
import com.audrey.soft.inventory.app.usecases.Categoria.ListCategoriasUseCase;
import com.audrey.soft.inventory.app.usecases.Categoria.UpdateCategoriaUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.BuscarClientesUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.CreateClienteUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.ListClientesUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.UpdateClienteUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.CreateProductoUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.ListProductosUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.UpdateProductoUseCase;
import com.audrey.soft.inventory.app.usecases.StockMovement.*;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataProductoRepository;
import com.audrey.soft.inventory.domain.ports.CategoriaRepositoryPort;
import com.audrey.soft.inventory.domain.ports.ClienteRepositoryPort;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfig {

    @Bean
    public CreateCategoriaUseCase createCategoriaUseCase(CategoriaRepositoryPort repo, CategoriaMapper mapper) {
        return new CreateCategoriaUseCase(repo, mapper);
    }

    @Bean
    public UpdateCategoriaUseCase updateCategoriaUseCase(CategoriaRepositoryPort repo, CategoriaMapper mapper) {
        return new UpdateCategoriaUseCase(repo, mapper);
    }

    @Bean
    public ListCategoriasUseCase listCategoriasUseCase(CategoriaRepositoryPort repo, CategoriaMapper mapper) {
        return new ListCategoriasUseCase(repo, mapper);
    }

    @Bean
    public CreateProductoUseCase createProductoUseCase(ProductoRepositoryPort repo, ProductoMapper mapper,
                                                       StockMovementRepositoryPort movRepo) {
        return new CreateProductoUseCase(repo, mapper, movRepo);
    }

    @Bean
    public UpdateProductoUseCase updateProductoUseCase(ProductoRepositoryPort repo, ProductoMapper mapper) {
        return new UpdateProductoUseCase(repo, mapper);
    }

    @Bean
    public ListProductosUseCase listProductosUseCase(ProductoRepositoryPort repo, ProductoMapper mapper) {
        return new ListProductosUseCase(repo, mapper);
    }

    @Bean
    public CreateClienteUseCase createClienteUseCase(ClienteRepositoryPort repo, ClienteMapper mapper) {
        return new CreateClienteUseCase(repo, mapper);
    }

    @Bean
    public UpdateClienteUseCase updateClienteUseCase(ClienteRepositoryPort repo, ClienteMapper mapper) {
        return new UpdateClienteUseCase(repo, mapper);
    }

    @Bean
    public BuscarClientesUseCase buscarClientesUseCase(ClienteRepositoryPort repo, ClienteMapper mapper) {
        return new BuscarClientesUseCase(repo, mapper);
    }

    @Bean
    public ListClientesUseCase listClientesUseCase(ClienteRepositoryPort repo, ClienteMapper mapper) {
        return new ListClientesUseCase(repo, mapper);
    }

    @Bean
    public ListStockMovementsUseCase listStockMovementsUseCase(StockMovementRepositoryPort repo) {
        return new ListStockMovementsUseCase(repo);
    }

    @Bean
    public CreateStockEntradaUseCase createStockEntradaUseCase(ProductoRepositoryPort productoRepo,
                                                                StockMovementRepositoryPort movRepo) {
        return new CreateStockEntradaUseCase(productoRepo, movRepo);
    }

    @Bean
    public GetInventoryKpiUseCase getInventoryKpiUseCase(SpringDataProductoRepository productoRepository) {
        return new GetInventoryKpiUseCase(productoRepository);
    }

    @Bean
    public ListAjusteMotivosUseCase listAjusteMotivosUseCase(SpringDataAjusteMotivoRepository repo) {
        return new ListAjusteMotivosUseCase(repo);
    }

    @Bean
    public CreateStockAjusteUseCase createStockAjusteUseCase(ProductoRepositoryPort productoRepo,
                                                            StockMovementRepositoryPort movRepo,
                                                            SpringDataAjusteMotivoRepository motivoRepo) {
        return new CreateStockAjusteUseCase(productoRepo, movRepo, motivoRepo);
    }

    @Bean
    public CreateAjusteMotivoUseCase createAjusteMotivoUseCase(SpringDataAjusteMotivoRepository repo) {
        return new CreateAjusteMotivoUseCase(repo);
    }

    @Bean
    public UpdateAjusteMotivoUseCase updateAjusteMotivoUseCase(SpringDataAjusteMotivoRepository repo) {
        return new UpdateAjusteMotivoUseCase(repo);
    }
}
