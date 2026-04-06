-- changeset santer:13-mesas-add-zona
ALTER TABLE restaurant.mesas ADD COLUMN IF NOT EXISTS zona varchar(50) NOT NULL DEFAULT 'GENERAL';
-- rollback ALTER TABLE restaurant.mesas DROP COLUMN IF EXISTS zona;
