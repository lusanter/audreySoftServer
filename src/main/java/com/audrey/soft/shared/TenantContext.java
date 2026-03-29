package com.audrey.soft.shared;

import java.util.UUID;

/**
 * Contexto de tenant por request.
 * Limpiado al final de cada request por el JwtAuthenticationFilter.
 */
public class TenantContext {

    private TenantContext() {}

    private static final ThreadLocal<UUID> empresaId = new ThreadLocal<>();
    private static final ThreadLocal<UUID> sucursalId = new ThreadLocal<>();

    public static UUID getEmpresaId() { return empresaId.get(); }
    public static void setEmpresaId(UUID id) { empresaId.set(id); }

    public static UUID getSucursalId() { return sucursalId.get(); }
    public static void setSucursalId(UUID id) { sucursalId.set(id); }

    public static void clear() {
        empresaId.remove();
        sucursalId.remove();
    }
}
