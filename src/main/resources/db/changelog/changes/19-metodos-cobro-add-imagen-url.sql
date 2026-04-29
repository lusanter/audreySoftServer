-- liquibase formatted sql

-- changeset santer:19-metodos-cobro-add-imagen-url
ALTER TABLE billing.metodos_cobro ADD COLUMN IF NOT EXISTS imagen_url varchar(500);

UPDATE billing.metodos_cobro SET imagen_url = 'https://images.seeklogo.com/logo-png/39/1/yape-app-logo-png_seeklogo-399697.png'  WHERE codigo = 'YAPE';
UPDATE billing.metodos_cobro SET imagen_url = 'https://images.seeklogo.com/logo-png/38/1/plin-logo-png_seeklogo-386806.png'       WHERE codigo = 'PLIN';
-- rollback ALTER TABLE billing.metodos_cobro DROP COLUMN IF EXISTS imagen_url;
