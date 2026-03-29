package com.audrey.soft.tenant.domain.models;

/**
 * Define el tipo o rubro del negocio. Un negocio RETAIL mostrará un software
 * de caja rápida, mientras que RESTAURANT pedirá control de comandas y mesas.
 */
public enum VerticalType {
    RETAIL,
    RESTAURANT,
    SERVICES
}
