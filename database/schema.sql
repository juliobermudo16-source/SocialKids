-- =====================================================================
-- SocialKids - Isla Conecta
-- Esquema real de la base de datos SQLite generada por Room 2.6.1
-- Generado a partir de app/schemas/...SocialKidsDatabase/1.json
-- Version de esquema: 1     identityHash: dc408b061f12356fc1897f382776dbb9
-- =====================================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------------
-- perfil: identidad local del jugador. Una sola fila (id = 1).
-- No guarda nombre real, correo, telefono ni ningun dato personal.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `perfil` (
    `id`              INTEGER NOT NULL,
    `alias`           TEXT    NOT NULL,
    `avatarId`        INTEGER NOT NULL,
    `xp`              INTEGER NOT NULL,
    `creadoEn`        INTEGER NOT NULL,
    `onboardingHecho` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);

-- ---------------------------------------------------------------------
-- progreso_mision: mejor resultado guardado de cada una de las 24 misiones.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `progreso_mision` (
    `misionId`         TEXT    NOT NULL,
    `zonaId`           TEXT    NOT NULL,
    `mejoresEstrellas` INTEGER NOT NULL,
    `mejorPuntaje`     INTEGER NOT NULL,
    `intentos`         INTEGER NOT NULL,
    `completada`       INTEGER NOT NULL,
    `actualizadoEn`    INTEGER NOT NULL,
    PRIMARY KEY(`misionId`)
);

-- ---------------------------------------------------------------------
-- intento: historial completo. De aqui salen las estadisticas y los hitos.
-- hito = logro propio de la mecanica (puente firme, mensaje asertivo
-- perfecto, acuerdo con calma alta, rostro clavado...).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `intento` (
    `id`        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `misionId`  TEXT    NOT NULL,
    `mecanica`  TEXT    NOT NULL,
    `puntaje`   INTEGER NOT NULL,
    `estrellas` INTEGER NOT NULL,
    `hito`      INTEGER NOT NULL,
    `diaEpoch`  INTEGER NOT NULL,
    `creadoEn`  INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_intento_misionId` ON `intento` (`misionId`);
CREATE INDEX IF NOT EXISTS `index_intento_diaEpoch` ON `intento` (`diaEpoch`);

-- ---------------------------------------------------------------------
-- carta: cartas de la coleccion desbloqueadas (25 posibles).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `carta` (
    `cartaId`        TEXT    NOT NULL,
    `desbloqueadaEn` INTEGER NOT NULL,
    PRIMARY KEY(`cartaId`)
);

-- ---------------------------------------------------------------------
-- insignia: insignias conseguidas (12 posibles).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `insignia` (
    `insigniaId`   TEXT    NOT NULL,
    `conseguidaEn` INTEGER NOT NULL,
    PRIMARY KEY(`insigniaId`)
);

-- ---------------------------------------------------------------------
-- animo: Diario de Animo. Alimenta el grafico semanal y las medias.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `animo` (
    `id`         INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `diaEpoch`   INTEGER NOT NULL,
    `emocion`    TEXT    NOT NULL,
    `intensidad` INTEGER NOT NULL,
    `nota`       TEXT    NOT NULL,
    `creadoEn`   INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_animo_diaEpoch` ON `animo` (`diaEpoch`);

-- ---------------------------------------------------------------------
-- visita: un dia por fila. Base del calculo de rachas.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `visita` (
    `diaEpoch` INTEGER NOT NULL,
    PRIMARY KEY(`diaEpoch`)
);

-- Tabla interna que Room crea para validar el esquema.
CREATE TABLE IF NOT EXISTS `room_master_table` (
    `id` INTEGER PRIMARY KEY,
    `identity_hash` TEXT
);
INSERT OR REPLACE INTO `room_master_table` (`id`, `identity_hash`)
VALUES (42, 'dc408b061f12356fc1897f382776dbb9');
