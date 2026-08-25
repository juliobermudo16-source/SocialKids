# Memoria descriptiva — SocialKids (Isla Conecta)

Versión 1.0.0 · Aplicación Android educativa para 8–12 años

---

## 1. Problema del que parte el proyecto

Las habilidades sociales se enseñan casi siempre de dos maneras: hablando de ellas o midiéndolas
con cuestionarios. Las dos fallan por lo mismo. Un niño de diez años puede responder perfectamente
"¿qué deberías hacer si alguien se ríe de tu compañero?" y no hacer nada cuando ocurre de verdad,
porque saber la respuesta y sostener la conducta en caliente son cosas distintas.

SocialKids parte de esa distancia. La aplicación no pregunta qué habría que hacer: pone al niño a
**hacerlo** en una situación simulada, con variables que se mueven, y le devuelve el resultado de
sus decisiones.

## 2. Objetivo

Construir una aplicación Android nativa, funcional y offline, que entrene tres competencias:

1. **Empatía** — distinguir lo que otra persona siente, piensa y necesita.
2. **Comunicación asertiva** — decir lo que a uno le pasa sin atacar ni desaparecer.
3. **Habilidades sociales** — escuchar, poner límites, negociar acuerdos, incluir, reparar.

Y hacerlo con una experiencia que un niño de diez años quiera abrir mañana otra vez.

## 3. Público objetivo

Niños y niñas de **8 a 12 años**. Esta franja lee textos breves con soltura, entiende sistemas de
progreso, compara resultados y disfruta de mapas y colecciones. Al mismo tiempo, detecta enseguida
cuándo un producto la trata como si tuviera cinco años.

Decisiones tomadas a partir de eso:

- Nada de estética de preescolar: la paleta es saturada pero contemporánea, y las formas son
  redondeadas sin ser blandas.
- Nada de lenguaje condescendiente. Los textos hablan de tú a tú: «Interrumpir hace que la otra
  persona empiece de nuevo por dentro».
- Sin emojis en la interfaz. La expresividad la llevan las ilustraciones vectoriales.
- Retos que se pueden fallar. Sacar cero estrellas es posible, y no pasa nada.

## 4. Concepto: la Isla Conecta

La isla perdió las palabras que unen a la gente. **Nima** —una criatura redonda con orejas largas y
una antena que se enciende con el color de la emoción que siente— guía al jugador sin resolverle
nada. Aparece para presentar, explicar un error y celebrar; nunca interrumpe a media actividad.

La narrativa cumple una función concreta: convierte «ejercicio 4» en «reparar el puente para que
alguien pueda cruzar». Es ligera a propósito. No hay cinemáticas ni diálogos largos.

### Las seis zonas

| Zona | Habilidad entrenada | Metáfora visual |
|---|---|---|
| Faro de las Emociones | Reconocer y nombrar emociones | Un faro apagado que hay que encender |
| Bosque que Escucha | Escucha activa | Árboles que repiten lo último que oyeron |
| Puente de la Empatía | Toma de perspectiva | Un puente de tres tablones caído |
| Plaza de las Palabras | Asertividad | Palabras que se vuelven objetos: unas rompen, otras construyen |
| Taller de Acuerdos | Resolución de conflictos | Máquinas que solo arrancan si hay acuerdo |
| Mirador de la Amistad | Amistad, inclusión, decir basta | Desde arriba se ve quién se quedó abajo |

Cada zona tiene **cuatro misiones** encadenadas: 24 en total.

## 5. Los primeros treinta segundos

Antes de escribir código se respondieron estas cinco preguntas:

- **¿Qué hace el niño en los primeros 30 segundos?** Ve el mar animarse, elige un avatar de ocho
  posibles y entra al mapa. En la misión 1 mueve un deslizador y ve una cara cambiar de expresión
  al instante.
- **¿Cuál es la acción principal de diversión?** Manipular: mover ejes de un rostro, arrastrar
  piezas, montar frases, decidir en una discusión que se le va de las manos.
- **¿Qué provoca curiosidad?** El mapa con zonas todavía cerradas y las cartas con silueta y
  signos de interrogación.
- **¿Qué hace que quiera volver mañana?** La racha, la siguiente carta y la insignia que está a un
  paso (la app le dice cuál es y cuánto le falta).
- **¿Qué progreso ve?** Barra de nivel, porcentaje por zona, estrellas por misión y la colección
  llenándose.

## 6. Ciclo principal de juego

```
Entrar a la isla
      ↓
El mapa señala la siguiente misión (calculada desde el progreso real)
      ↓
Interactuar: construir, arrastrar, medir, negociar
      ↓
Feedback con explicación educativa breve, nunca solo "correcto"
      ↓
Recompensa: XP, estrellas, carta nueva, insignia
      ↓
Se desbloquea la siguiente misión o una zona entera
      ↓
Volver a explorar
```

## 7. Diseño de las mecánicas

La especificación pedía que más de la mitad de la experiencia **no** fuese opción múltiple.
El reparto real de las 24 misiones:

| Mecánica | Misiones | Tipo de interacción |
|---|---:|---|
| Puente de la empatía | 5 | Arrastrar y soltar |
| Constructor de mensajes | 5 | Arrastrar y componer |
| Simulador de conflicto | 4 | Decisión con estado simulado |
| Detective de escucha | 4 | Selección múltiple sobre contenido + respuesta |
| Estudio de rostros | 3 | Manipulación continua (deslizadores) |
| Termómetro emocional | 3 | Medición continua + estrategia |

**16 de 24 misiones (67 %)** son de manipulación directa. Hay una prueba automatizada que verifica
esa proporción (`ContenidoTest`), para que no se degrade al añadir contenido.

### 7.1 Estudio de Rostros

El rostro no se elige de una galería: se construye. Cuatro ejes continuos de 0 a 100 —cejas, ojos,
boca y energía— se traducen en geometría concreta (la curvatura de la boca, la altura del párpado,
la inclinación de la ceja hacia dentro o hacia fuera, la caída de los hombros). El motor calcula la
distancia a un rostro objetivo con pesos: la boca aporta un 32 %, las cejas un 28 %, los ojos un
22 % y la energía corporal un 18 %.

Contenido pedagógico: el miedo y la sorpresa se parecen porque ambos abren los ojos; se distinguen
por las cejas. La alegría auténtica arruga los ojos; por eso se nota una sonrisa de compromiso.

### 7.2 Detective de Escucha

Se separa deliberadamente en tres fases: leer el relato entero, marcar los datos reales y solo
después elegir la respuesta. La puntuación combina un **F1** entre lo seleccionado y lo realmente
dicho (60 %) con la calidad del tipo de respuesta (40 %): reflejar, preguntar, validar, aconsejar
demasiado pronto, desviar hacia uno mismo o juzgar.

Marcar detalles que nadie dijo penaliza igual que olvidarlos. Escuchar también es no rellenar
huecos con suposiciones.

### 7.3 Puente de la Empatía

Tres tablones: *siente*, *piensa*, *necesita*. Seis piezas, de las cuales tres encajan y tres son
trampa: un juicio («es un exagerado»), una solución rápida («que se apunte a otro grupo») y una
suposición («lo dice para llamar la atención»). Distinguir empatía de juicio es exactamente el
aprendizaje.

El puente se dibuja más o menos completo según cuántos tablones encajen: la estabilidad es una
función del acierto, no una decoración.

### 7.4 Constructor de Mensajes

Un mensaje-yo tiene cuatro partes: *yo me siento… cuando… porque… me gustaría…*. Para cada hueco
hay tres fichas: una asertiva, una agresiva y una pasiva. La frase se ve crecer en vivo y el borde
cambia de color según el tono resultante.

Regla del clasificador: una sola ficha agresiva vuelve agresivo todo el mensaje (basta un «eres un
idiota» para que el resto deje de escucharse); dos o más fichas pasivas lo vuelven pasivo. El
resultado explica qué parte concreta falla.

### 7.5 Simulador de Conflicto

Es el motor con más peso. Tres variables de 0 a 100 —**calma**, **confianza**, **acuerdo**— se
mueven con cada elección a lo largo de tres turnos. El desenlace se calcula con umbrales:

- calma < 30 o confianza < 30 → **se rompió la charla**
- acuerdo ≥ 65 y calma ≥ 50 → **acuerdo real**
- en otro caso → **tregua fría**

No hay finales escritos a mano. Un jugador que ataca en el primer turno y luego intenta arreglarlo
puede terminar en tregua; uno que pide una pausa a tiempo suele llegar al acuerdo. Esa es la
lección: la calma no es un adorno, es lo que permite negociar.

### 7.6 Termómetro Emocional

Dos decisiones independientes: poner un número a la intensidad de una escena y elegir una
estrategia proporcional. Respirar 4-4-6 sirve para un 8; hablarlo sirve para un 3. La estrategia se
juzga contra la intensidad **real** de la situación, no contra la que el niño creyó ver, para que
un error de medición no arrastre al otro.

Mensaje de fondo: no todo lo que molesta es un 8, y responder con un cohete a un mosquito también
es un error.

## 8. Gamificación

Se usa solo lo que refuerza la conducta que interesa:

- **XP y niveles** (12 niveles, curva suave: 100 XP el primer salto, +50 cada siguiente).
- **Estrellas** por misión, con corte único para todas las mecánicas: 88 → 3, 70 → 2, 45 → 1.
- **25 cartas** con un dato real detrás («un perdón con la palabra *pero* detrás deja de ser un
  perdón»). Se desbloquean al completar la misión que las guarda.
- **12 insignias** evaluadas sobre estadísticas persistidas, no sobre tiempo de uso.
- **Racha** de días, que se muestra pero **nunca castiga**: si se rompe, la app dice
  «si se corta no pasa nada: se empieza otra».

Lo que se descartó a propósito: rankings online, comparación con otros niños, vidas que obligan a
esperar, compras, notificaciones de culpa y cualquier recompensa que no venga de una acción real.

Repetir una misión sigue dando XP, pero un 35 % de la primera vez, para que no se pueda granjear
puntos repitiendo la misión más fácil.

## 9. Diseño visual

**Identidad**: la chispa de cuatro puntas es el símbolo recurrente (aparece en el logotipo, en el
icono del launcher, en la antena de Nima y en el avatar «Chispa»). La paleta son doce tonos con
nombre; cada zona tiene su color principal y su color de apoyo.

**Todo está dibujado con Compose Canvas o vector drawables.** No hay ni una imagen descargada, ni
una URL, ni un recurso que dependa de la red. El inventario visual:

- Nima con seis estados de ánimo distintos.
- Un rostro paramétrico que se recalcula con cada cambio.
- 8 avatares con accesorio propio (gorra, gafas, auriculares, bufanda, flor, antena, capucha, diadema).
- 12 símbolos vectoriales para cartas, insignias e iconos de módulo.
- 6 escenas de zona completamente distintas: faro con haz de luz, bosque con ondas de sonido,
  puente con arco, plaza con bocadillos, taller con engranajes que giran, mirador con barandilla.
- Fondo de isla con mar, tres bandas de olas animadas, nubes, sol e islotes.
- Termómetro, medidores de conflicto, gráfico semanal y puente de estabilidad variable.
- Icono de launcher propio en cinco densidades + icono adaptativo con capa monocroma.

**Regla de densidad**: ninguna pantalla principal es solo título + párrafo + botones. El mapa es un
mapa con sendero punteado y nodos; la zona abre con su escena; cada actividad tiene su ilustración
funcional.

## 10. Accesibilidad

- Los estados de misión (bloqueada, disponible, empezada, completada, dominada) se comunican con
  **icono + texto**, nunca solo con color.
- Ajuste de **texto grande** (escala tipográfica ×1.18) y de **alto contraste**.
- Las **animaciones se pueden desactivar** por completo; el interruptor apaga las transiciones
  infinitas de Nima, del mar, de las escenas y de la respiración.
- Sonido y vibración son opcionales y están separados.
- El arrastre siempre tiene alternativa por toque: tocar la pieza y luego el hueco.
- Descripciones de contenido en avatar, guía y mapa.

## 11. Privacidad infantil

El manifiesto **no declara ningún permiso**. La aplicación no pide correo, teléfono, dirección,
ubicación, contactos, cámara ni micrófono, y no incluye internet, backend, login, analítica ni
publicidad.

Se guarda un apodo (2–16 caracteres, nunca el nombre real), un identificador de avatar y el
progreso. Todo vive en una base SQLite local. Ajustes incluye un botón que borra absolutamente
todo, con confirmación previa.

## 12. Sesiones de uso

Diseñado para sesiones de **5 a 20 minutos**: una misión dura entre 2 y 5 minutos, el progreso se
guarda al terminar cada una y el mapa siempre indica dónde se quedó uno. El Rincón de Calma
funciona como sesión corta independiente, sin desbloqueos.

## 13. Contenido incluido

| Elemento | Cantidad |
|---|---:|
| Zonas | 6 |
| Misiones | 24 |
| Rostros objetivo | 3 |
| Retos de termómetro (con 4 estrategias cada uno) | 3 |
| Relatos de escucha (7 detalles + 4 respuestas cada uno) | 4 |
| Retos de puente (6 piezas cada uno) | 5 |
| Retos de mensaje (12 fichas cada uno) | 5 |
| Escenas de conflicto (4 nodos × 3 opciones) | 4 |
| Cartas coleccionables | 25 |
| Insignias | 12 |
| Avatares | 8 |
| Emociones del diario | 8 |
| Pantallas principales | 18 |

## 14. Revisión final de experiencia

Evaluación desde el punto de vista de un niño de 10 años:

- *¿Me gustaría abrirla mañana?* Sí: hay una carta a la vista, una racha viva y una insignia a un
  paso, y la app dice exactamente cuánto falta.
- *¿Entiendo qué hacer?* Sí: el mapa destaca la siguiente misión con su nombre y su recompensa.
- *¿Tengo algo que descubrir?* Sí: tres zonas cerradas al empezar y 17 cartas en silueta.
- *¿Me siento recompensado?* Sí: XP, estrellas, carta, insignia y zona completada, cada una con su
  animación breve.
- *¿Hay algo que coleccionar?* 25 cartas con dato real y 12 insignias.
- *¿Parece hecha para mí?* Es colorida y con personaje, pero los textos tratan de tú a tú y los
  retos se pueden fallar.

## 15. Limitaciones conocidas

Se documentan de forma explícita, sin sustituirlas por marcador de posición:

- **El sonido usa `ToneGenerator`** (tonos cortos del sistema) en lugar de archivos de audio
  producidos. Es funcional, silenciable y no añade peso al APK, pero no es un diseño sonoro
  propio.
- **El arrastre se activa con pulsación mantenida**, para no competir con el desplazamiento
  vertical de la pantalla. Se compensa con la alternativa por toque, explicada en la propia
  actividad.
- **Las pruebas de Room son instrumentadas** (`app/src/androidTest`) y necesitan un dispositivo o
  emulador; no se ejecutan en el workflow de CI. La lógica del repositorio sí está cubierta por
  14 pruebas JVM con dobles de prueba que implementan los mismos DAO.
- **El APK de release se genera sin firmar.** Firmarlo requiere un almacén de claves que no debe
  vivir en el repositorio.
- **No hay traducciones**: la aplicación está solo en español.

## 16. Resultado

93 pruebas unitarias en verde, lint sin errores y APK compilado y verificado. El detalle está en
`docs/BUILD_REPORT.md`, con los hashes SHA-256 de los binarios entregados.
