package com.audrey.soft.restaurant.app.usecases.Mesa;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;

import java.util.UUID;

public class DeleteMesaUseCase {

    private final MesaRepositoryPort mesaRepository;

    public DeleteMesaUseCase(MesaRepositoryPort mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public void execute(UUID mesaId) {
        var mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + mesaId));
        if (mesa.getEstado() == EstadoMesa.OCUPADA) {
            throw new IllegalStateException("No se puede eliminar una mesa ocupada.");
        }
        mesaRepository.deleteById(mesaId);
    }
}
