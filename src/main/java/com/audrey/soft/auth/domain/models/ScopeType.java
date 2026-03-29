package com.audrey.soft.auth.domain.models;

/**
 * Define el nivel organizativo o "alcance" en el que se ejerce un rol.
 */
public enum ScopeType {
    /** 
     * Acceso a todo el sistema Audrey (Reservado normalmente para SUPER_ADMIN). 
     * Cuando el scope es PLATAFORMA, el scope_id es null.
     */
    PLATAFORMA,

    /** 
     * Acceso a una Empresa completa y a todas sus sucursales subyacentes.
     * El scope_id corresponderá al UUID de la Empresa.
     */
    EMPRESA,

    /** 
     * Acceso a una sucursal o local específico.
     * El scope_id corresponderá al UUID de la Sucursal.
     */
    SUCURSAL
}
