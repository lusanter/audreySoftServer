package com.audrey.soft.restaurant.app.dtos;

import java.util.UUID;

public record MoverItemRequest(
        UUID itemId,
        String subCuenta   // 'A', 'B', 'C', null = sin subcuenta
) {}
