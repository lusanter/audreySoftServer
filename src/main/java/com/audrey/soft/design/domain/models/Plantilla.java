package com.audrey.soft.design.domain.models;

import com.audrey.soft.design.application.dtos.CapaDTO;
import com.audrey.soft.design.application.dtos.PlantillaRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Plantilla {
    private String id;
    private String nombre;
    private String descripcion;
    private PlantillaRequestDTO.TipoUso tipoUso;
    private PlantillaRequestDTO.Formato formato;
    private List<String> tags;
    private PlantillaRequestDTO.Origen origen;
    private List<CapaDTO> capas;
    private LocalDateTime fechaCreacion;
    private Boolean activa;
    private String empresaId; // Para plantillas específicas de empresa

    // Constructor para nueva plantilla
    public Plantilla(String nombre, String descripcion, PlantillaRequestDTO.TipoUso tipoUso,
                     PlantillaRequestDTO.Formato formato, List<String> tags,
                     PlantillaRequestDTO.Origen origen, List<CapaDTO> capas, String empresaId) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoUso = tipoUso;
        this.formato = formato;
        this.tags = tags;
        this.origen = origen;
        this.capas = capas;
        this.fechaCreacion = LocalDateTime.now();
        this.activa = true;
        this.empresaId = empresaId;
    }

    // Constructor completo (para reconstruir desde BD)
    public Plantilla(String id, String nombre, String descripcion, PlantillaRequestDTO.TipoUso tipoUso,
                     PlantillaRequestDTO.Formato formato, List<String> tags,
                     PlantillaRequestDTO.Origen origen, List<CapaDTO> capas,
                     LocalDateTime fechaCreacion, Boolean activa, String empresaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoUso = tipoUso;
        this.formato = formato;
        this.tags = tags;
        this.origen = origen;
        this.capas = capas;
        this.fechaCreacion = fechaCreacion;
        this.activa = activa;
        this.empresaId = empresaId;
    }

    // Métodos de negocio
    public void actualizar(String nombre, String descripcion, PlantillaRequestDTO.TipoUso tipoUso,
                          PlantillaRequestDTO.Formato formato, List<String> tags, List<CapaDTO> capas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoUso = tipoUso;
        this.formato = formato;
        this.tags = tags;
        this.capas = capas;
    }

    public void desactivar() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    public boolean esDeEmpresa() {
        return PlantillaRequestDTO.Origen.EMPRESA.equals(this.origen);
    }

    public boolean esSuperAdmin() {
        return PlantillaRequestDTO.Origen.SUPER_ADMIN.equals(this.origen);
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public PlantillaRequestDTO.TipoUso getTipoUso() { return tipoUso; }
    public PlantillaRequestDTO.Formato getFormato() { return formato; }
    public List<String> getTags() { return tags; }
    public PlantillaRequestDTO.Origen getOrigen() { return origen; }
    public List<CapaDTO> getCapas() { return capas; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Boolean getActiva() { return activa; }
    public String getEmpresaId() { return empresaId; }
}