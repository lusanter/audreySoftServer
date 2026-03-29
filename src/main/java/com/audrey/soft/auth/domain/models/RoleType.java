package com.audrey.soft.auth.domain.models;

/**
 * Catálogo maestro de roles posibles en el sistema Audrey.
 * (En un futuro nivel 4 esto podría volverse una tabla dinámica, pero para
 * empezar, un Enum estandarizado es robusto y seguro).
 */
public enum RoleType {
    /** Dueño / Configuración general de toda la cuenta Audrey (Scope: Plataforma) */
    SUPER_ADMIN,

    /** Administrador principal de una Empresa (Scope: Empresa) */
    ADMIN,

    /** Administrador o Supervisor de varias zonas (Scope: Empresa) */
    GERENTE_REGIONAL,

    /** Responsable total de una sucursal física (Scope: Sucursal) */
    ENCARGADO,

    /** Operador de caja transaccional (Scope: Sucursal) */
    CAJERO,

    /** Personal de atención de piso y pedidos (Scope: Sucursal) */
    MOZO,

    /** Personal de cocina y recepción de órdenes (Scope: Sucursal) */
    COCINERO
}
