# Base de datos — SocialKids

SQLite gestionada por **Room 2.6.1** · Esquema versión **1** · Archivo `socialkids.db`
Hash de identidad: `dc408b061f12356fc1897f382776dbb9`

Los scripts reales están en [`database/schema.sql`](../database/schema.sql) y
[`database/sample_data.sql`](../database/sample_data.sql). El esquema exportado por Room
(fuente de verdad) vive en `app/schemas/com.socialkids.app.data.local.SocialKidsDatabase/1.json`.

---

## 1. Principio de diseño

La base de datos guarda **solo el progreso del jugador**. El contenido del juego —las 24 misiones,
las 25 cartas, los retos de cada mecánica— es contenido fijo definido en Kotlin
(`data/seed`), y no se duplica en tablas. Así el contenido puede evolucionar con la aplicación sin
necesidad de migraciones, y la base de datos permanece pequeña.

Consecuencia práctica: `progreso_mision.misionId` y `carta.cartaId` son referencias lógicas al
catálogo, no claves foráneas. `ContenidoTest` verifica en cada compilación que todos los
identificadores del catálogo siguen siendo coherentes.

---

## 2. Diagrama

```
              ┌──────────────┐
              │   perfil     │   1 sola fila (id = 1)
              │──────────────│
              │ id (PK)      │
              │ alias        │
              │ avatarId     │
              │ xp           │
              │ creadoEn     │
              │ onboarding   │
              └──────────────┘

┌──────────────────┐   ┌──────────────────┐   ┌──────────────┐
│ progreso_mision  │   │     intento      │   │    carta     │
│──────────────────│   │──────────────────│   │──────────────│
│ misionId (PK)    │   │ id (PK, auto)    │   │ cartaId (PK) │
│ zonaId           │   │ misionId  (idx)  │   │ desbloqueada │
│ mejoresEstrellas │   │ mecanica         │   └──────────────┘
│ mejorPuntaje     │   │ puntaje          │
│ intentos         │   │ estrellas        │   ┌──────────────┐
│ completada       │   │ hito             │   │  insignia    │
│ actualizadoEn    │   │ diaEpoch  (idx)  │   │──────────────│
└──────────────────┘   │ creadoEn         │   │ insigniaId PK│
                       └──────────────────┘   │ conseguidaEn │
                                              └──────────────┘
┌──────────────────┐   ┌──────────────┐
│      animo       │   │   visita     │
│──────────────────│   │──────────────│
│ id (PK, auto)    │   │ diaEpoch (PK)│
│ diaEpoch  (idx)  │   └──────────────┘
│ emocion          │
│ intensidad       │
│ nota             │
│ creadoEn         │
└──────────────────┘
```

---

## 3. Tablas

### 3.1 `perfil`

Identidad local del jugador. Una única fila con `id = 1`.

| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK | siempre 1 |
| `alias` | TEXT | apodo, 2–16 caracteres; **nunca el nombre real** |
| `avatarId` | INTEGER | 0–7, índice del avatar dibujado con Canvas |
| `xp` | INTEGER | experiencia acumulada |
| `creadoEn` | INTEGER | epoch en milisegundos |
| `onboardingHecho` | INTEGER | 0/1 |

No hay correo, teléfono, dirección, fecha de nacimiento ni ningún otro dato personal.
El alias se recorta a 16 caracteres y, si queda vacío, se sustituye por «Explorador».

### 3.2 `progreso_mision`

Mejor resultado de cada una de las 24 misiones. Se actualiza con `@Upsert`, de modo que repetir una
misión no crea filas nuevas.

| Columna | Tipo | Notas |
|---|---|---|
| `misionId` | TEXT PK | por ejemplo `m_faro_1` |
| `zonaId` | TEXT | nombre del enum `ZonaId` |
| `mejoresEstrellas` | INTEGER | 0–3, se guarda el máximo histórico |
| `mejorPuntaje` | INTEGER | 0–100, máximo histórico |
| `intentos` | INTEGER | contador acumulado |
| `completada` | INTEGER | 1 en cuanto se logra al menos una estrella |
| `actualizadoEn` | INTEGER | epoch ms |

Guardar el **máximo** y no el último resultado es una decisión pedagógica: un mal intento nunca
borra un logro anterior.

### 3.3 `intento`

Historial completo. Es la tabla de la que salen las estadísticas y las insignias de habilidad.

| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `misionId` | TEXT, indexado | |
| `mecanica` | TEXT | `ROSTROS`, `ESCUCHA`, `PUENTE`, `MENSAJE`, `CONFLICTO`, `TERMOMETRO` |
| `puntaje` | INTEGER | 0–100 |
| `estrellas` | INTEGER | 0–3 |
| `hito` | INTEGER | 1 si se cumplió el logro propio de la mecánica |
| `diaEpoch` | INTEGER, indexado | día desde 1970-01-01 |
| `creadoEn` | INTEGER | epoch ms |

**Qué es un hito**, por mecánica:

| Mecánica | Condición |
|---|---|
| Rostros | 3 estrellas |
| Escucha | 3 estrellas |
| Puente | los tres tablones correctos |
| Mensaje | estructura completa, tono asertivo y ≥ 88 puntos |
| Conflicto | desenlace de acuerdo con calma ≥ 50 |
| Termómetro | ≥ 88 puntos |

Los hitos se cuentan con `COUNT(DISTINCT misionId)`, de modo que repetir la misma misión no infla
el contador.

### 3.4 `carta` e `insignia`

Dos tablas idénticas en forma: identificador de texto como clave primaria y marca de tiempo. Las
inserciones usan `OnConflictStrategy.IGNORE`, así que desbloquear dos veces la misma carta no
duplica nada.

### 3.5 `animo`

Diario de ánimo. Alimenta el gráfico semanal, la intensidad media y el reparto por emoción.

| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK AUTOINCREMENT | |
| `diaEpoch` | INTEGER, indexado | |
| `emocion` | TEXT | una de las 8 emociones del diario |
| `intensidad` | INTEGER | acotada a 1–10 en el repositorio |
| `nota` | TEXT | recortada a 140 caracteres |
| `creadoEn` | INTEGER | epoch ms |

Se permiten varias anotaciones el mismo día; el gráfico las promedia.

### 3.6 `visita`

Una fila por día en que se abrió la aplicación. Base del cálculo de rachas. La clave primaria es el
propio día, así que registrar varias visitas en la misma jornada no cuenta más de una vez.

---

## 4. Cómo se calcula cada cifra

Ninguna estadística de la interfaz está escrita a mano.

| Dato mostrado | Origen |
|---|---|
| Nivel y barra de nivel | `perfil.xp` → `ProgresoCalculadora` |
| Misiones completadas | `COUNT(*) FROM progreso_mision WHERE completada = 1` |
| Misiones dominadas | `COUNT(*) WHERE mejoresEstrellas >= 3` |
| Porcentaje de zona | misiones completadas de la zona / 4 |
| Zonas completadas | evaluación por zona sobre `progreso_mision` |
| Cartas e insignias | `COUNT(*)` de sus tablas |
| Racha actual y mejor racha | `visita.diaEpoch` → `RachaCalculadora` |
| Gráfico de 7 días | media de `animo.intensidad` por día |
| Intensidad media | `AVG(animo.intensidad)` |
| Emoción más anotada | `GROUP BY emocion ORDER BY COUNT(*) DESC` |
| Hitos por habilidad | `COUNT(DISTINCT misionId) FROM intento WHERE mecanica = ? AND hito = 1` |

---

## 5. Consultas de ejemplo

```sql
-- Progreso por zona
SELECT zonaId,
       COUNT(*)                                   AS intentadas,
       SUM(CASE WHEN completada = 1 THEN 1 ELSE 0 END) AS completadas,
       SUM(CASE WHEN mejoresEstrellas >= 3 THEN 1 ELSE 0 END) AS dominadas
FROM progreso_mision
GROUP BY zonaId;

-- Evolución del ánimo en la última semana
SELECT diaEpoch, ROUND(AVG(intensidad), 1) AS media, COUNT(*) AS anotaciones
FROM animo
WHERE diaEpoch >= (SELECT MAX(diaEpoch) FROM animo) - 6
GROUP BY diaEpoch
ORDER BY diaEpoch;

-- Mecánicas donde más cuesta avanzar
SELECT mecanica, ROUND(AVG(puntaje), 1) AS media, COUNT(*) AS intentos
FROM intento
GROUP BY mecanica
ORDER BY media ASC;

-- Misiones candidatas a repaso
SELECT misionId, mejoresEstrellas
FROM progreso_mision
WHERE completada = 1 AND mejoresEstrellas < 3
ORDER BY mejoresEstrellas;
```

---

## 6. Datos de ejemplo

`database/sample_data.sql` reproduce el estado de una jugadora ficticia («Ada») tras cuatro días:
nivel 3 con 268 XP, el Faro completo, el Bosque a medias, 8 cartas, 4 insignias, 10 anotaciones de
ánimo y una racha de 4 días.

Se puede cargar y comprobar con:

```bash
sqlite3 prueba.db < database/schema.sql
sqlite3 prueba.db < database/sample_data.sql
sqlite3 prueba.db "SELECT COUNT(*) FROM progreso_mision WHERE completada = 1;"   -- 7
```

Los scripts se han verificado cargándolos en SQLite: las consultas de comprobación incluidas al
final de `sample_data.sql` devuelven los valores anotados como comentario.

---

## 7. Migraciones

La versión actual del esquema es la **1**, exportada a `app/schemas/`. El constructor usa
`fallbackToDestructiveMigration()`, adecuado durante el desarrollo: si el esquema cambia, la base se
recrea vacía.

Para una versión publicada en tienda habría que sustituirlo por migraciones explícitas
(`Migration(1, 2)`), y aprovechar los ficheros JSON exportados para escribir pruebas de migración
con `MigrationTestHelper`.

---

## 8. Copias de seguridad y privacidad

`android:allowBackup="true"` con reglas restringidas en `res/xml/reglas_copia.xml` y
`res/xml/reglas_extraccion_datos.xml`: solo se incluye `socialkids.db` y las preferencias.

Como la base no contiene ningún dato personal —solo un apodo elegido por el niño, un número de
avatar y su progreso— una copia del dispositivo no expone información sensible. «Borrar mi
progreso» en Ajustes vacía las siete tablas.
