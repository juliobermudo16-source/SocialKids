# Informe de compilación — SocialKids v1.0.0

Resultados **reales**, obtenidos ejecutando las tareas en esta máquina. Ningún dato de este informe
está estimado o simulado.

---

## 1. Entorno de compilación

| Elemento | Valor |
|---|---|
| Fecha de compilación | 2026-08-25 |
| Sistema | Windows 11 Pro 10.0.26200 (x64) |
| JDK | Eclipse Temurin **17.0.20.1+1** |
| Gradle | **8.11.1** (wrapper) |
| Android Gradle Plugin | **8.7.3** |
| Kotlin | **2.0.21** |
| KSP | 2.0.21-1.0.28 |
| Android SDK | platform **35**, build-tools **35.0.0** y **34.0.0** |
| compileSdk / targetSdk / minSdk | 35 / 35 / 24 |

> **Nota sobre el entorno.** La máquina tenía instalado JDK 25, incompatible con AGP 8.7.3, y no
> tenía Android SDK ni Gradle. Para poder verificar de verdad la compilación se instalaron JDK 17,
> el SDK de Android (platform 35 y build-tools 35) y se usó el Gradle wrapper del proyecto.
> Además, `java.io.tmpdir` tuvo que redirigirse a una ruta ASCII (`C:\skbuild\tmp`): el directorio
> temporal por defecto contenía un carácter acentuado y eso impedía a la JVM crear los sockets
> AF_UNIX que necesita el demonio de Gradle.

---

## 2. Tareas ejecutadas

| Tarea | Resultado | Duración |
|---|---|---|
| `./gradlew clean` | **BUILD SUCCESSFUL** | 5 s |
| `./gradlew testDebugUnitTest` | **BUILD SUCCESSFUL** | 30 s |
| `./gradlew lintDebug` | **BUILD SUCCESSFUL** | 2 m 55 s |
| `./gradlew assembleDebug` | **BUILD SUCCESSFUL** | 9 s (incremental) |
| `./gradlew assembleRelease` | **BUILD SUCCESSFUL** | dentro de la ejecución combinada de 3 m 8 s |

---

## 3. Pruebas unitarias

**93 pruebas ejecutadas · 0 fallidas · 0 con error · 0 omitidas.**

Fuente: `app/build/test-results/testDebugUnitTest/*.xml` (13 archivos).

| Clase de prueba | Pruebas | Fallos | Errores |
|---|---:|---:|---:|
| `RostroEngineTest` | 7 | 0 | 0 |
| `EscuchaEngineTest` | 6 | 0 | 0 |
| `PuenteEngineTest` | 5 | 0 | 0 |
| `MensajeEngineTest` | 7 | 0 | 0 |
| `ConflictoEngineTest` | 7 | 0 | 0 |
| `TermometroEngineTest` | 6 | 0 | 0 |
| `EstrellasTest` | 1 | 0 | 0 |
| `ProgresoCalculadoraTest` | 8 | 0 | 0 |
| `DesbloqueoEvaluadorTest` | 8 | 0 | 0 |
| `InsigniaEvaluadorTest` | 7 | 0 | 0 |
| `EstadisticasTest` | 7 | 0 | 0 |
| `ContenidoTest` | 10 | 0 | 0 |
| `JuegoRepositoryTest` | 14 | 0 | 0 |
| **Total** | **93** | **0** | **0** |

### Un fallo real detectado y corregido

La primera ejecución de la batería dio **1 fallo**: `RostroEngineTest > los rasgos sobrantes restan
puntos`. La causa era un defecto real del motor, no de la prueba: la penalización por activar un
rasgo extra innecesario se aplicaba **después** de sumar la bonificación, y el recorte final a 100
la anulaba por completo en los rostros bien construidos.

Se rediseñó `RostroEngine.puntaje` separando la parte geométrica (85 %) de la parte de rasgos
(15 %), con `puntajeExtras` como función propia y comprobable. Tras el cambio, las 93 pruebas
pasan.

### Pruebas instrumentadas

`app/src/androidTest/.../BaseDeDatosTest.kt` contiene 7 pruebas de persistencia real con Room.
**No se han ejecutado**: requieren un dispositivo o emulador Android, que no estaba disponible en
este entorno. Se ejecutan con `./gradlew connectedDebugAndroidTest`.

---

## 4. Análisis estático (lint)

**0 errores · 49 avisos · 1 informativo.**

Informes: `app/build/reports/lint-results-debug.html` y `lint-results-debug.xml`.

| Categoría | Cantidad | Comentario |
|---|---:|---|
| `GradleDependency` | 45 | Existen versiones más nuevas de las dependencias. Se han fijado versiones estables y compatibles entre sí a propósito; no se usan versiones dinámicas. |
| `AndroidGradlePluginVersion` | 3 | Lo mismo aplicado al AGP. |
| `ModifierParameter` | 1 | En `Nima()`, el parámetro `modifier` no es el primero opcional porque `tamanio` es más relevante para quien llama. |
| `AutoboxingStateCreation` | 1 (info) | Un `mutableStateOf` con `Int` en `ActividadViewModel`. |

Ninguna advertencia afecta a la corrección ni a la seguridad de la aplicación. En una primera
pasada había además avisos de recursos sin usar, etiqueta redundante en el manifiesto e icono
adaptativo sin capa monocroma; **los tres se corrigieron** (los textos pasaron a usarse como
descripciones de accesibilidad, se eliminó la etiqueta duplicada y se añadió
`ic_launcher_monochrome`), y por eso los avisos bajaron de 60 a 49.

---

## 5. Artefactos generados

| Archivo | Tamaño | SHA-256 |
|---|---:|---|
| `SocialKids-v1.0.0.apk` (debug, firmado con la clave de depuración) | 18 153 871 B (17,3 MB) | `6639ac4803a48e0ec82c442b9ab40e61e626d051f29788df918c78f3c4657719` |
| `SocialKids-v1.0.0-release-unsigned.apk` | 11 582 108 B (11,0 MB) | `acdb06e40280d2bf26963b5286795861fa502e0cc1fab1e39d445944be0629c5` |

Los mismos hashes están en `deliverables/SHA256SUMS.txt`.

### Verificación del contenido del APK

Se abrió el APK de depuración y se comprobó su interior:

- 159 entradas, con `AndroidManifest.xml`, `resources.arsc` y **15 archivos dex** (multidex).
- Los iconos del launcher están presentes en las cinco densidades
  (`res/mipmap-xxhdpi-v4/ic_launcher.png` verificado).

El APK de release se genera **sin firmar**: firmarlo exige un almacén de claves, que no debe formar
parte del repositorio.

---

## 6. Base de datos

El esquema exportado por Room (`app/schemas/...SocialKidsDatabase/1.json`, versión 1, identityHash
`dc408b061f12356fc1897f382776dbb9`) se transcribió a `database/schema.sql`.

Ambos scripts SQL se **verificaron cargándolos en SQLite**. Las consultas de comprobación
devolvieron exactamente los valores documentados:

| Consulta | Resultado |
|---|---:|
| Misiones completadas | 7 |
| Misiones dominadas | 4 |
| Hitos de rostro | 1 |
| Intensidad media del diario | 5,8 |
| Cartas / insignias | 8 / 4 |
| Días visitados | 4 |

---

## 7. Tamaño del proyecto

| Métrica | Valor |
|---|---:|
| Archivos Kotlin | 63 |
| Líneas de Kotlin | 11 732 |
| Archivos de pantalla y actividad | 20 |
| Entidades Room | 7 |
| Motores de dominio | 6 |
| Misiones | 24 |
| Cartas | 25 |
| Insignias | 12 |

---

## 8. Documentación en PDF

Los tres documentos exigidos se generaron en `docs/pdf/`:

- `MEMORIA_DESCRIPTIVA.pdf`
- `MANUAL_USUARIO.pdf`
- `MANUAL_TECNICO.pdf`

Son PDF reales (formato 1.4, texto seleccionable), generados a partir de los `.md` correspondientes.

---

## 9. Integración continua

`.github/workflows/android.yml` repite esta misma secuencia en `ubuntu-latest` con JDK 17 y
SDK 35, y publica APK, fuentes, informes de pruebas y lint, y PDF como artefactos descargables. Al
empujar una etiqueta `v*` crea además una release de GitHub con el APK adjunto.

### Ejecución verificada en GitHub Actions

El workflow se ejecutó realmente tras el primer push:

| Dato | Valor |
|---|---|
| Repositorio | `juliobermudo16-source/SocialKids` |
| Ejecución | `32814352253` |
| Resultado | **success** |
| Duración | 8 m 30 s |
| Runner | `ubuntu-latest`, JDK 17 Temurin, SDK 35 |

Resumen que publicó el propio workflow:

- Pruebas unitarias: **93 ejecutadas, 0 fallidas, 0 con error, 0 omitidas**
- Lint: **0 errores**
- APK: `SocialKids-v1.0.0.apk`, 17,0 MB

SHA-256 de los APK generados en CI (distintos a los locales porque el APK de depuración se firma
con la clave de depuración del runner y las marcas de tiempo cambian):

| Archivo | SHA-256 |
|---|---|
| `SocialKids-v1.0.0.apk` | `2563ee898d83024d2a17c7529d0c5e57b35ad0cb2f1bfeb1a62f5a8c2ec13832` |
| `SocialKids-v1.0.0-release-unsigned.apk` | `e5b20a63c81bb00e3459e6a549c35bdef929fe00a9e7e70ba1fd16209967d90d` |

Artefactos publicados y descargables desde la pestaña Actions:

| Artefacto | Tamaño |
|---|---:|
| `SocialKids-APK` | 28 914 314 B |
| `SocialKids-fuente` | 320 582 B |
| `SocialKids-informes` | 60 807 B |
| `SocialKids-documentacion` | 61 913 B |

---

## 10. Conclusión

**COMPILACIÓN VERIFICADA.**

Las cuatro tareas exigidas (`clean`, `testDebugUnitTest`, `lintDebug`, `assembleDebug`) se
ejecutaron realmente y terminaron con `BUILD SUCCESSFUL`. Se añadió `assembleRelease`, también
correcto. 93 pruebas en verde, lint sin errores y dos APK generados y verificados.

Lo único **no verificado** en este entorno, y así se declara: las 7 pruebas instrumentadas de Room,
que necesitan un dispositivo o emulador Android, y la ejecución en vivo de la interfaz sobre un
dispositivo real.
