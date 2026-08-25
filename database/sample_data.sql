-- =====================================================================
-- SocialKids - datos de ejemplo
-- Retrato de una jugadora ficticia (alias 'Ada') tras cuatro dias de uso.
-- Sirve para revisar consultas y pantallas sin tener que jugar la partida.
-- Dia epoch de referencia: 20690 (2026-08-25)
-- =====================================================================

DELETE FROM visita; DELETE FROM animo; DELETE FROM insignia;
DELETE FROM carta; DELETE FROM intento; DELETE FROM progreso_mision; DELETE FROM perfil;

-- Perfil: nivel 3 con 268 XP acumulados
INSERT INTO perfil (id, alias, avatarId, xp, creadoEn, onboardingHecho)
VALUES (1, 'Ada', 2, 268, 1787418000000, 1);

-- Progreso: Faro completo, Bosque a medias, primera del Puente empezada
INSERT INTO progreso_mision (misionId, zonaId, mejoresEstrellas, mejorPuntaje, intentos, completada, actualizadoEn) VALUES
  ('m_faro_1','FARO',3,96,1,1,1787400000000),
  ('m_faro_2','FARO',3,100,2,1,1787400000000),
  ('m_faro_3','FARO',2,78,2,1,1787486400000),
  ('m_faro_4','FARO',3,92,1,1,1787486400000),
  ('m_bosque_1','BOSQUE',3,94,1,1,1787572800000),
  ('m_bosque_2','BOSQUE',2,74,1,1,1787572800000),
  ('m_bosque_3','BOSQUE',1,58,2,1,1787659200000),
  ('m_bosque_4','BOSQUE',0,31,1,0,1787659200000);

-- Historial de intentos (origen real de estadisticas e insignias)
INSERT INTO intento (misionId, mecanica, puntaje, estrellas, hito, diaEpoch, creadoEn) VALUES
  ('m_faro_1','ROSTROS',96,3,1,20687,1787421600000),
  ('m_faro_2','TERMOMETRO',64,1,0,20687,1787421600000),
  ('m_faro_2','TERMOMETRO',100,3,0,20687,1787421600000),
  ('m_faro_3','ROSTROS',52,1,0,20688,1787508000000),
  ('m_faro_3','ROSTROS',78,2,0,20688,1787508000000),
  ('m_faro_4','TERMOMETRO',92,3,0,20688,1787508000000),
  ('m_bosque_1','ESCUCHA',94,3,1,20689,1787594400000),
  ('m_bosque_2','ESCUCHA',74,2,0,20689,1787594400000),
  ('m_bosque_3','PUENTE',40,0,0,20690,1787680800000),
  ('m_bosque_3','PUENTE',58,1,0,20690,1787680800000),
  ('m_bosque_4','ESCUCHA',31,0,0,20690,1787680800000);

-- Cartas desbloqueadas: la de bienvenida mas una por mision completada
INSERT INTO carta (cartaId, desbloqueadaEn) VALUES
  ('c_nima',1787421600000),
  ('c_alegria',1787421600000),
  ('c_termometro',1787421600000),
  ('c_miedo',1787508000000),
  ('c_calma',1787508000000),
  ('c_escucha',1787594400000),
  ('c_atencion',1787594400000),
  ('c_pistas',1787680800000);

-- Insignias conseguidas hasta ahora
INSERT INTO insignia (insigniaId, conseguidaEn) VALUES
  ('ins_primer_paso',1787425200000),
  ('ins_explorador',1787598000000),
  ('ins_racha',1787684400000),
  ('ins_diario',1787684400000);

-- Diario de animo de los ultimos cuatro dias
INSERT INTO animo (diaEpoch, emocion, intensidad, nota, creadoEn) VALUES
  (20687,'Ilusion',8,'Primer dia en la isla',1787428800000),
  (20687,'Calma',5,'Despues de respirar',1787428800000),
  (20688,'Enfado',7,'Discusion por el mando',1787515200000),
  (20688,'Calma',4,'Lo hablamos y salio bien',1787515200000),
  (20689,'Alegria',9,'Me eligieron para el equipo',1787601600000),
  (20689,'Verguenza',5,'Me trabe al leer en voz alta',1787601600000),
  (20689,'Tristeza',6,'Lena se fue pronto',1787601600000),
  (20690,'Alegria',7,'Recreo con el grupo nuevo',1787688000000),
  (20690,'Miedo',4,'Manana hay examen',1787688000000),
  (20690,'Calma',3,'Respiracion 4-4-6 antes de dormir',1787688000000);

-- Visitas: cuatro dias seguidos, racha actual = 4
INSERT INTO visita (diaEpoch) VALUES
  (20687), (20688), (20689), (20690);

-- ---------------------------------------------------------------------
-- Consultas de comprobacion
-- ---------------------------------------------------------------------
-- Misiones completadas:
--   SELECT COUNT(*) FROM progreso_mision WHERE completada = 1;            -- 7
-- Misiones dominadas (3 estrellas):
--   SELECT COUNT(*) FROM progreso_mision WHERE mejoresEstrellas >= 3;     -- 4
-- Hitos de rostro:
--   SELECT COUNT(DISTINCT misionId) FROM intento WHERE mecanica='ROSTROS' AND hito=1;  -- 1
-- Intensidad media del diario:
--   SELECT ROUND(AVG(intensidad),1) FROM animo;                           -- 5.8
-- Emocion mas anotada:
--   SELECT emocion, COUNT(*) c FROM animo GROUP BY emocion ORDER BY c DESC LIMIT 1;
