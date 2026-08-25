# SocialKids — Isla Conecta

Aplicación Android educativa para niños y niñas de **8 a 12 años**. Entrena tres cosas concretas:
**empatía**, **comunicación asertiva** y **habilidades sociales**. No es un cuestionario con dibujos:
es una isla que se explora, seis zonas que se abren, veinticuatro misiones que se **tocan, arrastran,
construyen y negocian**, y una colección de cartas que se desbloquea con acciones reales.

![Icono](app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

---

## Qué es

> La Isla Conecta se quedó muda: la gente dejó de entenderse. **Nima**, la criatura guía,
> te pide ayuda para devolverle a la isla las palabras que unen.

Esa es la narrativa. Debajo hay seis motores de dominio reales, una base de datos Room,
y una interfaz Jetpack Compose donde **todas las ilustraciones están dibujadas con Canvas**:
ni una sola imagen descargada de internet, ni un solo recurso externo.

### Las seis zonas

| Zona | Habilidad | Se abre con |
|---|---|---|
| Faro de las Emociones | Reconocer y nombrar emociones | 0 XP |
| Bosque que Escucha | Escucha activa | 60 XP |
| Puente de la Empatía | Tomar la perspectiva del otro | 150 XP |
| Plaza de las Palabras | Comunicación asertiva (mensaje-yo) | 260 XP |
| Taller de Acuerdos | Resolución de conflictos | 390 XP |
| Mirador de la Amistad | Amistad, inclusión y decir basta | 540 XP |

### Las seis mecánicas

Ninguna es "elige la respuesta correcta":

1. **Estudio de Rostros** — construyes una cara moviendo cuatro ejes (cejas, ojos, boca, energía)
   y el motor mide la distancia a la emoción objetivo. La cara se redibuja en vivo.
2. **Detective de Escucha** — escuchas un relato, rescatas los datos que **de verdad** se dijeron
   (precisión y cobertura, F1) y eliges cómo responder.
3. **Puente de la Empatía** — arrastras tres piezas a los tablones *siente / piensa / necesita*.
   Hay piezas trampa que son juicios o soluciones rápidas.
4. **Constructor de Mensajes** — armas un mensaje-yo con fichas. El motor clasifica la frase
   resultante como pasiva, agresiva o asertiva y explica por qué.
5. **Simulador de Conflicto** — máquina de estados con tres variables vivas (calma, confianza,
   acuerdo). El desenlace no está escrito: lo calculan los umbrales.
6. **Termómetro Emocional** — mides la intensidad de una escena de 0 a 10 y eliges una estrategia
   proporcional a esa intensidad.

### Además

- **25 cartas coleccionables** que se desbloquean completando misiones reales.
- **12 insignias** con reglas evaluadas sobre estadísticas persistidas.
- **Diario de ánimo** con gráfico semanal calculado desde la base de datos.
- **Rincón de calma**: respiración 4-4-6 animada, disponible siempre.
- **Modo repaso** con las misiones donde quedan estrellas por ganar.
- **8 avatares** dibujados con Canvas, cada uno con su accesorio.

---

## Privacidad

La aplicación **no declara ningún permiso** en el manifiesto.

- Sin internet, sin cuentas, sin anuncios, sin analítica, sin backend.
- Sin correo, teléfono, dirección, ubicación, contactos, cámara ni micrófono.
- Solo se guardan un apodo, un avatar y el progreso, y todo se queda en el dispositivo.
- «Borrar mi progreso» en Ajustes elimina absolutamente todo.

---

## Compilar

Requisitos: **JDK 17**, Android SDK con **platform 35** y **build-tools 35.0.0**. `minSdk 24`.

```bash
./gradlew clean testDebugUnitTest lintDebug assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

### Compilación en GitHub Actions

El workflow [`.github/workflows/android.yml`](.github/workflows/android.yml) se ejecuta en cada
push y genera el APK automáticamente. Sube como artefactos:

- `SocialKids-APK` — el APK de depuración, el de release sin firmar y `SHA256SUMS.txt`
- `SocialKids-fuente` — el código fuente empaquetado
- `SocialKids-informes` — informes de pruebas y de lint
- `SocialKids-documentacion` — los PDF

Si se empuja una etiqueta `v*` (por ejemplo `git tag v1.0.0 && git push --tags`), además publica
una **release** de GitHub con el APK adjunto.

---

## Estructura

```
app/
  src/main/java/com/socialkids/app/
    data/      local (Room), seed (contenido), repository
    domain/    model, engine (6 motores), usecase (progreso, insignias, estadísticas)
    ui/        art (Canvas), components, screens, theme, navigation
    util/      reloj y retroalimentación (sonido + háptica)
  src/test/    93 pruebas unitarias JVM
  src/androidTest/  pruebas instrumentadas de Room
database/    schema.sql y sample_data.sql
docs/        memoria, manuales y informe de compilación (+ PDF)
deliverables/ APK, fuentes y documentos
```

---

## Estado verificado

| Comprobación | Resultado |
|---|---|
| `testDebugUnitTest` | **93 pruebas, 0 fallos, 0 errores** |
| `lintDebug` | **0 errores**, 49 avisos (versiones de dependencias) |
| `assembleDebug` | **BUILD SUCCESSFUL** — 18 MB |
| `assembleRelease` | **BUILD SUCCESSFUL** — 12 MB (sin firmar) |

Detalle completo y hashes SHA-256 en [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md).

---

## Documentación

- [Memoria descriptiva](docs/MEMORIA_DESCRIPTIVA.md) — qué se diseñó y por qué
- [Manual de usuario](docs/MANUAL_USUARIO.md) — para el niño y para la familia
- [Manual técnico](docs/MANUAL_TECNICO.md) — arquitectura y motores
- [Base de datos](docs/BASE_DE_DATOS.md) — tablas, consultas y decisiones
- [Informe de compilación](docs/BUILD_REPORT.md) — resultados reales

## Licencia

Proyecto educativo. Uso libre con fines didácticos.
