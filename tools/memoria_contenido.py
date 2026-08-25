#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Contenido de la Memoria Descriptiva de SocialKids.

Usa los bloques de tools/memoria_docx.py y lee los fragmentos de código
directamente del repositorio, de modo que el documento no puede quedar
desincronizado con el código real.

Uso:
    python tools/memoria_contenido.py [salida.docx]
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from memoria_docx import RAIZ, Memoria, leer  # noqa: E402

# --------------------------------------------------------------- códigos

CODIGOS = [
    (
        "4.2.1", "Código fuente principal de MainActivity.kt",
        "Única Activity de la aplicación. Instala la pantalla de arranque, obtiene el "
        "ViewModel principal, aplica el tema según los ajustes de accesibilidad guardados "
        "y monta el árbol de navegación dentro de la capa que gestiona el arrastre de piezas.",
        "app/src/main/java/com/socialkids/app/MainActivity.kt", None, None, False,
    ),
    (
        "4.2.2", "Código fuente principal de SocialKidsApp.kt",
        "Clase Application y contenedor de dependencias. Construye una sola vez la base de "
        "datos Room y los dos repositorios, sin necesidad de un framework de inyección.",
        "app/src/main/java/com/socialkids/app/SocialKidsApp.kt", None, None, True,
    ),
    (
        "4.2.3", "Código fuente principal de SocialKidsDatabase.kt",
        "Declaración de la base de datos local. Registra las siete entidades, expone los seis "
        "DAO y aplica el patrón singleton con doble comprobación para tener una única instancia.",
        "app/src/main/java/com/socialkids/app/data/local/SocialKidsDatabase.kt", None, None, True,
    ),
    (
        "4.2.4", "Código fuente principal de Entidades.kt",
        "Tablas de la base de datos: perfil, progreso por misión, historial de intentos, cartas, "
        "insignias, diario de ánimo y visitas diarias. No existe ningún campo con datos personales.",
        "app/src/main/java/com/socialkids/app/data/local/entity/Entidades.kt", None, None, True,
    ),
    (
        "4.2.5", "Código fuente principal de JuegoRepository.kt (fragmento)",
        "Única puerta de entrada a los datos del juego. Se muestra el método registrarResultado, "
        "que es el núcleo del bucle de juego: guarda el progreso, anota el intento, suma "
        "experiencia, desbloquea la carta, otorga las insignias nuevas y comprueba si la zona "
        "ha quedado completa.",
        "app/src/main/java/com/socialkids/app/data/repository/JuegoRepository.kt", 147, 221, False,
    ),
    (
        "4.2.6", "Código fuente principal de MensajeEngine.kt (fragmento)",
        "Motor del Constructor de Mensajes. Monta la frase con las fichas colocadas, clasifica el "
        "estilo resultante como pasivo, agresivo o asertivo y calcula la puntuación combinando "
        "estructura y tono. Es lógica pura: no conoce Android ni Compose.",
        "app/src/main/java/com/socialkids/app/domain/engine/MensajeEngine.kt", 8, 100, False,
    ),
    (
        "4.2.7", "Código fuente principal de ConflictoEngine.kt (fragmento)",
        "Motor del Simulador de Conflicto. Máquina de estados con tres variables vivas -calma, "
        "confianza y acuerdo- que se mueven con cada elección. El desenlace no está escrito: se "
        "calcula con umbrales sobre el estado final de la conversación.",
        "app/src/main/java/com/socialkids/app/domain/engine/ConflictoEngine.kt", 6, 82, False,
    ),
    (
        "4.2.8", "Código fuente principal de Progreso.kt (fragmento)",
        "Reglas de experiencia, niveles y desbloqueos. Define la curva de progreso, la experiencia "
        "que otorga cada misión según las estrellas obtenidas y el estado en que se encuentra cada "
        "misión dentro de su zona.",
        "app/src/main/java/com/socialkids/app/domain/usecase/Progreso.kt", 7, 75, False,
    ),
    (
        "4.2.9", "Código fuente principal de ActividadViewModel.kt (fragmento)",
        "ViewModel de la misión en curso. Guarda el estado de la actividad y delega el cálculo del "
        "resultado en el motor que corresponde a la mecánica, marcando además el hito propio de "
        "cada una de ellas.",
        "app/src/main/java/com/socialkids/app/ui/ActividadViewModel.kt", 159, 224, False,
    ),
    (
        "4.2.10", "Código fuente principal de Navegacion.kt (fragmento)",
        "Catálogo de rutas y grafo de navegación. Catorce destinos con transiciones propias; las "
        "rutas con parámetro validan el argumento recibido y recurren a un valor por defecto si "
        "no es válido.",
        "app/src/main/java/com/socialkids/app/ui/navigation/Navegacion.kt", 34, 110, False,
    ),
    (
        "4.2.11", "Código fuente de configuración app/build.gradle.kts",
        "Configuración de compilación del módulo: identificador de aplicación, niveles de SDK, "
        "activación de Compose, exportación del esquema de Room y todas las dependencias con "
        "versión fija, sin versiones dinámicas.",
        "app/build.gradle.kts", None, None, False,
    ),
]

# --------------------------------------------------------------- módulos

MODULOS = [
    ("MainActivity.kt", "Única Activity. Instala la pantalla de arranque, aplica el tema y monta la navegación."),
    ("SocialKidsApp.kt", "Application y contenedor de dependencias: base de datos y repositorios."),
    ("data/local/SocialKidsDatabase.kt", "Base de datos Room: siete entidades, versión 1, esquema exportado."),
    ("data/local/entity/Entidades.kt", "Tablas perfil, progreso_mision, intento, carta, insignia, animo y visita."),
    ("data/local/dao/Daos.kt", "Seis DAO declarados como interfaces, lo que permite sustituirlos en las pruebas."),
    ("data/repository/JuegoRepository.kt", "Única puerta a los datos: estado combinado, registro de resultados, ánimo y reinicio."),
    ("data/repository/AjustesRepository.kt", "Preferencias de sonido, vibración, animaciones, texto grande y contraste con DataStore."),
    ("data/seed/MundoSeed.kt", "Catálogo del mundo: seis zonas y veinticuatro misiones con mecánica, dificultad y recompensa."),
    ("data/seed/CartasSeed.kt", "Veinticinco cartas coleccionables y los ocho avatares con su accesorio."),
    ("data/seed/RetosSeed.kt", "Contenido de las mecánicas de rostro, termómetro y escucha."),
    ("data/seed/RetosPuenteSeed.kt", "Cinco retos del Puente de la Empatía, con piezas correctas y piezas trampa."),
    ("data/seed/RetosMensajeSeed.kt", "Cinco retos del Constructor de Mensajes, con tres fichas por ranura."),
    ("data/seed/RetosConflictoSeed.kt", "Cuatro escenas del Simulador de Conflicto, con sus nodos y opciones."),
    ("domain/model/Mundo.kt", "Modelos del dominio: zona, misión, carta, insignia, avatar y resultado de actividad."),
    ("domain/model/EstadoJuego.kt", "Fotografía completa del juego que consume la interfaz y estructura de recompensa."),
    ("domain/engine/RostroEngine.kt", "Motor del Estudio de Rostros: distancia ponderada por eje y evaluación de rasgos extra."),
    ("domain/engine/EscuchaEngine.kt", "Motor del Detective de Escucha: F1 entre lo marcado y lo dicho, más la calidad de la respuesta."),
    ("domain/engine/PuenteEngine.kt", "Motor del Puente de la Empatía: aciertos por tablón, penalización de trampas y estabilidad."),
    ("domain/engine/MensajeEngine.kt", "Motor del Constructor de Mensajes: montaje de la frase y clasificación del estilo."),
    ("domain/engine/ConflictoEngine.kt", "Motor del Simulador de Conflicto: máquina de estados y desenlace por umbrales."),
    ("domain/engine/TermometroEngine.kt", "Motor del Termómetro Emocional: precisión de la medida y proporcionalidad de la estrategia."),
    ("domain/usecase/Progreso.kt", "Curva de niveles, experiencia por misión, desbloqueos, porcentajes y modo repaso."),
    ("domain/usecase/Insignias.kt", "Catálogo de doce insignias y su evaluación sobre las estadísticas persistidas."),
    ("domain/usecase/Estadisticas.kt", "Resumen del diario de ánimo y cálculo de la racha actual y la mejor racha."),
    ("ui/JuegoViewModel.kt", "Estado global: perfil, progreso, ajustes, diario, racha, siguiente misión y repaso."),
    ("ui/ActividadViewModel.kt", "Estado de la misión en curso y evaluación mediante el motor correspondiente."),
    ("ui/navigation/Navegacion.kt", "Catorce rutas y grafo de navegación con transiciones propias."),
    ("ui/theme/Paleta.kt", "Paleta propia de doce tonos y colores de cada zona de la isla."),
    ("ui/theme/Tema.kt", "Esquemás claro y oscuro, tipografía escalable y modo de alto contraste."),
    ("ui/art/Nima.kt", "Ilustración de la criatura guía, con seis estados de animo y animación de flotacion."),
    ("ui/art/Rostro.kt", "Rostro paramétrico: los cuatro ejes del motor se traducen en geometria dibujada."),
    ("ui/art/Avatares.kt", "Ocho avatares dibujados con Canvas, cada uno con un accesorio distinto."),
    ("ui/art/Figuras.kt", "Doce símbolos vectoriales usados en cartas, insignias e iconos de módulo."),
    ("ui/art/Escenarios.kt", "Fondo de isla animado y seis escenas de zona claramente diferenciadas."),
    ("ui/components/Arrastre.kt", "Sistema propio de arrastrar y soltar, con zonas de destino y pieza flotante."),
    ("ui/components/Componentes.kt", "Botones, barras de progreso, medidores, estrellas, cabeceras y estados vacios."),
    ("ui/components/Tarjetas.kt", "Tarjetas de carta, insignia, misión y zona, y panel de explicacion educativa."),
    ("ui/screens/PantallaPortada.kt", "Portada con mar animado, logotipo propio y bienvenida de la guía."),
    ("ui/screens/PantallaOnboarding.kt", "Cuatro pantallas de presentación que solo aparecen la primera vez."),
    ("ui/screens/PantallaCrearPerfil.kt", "Elección de apodo y avatar. Nunca se solicita el nombre real."),
    ("ui/screens/PantallaMapa.kt", "Centro de la experiencia: mapa, siguiente misión y accesos de la mochila."),
    ("ui/screens/PantallaZona.kt", "Escena de la zona, misiones encadenadas y cartas obtenibles allí."),
    ("ui/screens/PantallaMision.kt", "Contenedor de misión: elige la mecánica y muestra la pantalla de recompensa."),
    ("ui/screens/ActividadRostros.kt", "Estudio de Rostros: cuatro ejes continuos y rasgos extra."),
    ("ui/screens/ActividadEscucha.kt", "Detective de Escucha: relato, selección de datos reales y respuesta."),
    ("ui/screens/ActividadPuente.kt", "Puente de la Empatía: arrastre de piezas a los tablones siente, piensa y necesita."),
    ("ui/screens/ActividadMensaje.kt", "Constructor de Mensajes: fichas, vista previa viva y tono resultante."),
    ("ui/screens/ActividadConflicto.kt", "Simulador de Conflicto: medidores, bitácora y opciones de diálogo."),
    ("ui/screens/ActividadTermometro.kt", "Termómetro Emocional: medición de intensidad y elección de estrategia."),
    ("ui/screens/PantallaColeccion.kt", "Coleccion de cartas con filtros por categoría y ficha de detalle."),
    ("ui/screens/PantallaInsignias.kt", "Insignias conseguidas y pendientes, con su progreso real."),
    ("ui/screens/PantallaDiario.kt", "Diario de ánimo: ocho emociones con rostro, intensidad y nota opcional."),
    ("ui/screens/PantallaEstadisticas.kt", "Números calculados desde la base de datos y gráfico semanal."),
    ("ui/screens/PantallaCalma.kt", "Rincon de calma con respiracion 4-4-6 guíada y animada."),
    ("ui/screens/PantallaRepaso.kt", "Misiónes completadas con menos de tres estrellas, para practicar otra vez."),
    ("ui/screens/PantallaPerfil.kt", "Identidad, nivel, logros acumulados y accesos a lo conseguido."),
    ("ui/screens/PantallaAjustes.kt", "Sonido, vibración, accesibilidad, información de privacidad y borrado de datos."),
    ("util/Reloj.kt", "Abstracción del tiempo, sustituible en pruebas, y formato corto de fechas."),
    ("util/Retroalimentacion.kt", "Sonido mediante tonos del sistema y vibración, ambos opcionales."),
    ("src/test/.../MotoresTest.kt", "Cuarenta pruebas de los seis motores de dominio y de la conversión a estrellas."),
    ("src/test/.../ProgresoTest.kt", "Treinta y nueve pruebas de niveles, desbloqueos, insignias, estadísticas y contenido."),
    ("src/test/.../RepositorioTest.kt", "Catorce pruebas del bucle completo con dobles de prueba de los DAO."),
    ("src/androidTest/.../BaseDeDatosTest.kt", "Siete pruebas instrumentadas de persistencia real con Room."),
    ("res/drawable/, res/mipmap-*/", "Icono adaptativo con capa monocroma e iconos de lanzador en cinco densidades."),
    ("res/values/, res/values-night/", "Textos, colores y temás para modo claro y modo oscuro."),
    ("res/xml/", "Reglas de copia de seguridad y de extraccion de datos, limitadas a la base local."),
    ("database/schema.sql", "Esquema real transcrito del exportado por Room, con sus indices."),
    ("database/sample_data.sql", "Juego de datos de ejemplo verificado sobre SQLite."),
    (".github/workflows/android.yml", "Integracion continua: pruebas, lint, APK, empaquetado y publicacion de artefactos."),
    ("tools/", "Utilidades del proyecto: generación de documentos, empaquetado y resumen de compilación."),
]

# ----------------------------------------------------------------- arbol

ARBOL = """SocialKids/
|-- .github/
|   `-- workflows/
|       `-- android.yml
|-- app/
|   |-- schemas/
|   |   `-- com.socialkids.app.data.local.SocialKidsDatabase/1.json
|   |-- src/
|   |   |-- androidTest/java/com/socialkids/app/
|   |   |   `-- BaseDeDatosTest.kt
|   |   |-- main/
|   |   |   |-- AndroidManifest.xml
|   |   |   |-- java/com/socialkids/app/
|   |   |   |   |-- MainActivity.kt
|   |   |   |   |-- SocialKidsApp.kt
|   |   |   |   |-- data/
|   |   |   |   |   |-- local/
|   |   |   |   |   |   |-- SocialKidsDatabase.kt
|   |   |   |   |   |   |-- dao/Daos.kt
|   |   |   |   |   |   `-- entity/Entidades.kt
|   |   |   |   |   |-- repository/
|   |   |   |   |   |   |-- AjustesRepository.kt
|   |   |   |   |   |   `-- JuegoRepository.kt
|   |   |   |   |   `-- seed/
|   |   |   |   |       |-- CartasSeed.kt
|   |   |   |   |       |-- MundoSeed.kt
|   |   |   |   |       |-- RetosConflictoSeed.kt
|   |   |   |   |       |-- RetosMensajeSeed.kt
|   |   |   |   |       |-- RetosPuenteSeed.kt
|   |   |   |   |       `-- RetosSeed.kt
|   |   |   |   |-- domain/
|   |   |   |   |   |-- engine/
|   |   |   |   |   |   |-- ConflictoEngine.kt
|   |   |   |   |   |   |-- EscuchaEngine.kt
|   |   |   |   |   |   |-- MensajeEngine.kt
|   |   |   |   |   |   |-- PuenteEngine.kt
|   |   |   |   |   |   |-- RostroEngine.kt
|   |   |   |   |   |   `-- TermómetroEngine.kt
|   |   |   |   |   |-- model/
|   |   |   |   |   |   |-- EstadoJuego.kt
|   |   |   |   |   |   `-- Mundo.kt
|   |   |   |   |   `-- usecase/
|   |   |   |   |       |-- Estadisticas.kt
|   |   |   |   |       |-- Insignias.kt
|   |   |   |   |       `-- Progreso.kt
|   |   |   |   |-- ui/
|   |   |   |   |   |-- ActividadViewModel.kt
|   |   |   |   |   |-- JuegoViewModel.kt
|   |   |   |   |   |-- art/         (Nima, Rostro, Avatares, Figuras, Escenarios)
|   |   |   |   |   |-- components/  (Arrastre, Componentes, Tarjetas)
|   |   |   |   |   |-- navigation/  (Navegacion.kt)
|   |   |   |   |   |-- screens/     (20 pantallas y actividades)
|   |   |   |   |   `-- theme/       (Paleta.kt, Tema.kt)
|   |   |   |   `-- util/
|   |   |   |       |-- Reloj.kt
|   |   |   |       `-- Retroalimentacion.kt
|   |   |   `-- res/
|   |   |       |-- drawable/            (icono adaptativo y splash)
|   |   |       |-- mipmap-anydpi-v26/   (adaptive icon + monochrome)
|   |   |       |-- mipmap-mdpi ... xxxhdpi/
|   |   |       |-- values/ values-night/
|   |   |       `-- xml/                 (reglas de copia de seguridad)
|   |   `-- test/java/com/socialkids/app/
|   |       |-- MotoresTest.kt
|   |       |-- ProgresoTest.kt
|   |       `-- RepositorioTest.kt
|   |-- build.gradle.kts
|   `-- proguard-rules.pro
|-- database/
|   |-- schema.sql
|   `-- sample_data.sql
|-- deliverables/
|-- docs/
|   |-- BASE_DE_DATOS.md
|   |-- BUILD_REPORT.md
|   |-- MANUAL_TECNICO.md
|   |-- MANUAL_USUARIO.md
|   |-- MEMORIA_DESCRIPTIVA.md
|   `-- pdf/
|-- gradle/
|   |-- libs.versions.toml
|   `-- wrapper/
|-- tools/
|-- build.gradle.kts
|-- gradle.properties
|-- gradlew
|-- gradlew.bat
|-- settings.gradle.kts
`-- README.md"""


# ------------------------------------------------------------------ main

def main():
    salida = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
        RAIZ, "deliverables", "Memoria_Descriptiva_SocialKids.docx")
    m = Memoria()

    m.portada(
        "Memoria Descriptiva",
        "Aplicación móvil educativa para el desarrollo de la empatía, la comunicacion "
        "asertiva y las habilidades sociales en niñas y niños de 8 a 12 años",
        "SocialKids - Isla Conecta   |   Versión 1.0.0",
    )

    m.h1("1.", "Nombre de la aplicación.")
    m.parrafo(
        "Aplicación móvil educativa para el desarrollo de la empatía, la comunicacion "
        "asertiva y las habilidades sociales en niñas y niños de 8 a 12 años."
    )

    m.h1("2.", "Nombre corto.")
    m.parrafo("SOCIALKIDS")

    m.h1("3.", "Versión.")
    m.parrafo("1.0.0 (versionCode 1)")

    m.h1("4.", "Sobre la aplicación.")
    m.parrafo(
        "SocialKids es una aplicación Android nativa desarrollada con Kotlin y Jetpack Compose "
        "que entrena tres competencias socioemocionales concretas: la empatía, la comunicacion "
        "asertiva y las habilidades sociales. El contenido se organiza como un mundo de juego "
        "llamado Isla Conecta, compuesto por seis zonas y veinticuatro misiones que el jugador "
        "va abriendo conforme acumula experiencia."
    )
    m.parrafo(
        "La aplicación no plantea cuestionarios: cada misión se resuelve manipulando elementos en "
        "pantalla. El jugador construye rostros moviendo ejes continuos, arrastra piezas para "
        "levantar un puente, monta frases con fichas, mide la intensidad de una emocion y negocia "
        "una discusion cuyo desenlace depende de tres variables que cambian con cada elección. "
        "Dieciséis de las veinticuatro misiones son de manipulación directa."
    )
    m.parrafo(
        "La persistencia se resuelve íntegramente en el dispositivo mediante Room sobre SQLite y "
        "DataStore para las preferencias. La aplicación no declara ningún permiso en el "
        "manifiesto, no utiliza internet, cuentas de usuario, servicios en la nube, publicidad ni "
        "analitica, y todas las ilustraciones se dibujan en tiempo de ejecucion con Compose Canvas "
        "y recursos vectoriales, sin depender de ningún archivo externo."
    )
    m.parrafo("La aplicación tiene las siguientes funciones:")

    m.letra("a", "Identidad y primer acceso")
    for t in [
        "Pantalla de portada con el mundo animado y la criatura guía, llamada Nima.",
        "Presentación inicial de cuatro pantallas, que solo aparece la primera vez y puede omitirse.",
        "Creación de un perfil local con un apodo de dos a dieciséis caracteres y uno de ocho avatares.",
        "En ningún momento se solicita el nombre real ni ningún otro dato identificativo.",
    ]:
        m.punto(t)

    m.letra("b", "Exploración y misiones")
    for t in [
        "Mapa de la isla con seis zonas unidas por un sendero, cada una con su escenario propio.",
        "Apertura progresiva de zonas según la experiencia acumulada, y de misiones en cadena dentro de cada zona.",
        "Indicación permanente de la siguiente misión recomendada, calculada a partir del progreso real.",
        "Seis mecánicas interactivas: estudio de rostros, detective de escucha, puente de la empatía, "
        "constructor de mensajes, simulador de conflicto y termómetro emocional.",
        "Resultado con estrellas, puntuación, explicacion educativa breve y consejo concreto de mejora.",
        "Modo de repaso con las misiones completadas que aún no alcanzan las tres estrellas.",
    ]:
        m.punto(t)

    m.letra("c", "Progreso, coleccion y logros")
    for t in [
        "Experiencia y doce niveles, con una curva que hace más costoso cada salto.",
        "Veinticinco cartas coleccionables, cada una con un dato educativo, que se desbloquean al completar misiones.",
        "Doce insignias evaluadas sobre estadísticas persistidas, con indicación de lo que falta para cada una.",
        "Racha de días de uso, que se muestra como información y nunca como castigo.",
        "Pantalla de estadísticas calculadas íntegramente desde la base de datos.",
    ]:
        m.punto(t)

    m.letra("d", "Autorregulacion emocional")
    for t in [
        "Diario de ánimo con ocho emociones representadas por rostros, intensidad de uno a diez y nota opcional.",
        "Gráfico de los últimos siete días, intensidad media y reparto por emocion.",
        "Rincon de calma con un ejercicio de respiracion 4-4-6 guíado y animado, disponible sin desbloqueos.",
        "Sugerencia automática de acudir al rincon de calma cuando se registra una intensidad alta.",
    ]:
        m.punto(t)

    m.letra("e", "Ajustes, accesibilidad y privacidad")
    for t in [
        "Interruptores independientes para el sonido y para la vibración.",
        "Desactivación completa de las animaciones, aumento del tamaño de texto y modo de alto contraste.",
        "Estados de misión comunicados con icono y texto, nunca únicamente mediante color.",
        "Alternativa por toque para toda accion de arrastrar y soltar.",
        "Información explicita sobre que datos se guardan y borrado total del progreso con confirmacion previa.",
    ]:
        m.punto(t)

    # ------------------------------------------------------------------ 4.1
    m.salto()
    m.h2("4.1", "Estructura del proyecto")
    m.parrafo(
        "El proyecto sigue la estructura estándar de un módulo de aplicación Android con Gradle "
        "Kotlin DSL. El código fuente se organiza en tres capas -data, domain y ui- con una regla "
        "de dependencia estricta: la interfaz y los datos dependen del dominio, y el dominio no "
        "depende de ningúno de los dos ni de Android, lo que permite probarlo en la máquina virtual "
        "de Java sin emulador."
    )
    m.codigo(ARBOL, 7.0)

    # ------------------------------------------------------------------ 4.2
    m.salto()
    m.h2("4.2", "Códigos principales de la aplicación")
    m.parrafo(
        "Se exponen a continuacion los archivos que sostienen el funcionamiento de la aplicación. "
        "En los archivos extensos se indica expresamente que se trata de un fragmento y se ha "
        "conservado la parte significativa."
    )
    for numero, titulo, descripcion, ruta, desde, hasta, sin_imports in CODIGOS:
        m.h3(numero, titulo)
        m.parrafo(descripcion)
        m.codigo(leer(ruta, desde, hasta, sin_imports))

    # ------------------------------------------------------------------ 4.3
    m.salto()
    m.h2("4.3", "Relación general de módulos y archivos del código fuente")
    m.parrafo(
        "Para que la memoria abarque el conjunto del código y no solamente los fragmentos "
        "expuestos en el numeral 4.2, se presenta una relación funcional de los principales "
        "módulos y archivos del repositorio SocialKids."
    )
    m.tabla(["Módulo / archivo", "Finalidad dentro del proyecto"], MODULOS, [6.2, 9.8])

    # ------------------------------------------------------------------ 4.4
    m.h2("4.4", "Flujo general de funcionamiento")
    m.parrafo(
        "La aplicación arranca en MainActivity, que instala la pantalla de arranque del sistema, "
        "obtiene JuegoViewModel a traves del contenedor definido en SocialKidsApp y aplica el tema "
        "con los ajustes de accesibilidad guardados. Todo el árbol de navegación se monta dentro de "
        "CapaArrastre, que es la que permite arrastrar piezas entre las distintas zonas de la "
        "pantalla."
    )
    m.parrafo(
        "El grafo de navegación comienza en la portada. Si no existe perfil, conduce a la "
        "presentación inicial y a la creación del perfil local; si ya existe, entra directamente al "
        "mapa. El mapa es el centro de la experiencia: muestra el nivel, la barra de experiencia, la "
        "racha, las seis zonas con su porcentaje de avance y la siguiente misión recomendada, que se "
        "obtiene recorriendo las zonas en orden y devolviendo la primera misión disponible."
    )
    m.parrafo(
        "Al abrir una misión, PantallaMision crea un ActividadViewModel identificado por el "
        "identificador de esa misión y seleccióna la pantalla correspondiente a su mecánica. El "
        "jugador manipula la actividad y, al pulsar Terminar, el ViewModel delega el cálculo en el "
        "motor de dominio adecuado, que devuelve una puntuación de cero a cien, las estrellas "
        "obtenidas, una explicacion educativa y un consejo concreto."
    )
    m.parrafo(
        "Ese resultado llega a JuegoRepository, que ejecuta el núcleo del bucle de juego: toma una "
        "fotografía de las estadísticas, actualiza el progreso conservando siempre el mejor "
        "resultado histórico, inserta el intento en el historial con su marca de hito, suma la "
        "experiencia, registra la visita del dia, desbloquea la carta si la misión se completa por "
        "primera vez, vuelve a fotografíar las estadísticas para detectar las insignias recien "
        "obtenidas y comprueba si la zona ha quedado completa. Todo ello se devuelve como una "
        "recompensa que la pantalla presenta al jugador."
    )
    m.parrafo(
        "La persistencia se realiza mediante Room. El repositorio expone un único flujo con el "
        "estado completo del juego, construido combinando el perfil, el progreso, las cartas, las "
        "insignias y un agregado de anotaciones de ánimo, visitas e hitos por mecánica; la interfaz "
        "nunca consulta un DAO directamente. Las preferencias de accesibilidad viajan por un flujo "
        "aparte y se inyectan en el árbol de composición, de modo que activar o desactivar las "
        "animaciones o el alto contraste tiene efecto inmediato en toda la aplicación."
    )

    # -------------------------------------------------------------------- 5
    m.h1("5.", "Observación final.")
    m.parrafo(
        "La presente memoria descriptiva se ha elaborado siguiendo la estructura de la plantilla "
        "proporcionada: identificación de la aplicación, descripción funcional, estructura del "
        "proyecto, exposición de códigos principales y relación general de módulos. Los fragmentos "
        "de código reproducidos no se han transcrito a mano: se leen del propio repositorio en el "
        "momento de generar el documento, de manera que no pueden quedar desincronizados con el "
        "código real."
    )
    m.parrafo(
        "El proyecto se compilo y verifico antes de redactar esta memoria. Las tareas "
        "testDebugUnitTest, lintDebug, assembleDebug y assembleRelease finalizaron correctamente, "
        "con noventa y tres pruebas unitarias superadas, ningún error de análisis estático y dos "
        "archivos APK generados. La misma secuencia se ejecuta de forma automática en la "
        "integración continua del repositorio publico. No se han ejecutado, y así se hace constar, "
        "las siete pruebas instrumentadas de base de datos, que requieren un dispositivo o emulador "
        "Android."
    )

    ruta = m.guardar(salida)
    print("Documento generado:", ruta)
    print("Tamaño:", os.path.getsize(ruta), "bytes")
    return ruta


if __name__ == "__main__":
    main()
