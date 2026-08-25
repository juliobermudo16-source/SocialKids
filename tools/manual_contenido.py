#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Contenido del Manual para Usuarios de SocialKids.

Sigue el modelo de manual proporcionado: portada, indice, introduccion y
secciones con pasos numerados junto a cada captura. Todas las capturas quedan
como marcos vacios para que se peguen a mano.

Uso:
    python tools/manual_contenido.py [salida.docx]
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from docx.shared import Pt  # noqa: E402

from manual_docx import RAIZ, TURQUESA, Manual  # noqa: E402

INDICE = [
    ("1", "INTRODUCCIÓN"),
    ("2", "INTERFACES DEL SISTEMA"),
    ("2.1", "ENTRAR A LA APLICACIÓN"),
    ("2.2", "CREAR TU EXPLORADOR"),
    ("2.3", "EL MAPA DE LA ISLA"),
    ("2.4", "LA PANTALLA DE UNA ZONA"),
    ("2.5", "LAS MISIONES"),
    ("2.5.1", "ESTUDIO DE ROSTROS"),
    ("2.5.2", "DETECTIVE DE ESCUCHA"),
    ("2.5.3", "PUENTE DE LA EMPATÍA"),
    ("2.5.4", "CONSTRUCTOR DE MENSAJES"),
    ("2.5.5", "SIMULADOR DE CONFLICTO"),
    ("2.5.6", "TERMÓMETRO EMOCIONAL"),
    ("2.6", "TU RESULTADO Y TU RECOMPENSA"),
    ("2.7", "CARTAS DE LA ISLA"),
    ("2.8", "INSIGNIAS"),
    ("2.9", "DIARIO DE ÁNIMO"),
    ("2.10", "TUS NÚMEROS"),
    ("2.11", "RINCÓN DE CALMA"),
    ("2.12", "PRACTICAR OTRA VEZ"),
    ("2.13", "TU EXPLORADOR"),
    ("2.14", "AJUSTES"),
    ("3", "PREGUNTAS FRECUENTES"),
]


def main():
    salida = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
        RAIZ, "deliverables", "Manual_Usuario_SocialKids.docx")
    m = Manual()

    # ------------------------------------------------------------- portada
    m.portada()

    # -------------------------------------------------------------- indice
    m.indice(INDICE, nota_edicion=(
        "NOTA PARA QUIEN EDITA ESTE DOCUMENTO (borrar antes de entregar)",
        "Cada captura tiene su marco de linea discontinua con el rotulo IMAGEN N. "
        "Para poner la tuya: haz clic dentro del marco, selecciona el texto gris "
        "[ PEGA AQUI TU CAPTURA DE PANTALLA ] y pega la imagen encima (Ctrl+V), o usa "
        "Insertar > Imagenes. El marco se adapta al alto de la imagen. Los pies de imagen "
        "en rojo estan numerados del 1 al 25 y puedes cambiar su texto libremente."
    ))

    # -------------------------------------------------------- introduccion
    m.titulo_seccion("1. INTRODUCCIÓN")
    m.texto(
        "SocialKids es una aplicación para aprender a entenderte a ti y a las personas que "
        "tienes al lado. Dentro hay una isla, la Isla Conecta, que se quedó muda porque la "
        "gente dejó de entenderse. Tu trabajo es devolverle las palabras que unen."
    )
    m.texto(
        "La isla tiene seis zonas y veinticuatro misiones. En cada misión no hay que elegir "
        "la respuesta correcta de una lista: hay que hacer cosas. Construyes caras, arrastras "
        "piezas, montas frases, mides emociones y decides qué dices en una discusión que se "
        "está poniendo fea."
    )
    m.texto(
        "Según vas avanzando ganas experiencia, subes de nivel, consigues cartas para tu "
        "colección e insignias por tus logros. También tienes un diario para anotar cómo estás "
        "y un rincón para respirar cuando lo necesites."
    )
    m.aviso(
        "ESTE MANUAL ESTÁ HECHO PARA TI, QUE VAS A EXPLORAR LA ISLA",
        "Sigue los pasos numerados. Cada imagen te enseña la pantalla de la que se habla. "
        "No necesitas internet en ningún momento y nadie te va a pedir tu nombre real."
    )
    m.salto()

    # ------------------------------------------------------- 2. INTERFACES
    m.titulo_seccion("2. INTERFACES DEL SISTEMA", "2.1  ENTRAR A LA APLICACIÓN")
    m.bloque(
        "Icono de SocialKids en el móvil",
        [
            "Busca el icono de SocialKids en tu móvil. Es una chispa amarilla sobre fondo "
            "turquesa. Tócalo para abrir la aplicación.",
        ],
        alto=6.5,
    )
    m.bloque(
        "Portada de la isla",
        [
            "Verás el mar moverse, el logotipo de SocialKids y a Nima, la criatura que te va a "
            "acompañar por toda la isla.",
            "Pulsa el botón EMPEZAR LA AVENTURA. Si ya habías jugado antes, el botón dirá "
            "CONTINUAR y te saludará por tu apodo.",
        ],
    )
    m.salto()

    m.titulo_seccion("2. INTERFACES DEL SISTEMA", "2.1  ENTRAR A LA APLICACIÓN")
    m.bloque(
        "Pantallas de presentación",
        [
            "La primera vez aparecen cuatro pantallas que te cuentan de qué va la isla: qué "
            "pasó, qué se entrena en cada zona, cómo se juega y qué datos se guardan.",
            "Desliza con el dedo hacia la izquierda o pulsa SIGUIENTE para avanzar.",
            "Si tienes prisa, pulsa SALTAR arriba a la derecha. Estas pantallas solo aparecen "
            "la primera vez.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.2 PERFIL
    m.titulo_seccion("2.2  CREAR TU EXPLORADOR", color="7C5CFF")
    m.bloque(
        "Pantalla de creación del perfil",
        [
            "Escribe tu apodo. Tiene que tener entre 2 y 16 letras. No pongas tu nombre real: "
            "no hace falta y así nadie sabe quién eres fuera de la isla.",
            "Elige uno de los ocho avatares tocándolo. El que elijas se pondrá más grande y con "
            "un borde de color.",
            "Cuando lo tengas, pulsa ENTRAR A LA ISLA. Recibirás tu primera carta de regalo: la "
            "carta de Nima.",
        ],
    )
    m.aviso(
        "PUEDES CAMBIARLO CUANDO QUIERAS",
        "Tu apodo y tu avatar no son para siempre. En la pantalla Tu explorador puedes "
        "cambiarlos las veces que quieras sin perder nada de tu progreso."
    )
    m.salto()

    # ------------------------------------------------------- 2.3 MAPA
    m.titulo_seccion("2.3  EL MAPA DE LA ISLA", "Es tu pantalla principal")
    m.bloque(
        "Mapa de la Isla Conecta",
        [
            "Arriba del todo está tu avatar, tu apodo, tu nivel, la barra de experiencia y los "
            "días seguidos que llevas entrando. Toca tu avatar para ver tu perfil completo.",
            "En el centro está la isla con las seis zonas unidas por un camino de puntos. La "
            "zona que late suavemente es la que te toca ahora.",
            "Las zonas con un candado todavía no están abiertas. Debajo de cada una pone cuánta "
            "experiencia necesitas para abrirla.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.3  EL MAPA DE LA ISLA", "Tu siguiente paso y tu mochila")
    m.bloque(
        "Tarjeta de la siguiente misión",
        [
            "Debajo del mapa aparece siempre tu siguiente misión, con su nombre, lo que hay que "
            "hacer y la experiencia que te va a dar.",
            "Pulsa EMPEZAR MISIÓN para ir directamente a ella sin buscarla en el mapa.",
        ],
        alto=7.0,
    )
    m.bloque(
        "La mochila: accesos rápidos",
        [
            "Más abajo está tu mochila, con seis accesos: Cartas, Insignias, Diario, Calma, "
            "Repaso y Ajustes. Cada uno te dice cuánto llevas conseguido.",
        ],
        alto=6.0,
    )
    m.salto()

    # ------------------------------------------------------- 2.4 ZONA
    m.titulo_seccion("2.4  LA PANTALLA DE UNA ZONA", color="3FD08D")
    m.bloque(
        "Pantalla de una zona",
        [
            "Arriba verás el escenario de la zona y una frase que te cuenta qué pasó allí.",
            "Debajo está la barra con lo que llevas hecho de esa zona y las cuatro misiones en "
            "orden. Cada una muestra su estado: Bloqueada, Disponible, Empezada, Completada o "
            "Dominada, y las estrellas que conseguiste.",
            "Las misiones se abren una detrás de otra: hasta que no completas la primera no se "
            "abre la segunda.",
            "Al final de la pantalla están las cartas que puedes conseguir en esa zona. Las que "
            "aún no tienes salen apagadas.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.5 MISIONES
    m.titulo_seccion("2.5  LAS MISIONES", "Hay seis tipos distintos", color="FF6B5A")
    m.texto(
        "Cada misión es de uno de estos seis tipos. Todas terminan igual: pulsas TERMINAR y "
        "recibes tus estrellas con una explicación de por qué. Si algo no te sale, puedes "
        "pulsar REINICIAR y volver a intentarlo las veces que quieras."
    )

    m.titulo_seccion("2.5.1  ESTUDIO DE ROSTROS", "Construye la cara de una emoción")
    m.bloque(
        "Estudio de rostros",
        [
            "Arriba te dicen qué emoción tienes que construir y te dan una pista.",
            "Mueve los cuatro deslizadores: Cejas, Ojos, Boca y Energía. La cara del espejo "
            "cambia mientras los mueves.",
            "Si hace falta, activa los detalles extra: Lágrima, Rubor, Sudor o Brillo.",
            "Empieza siempre por la boca y las cejas: son las que más cambian una emoción.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.5.2  DETECTIVE DE ESCUCHA", "Quédate con lo que de verdad se dijo")
    m.bloque(
        "Detective de escucha",
        [
            "Lee todo lo que te cuenta el personaje, sin prisa. Cuando lo tengas, pulsa YA LO HE "
            "ESCUCHADO.",
            "Marca solo las frases que la persona dijo de verdad. Cuidado: hay frases que suenan "
            "bien pero que nadie dijo.",
            "Después elige qué le respondes. Repetir con tus palabras lo que te contó funciona "
            "mejor que dar consejos enseguida o hablar de ti.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.5.3  PUENTE DE LA EMPATÍA", "Siente, piensa y necesita", color="7C5CFF")
    m.bloque(
        "Puente de la empatía",
        [
            "Lee la escena y mira los tres tablones del puente: SIENTE, PIENSA y NECESITA.",
            "Coloca una pieza en cada tablón. Puedes hacerlo de dos formas: manteniendo pulsada "
            "la pieza y arrastrándola, o tocando la pieza y después tocando el tablón.",
            "Hay tres piezas trampa que no encajan en ningún sitio: son juicios como es un "
            "exagerado, o soluciones rápidas como que se busque otro grupo.",
            "El puente se dibuja más completo cuantos más tablones aciertes.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.5.4  CONSTRUCTOR DE MENSAJES", "Di lo que te pasa sin romper nada")
    m.bloque(
        "Constructor de mensajes",
        [
            "Tienes que montar una frase con cuatro trozos: Yo me siento..., cuando..., "
            "porque... y Me gustaría...",
            "Para cada trozo hay tres fichas. Toca la que quieras y se colocará en su hueco.",
            "Mira el recuadro de arriba: tu frase se va montando sola y cambia de color. Verde "
            "es asertivo, rojo es un ataque y gris es esconderse.",
            "Con que una sola ficha sea un ataque, todo el mensaje se vuelve un ataque.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.5.5  SIMULADOR DE CONFLICTO", "Una discusión de verdad", color="FFC24B")
    m.bloque(
        "Simulador de conflicto",
        [
            "Arriba tienes tres barras: Calma, Confianza y Acuerdo. Cada cosa que dices las "
            "mueve hacia arriba o hacia abajo.",
            "Lee lo que te dice la otra persona y elige qué respondes. Debajo de cada respuesta "
            "pone de qué tipo es.",
            "Si la Calma o la Confianza bajan demasiado, la conversación se rompe. Pedir una "
            "pausa no es huir: es proteger la conversación.",
            "Para llegar a un acuerdo de verdad necesitas mantener la calma alta mientras "
            "propones algo que os sirva a los dos.",
        ],
    )
    m.salto()

    m.titulo_seccion("2.5.6  TERMÓMETRO EMOCIONAL", "Mide antes de reaccionar")
    m.bloque(
        "Termómetro emocional",
        [
            "Lee la escena y mueve el deslizador para decir del 0 al 10 cuánta emoción hay ahí "
            "de verdad. El termómetro sube y baja contigo.",
            "Después elige qué haces con esa emoción. Respirar sirve cuando estás muy encendido; "
            "hablarlo sirve cuando ya has bajado un poco.",
            "No todo lo que molesta es un 8. Medir bien evita responder con un cohete a un "
            "mosquito.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.6 RESULTADO
    m.titulo_seccion("2.6  TU RESULTADO Y TU RECOMPENSA", color="3FD08D")
    m.bloque(
        "Pantalla de resultado",
        [
            "Al pulsar TERMINAR aparecen tus estrellas (de 0 a 3) y tu puntuación.",
            "Debajo está el recuadro POR QUÉ. Léelo: ahí está lo que de verdad se aprende, y un "
            "consejo concreto de qué mejorar la próxima vez.",
        ],
        alto=7.5,
    )
    m.bloque(
        "Lo que has ganado",
        [
            "Más abajo verás la experiencia ganada, si has subido de nivel, la carta nueva que "
            "has conseguido y las insignias que acabas de ganar.",
            "Pulsa VOLVER AL MAPA para seguir, o PRACTICAR OTRA VEZ si quieres repetir la misión "
            "para conseguir más estrellas.",
        ],
        alto=7.0,
    )
    m.salto()

    # ------------------------------------------------------- 2.7 CARTAS
    m.titulo_seccion("2.7  CARTAS DE LA ISLA", "Tu colección", color="7C5CFF")
    m.bloque(
        "Colección de cartas",
        [
            "Hay 25 cartas en total. Arriba se ve cuántas llevas conseguidas.",
            "Puedes filtrar por Emociones, Habilidades, Personajes o Lugares con los botones de "
            "arriba.",
            "Las cartas que aún no tienes salen apagadas y con interrogaciones. Toca una carta "
            "que ya tengas para leer el dato que guarda dentro.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.8 INSIGNIAS
    m.titulo_seccion("2.8  INSIGNIAS", "Doce logros por conseguir", color="FF9A3C")
    m.bloque(
        "Pantalla de insignias",
        [
            "Arriba están las insignias que ya has conseguido y debajo las que te faltan.",
            "Cada insignia que te falta tiene una barra que te dice cuánto llevas. Por ejemplo: "
            "levantar tres puentes firmes o anotar tu ánimo diez veces.",
            "Nima te dice siempre cuál es la que tienes más cerca de conseguir.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.9 DIARIO
    m.titulo_seccion("2.9  DIARIO DE ÁNIMO", "Cuenta cómo estás", color="3FD08D")
    m.bloque(
        "Registrar tu ánimo",
        [
            "Elige una de las ocho emociones deslizando de lado. Cada una tiene su propia cara.",
            "Mueve el deslizador para decir con cuánta fuerza la sientes, del 1 al 10.",
            "Si quieres, escribe una nota corta contando qué ha pasado. Es opcional.",
            "Pulsa GUARDAR EN MI DIARIO. Si has puesto un 7 o más, la aplicación te ofrecerá ir "
            "al Rincón de calma.",
        ],
    )
    m.bloque(
        "Tus anotaciones",
        [
            "Debajo se van guardando todas tus anotaciones con su fecha. Si quieres borrar una, "
            "toca la X de la derecha.",
        ],
        alto=6.0,
    )
    m.salto()

    # ------------------------------------------------------- 2.10 NUMEROS
    m.titulo_seccion("2.10  TUS NÚMEROS", "Todo lo que llevas hecho")
    m.bloque(
        "Pantalla de estadísticas",
        [
            "Arriba tienes tus datos: nivel, experiencia, racha, misiones completadas, misiones "
            "dominadas y cartas.",
            "Después está el gráfico de tu ánimo de los últimos siete días, tu intensidad media "
            "y qué emoción anotas más.",
            "Al final ves cuánto llevas de cada zona y tus hitos de habilidad: rostros clavados, "
            "puentes firmes, mensajes asertivos perfectos y conflictos resueltos con calma.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.11 CALMA
    m.titulo_seccion("2.11  RINCÓN DE CALMA", "Respiración 4-4-6", color="63D2FF")
    m.bloque(
        "Rincón de calma",
        [
            "Pulsa EMPEZAR. El círculo empezará a crecer y a encogerse.",
            "Sigue el círculo: toma aire durante 4 segundos, sujétalo 4 y suéltalo despacio "
            "durante 6. La cuenta atrás te va guiando.",
            "Abajo se cuentan los ciclos que llevas. Puedes parar cuando quieras con el botón "
            "PAUSAR.",
        ],
    )
    m.aviso(
        "ESTÁ SIEMPRE DISPONIBLE",
        "El Rincón de calma no hay que desbloquearlo. Puedes entrar desde tu mochila cuando lo "
        "necesites, tengas el nivel que tengas."
    )
    m.salto()

    # ------------------------------------------------------- 2.12 REPASO
    m.titulo_seccion("2.12  PRACTICAR OTRA VEZ", "Sube tus estrellas", color="FF6B5A")
    m.bloque(
        "Pantalla de repaso",
        [
            "Aquí aparecen solo las misiones que ya completaste pero en las que todavía no "
            "tienes las tres estrellas.",
            "Toca cualquiera para repetirla. Repetir no es un castigo: la segunda vez se entiende "
            "mucho mejor por qué algo funciona.",
        ],
        alto=7.5,
    )
    m.salto()

    # ------------------------------------------------------- 2.13 PERFIL
    m.titulo_seccion("2.13  TU EXPLORADOR", "Tu perfil", color="7C5CFF")
    m.bloque(
        "Pantalla de perfil",
        [
            "Arriba está tu avatar, tu apodo, tu nivel y tu barra de experiencia.",
            "Debajo tienes cuatro barras con todo lo que llevas conseguido: misiones, cartas, "
            "insignias y zonas completadas.",
            "Pulsa CAMBIAR APODO Y AVATAR si quieres otro nombre u otra cara. No pierdes nada de "
            "lo conseguido.",
        ],
    )
    m.salto()

    # ------------------------------------------------------- 2.14 AJUSTES
    m.titulo_seccion("2.14  AJUSTES", "Tú decides cómo suena y cómo se ve")
    m.bloque(
        "Pantalla de ajustes",
        [
            "Puedes apagar los efectos de sonido y la vibración por separado.",
            "Puedes desactivar las animaciones si el movimiento te molesta, poner el texto más "
            "grande o activar el alto contraste.",
            "Más abajo se explica qué datos guarda la aplicación: solo tu apodo, tu avatar y tu "
            "progreso, y todo se queda dentro de tu móvil.",
            "El botón BORRAR MI PROGRESO deja la aplicación como recién instalada. Te lo "
            "pregunta dos veces, porque no se puede deshacer.",
        ],
    )
    m.salto()

    # ------------------------------------------------------------ 3. FAQ
    m.titulo_seccion("3. PREGUNTAS FRECUENTES")

    preguntas = [
        ("¿Necesito internet?",
         "No, nunca. La aplicación funciona entera sin conexión."),
        ("¿Tengo que poner mi nombre de verdad?",
         "No. Solo un apodo que elijas tú. La aplicación no pide correo, teléfono, ni ningún "
         "otro dato tuyo."),
        ("¿Y si fallo mucho una misión?",
         "No pasa nada. Puedes repetirla todas las veces que quieras, y cada vez te explica "
         "dónde falló la cosa."),
        ("Se me ha roto la racha de días. ¿He perdido algo?",
         "No. La racha solo es información. Si se corta, empieza otra y ya está."),
        ("¿Puede jugar otra persona en mi móvil?",
         "Hay un perfil por instalación. Para que juegue otra persona habría que borrar el "
         "progreso desde Ajustes."),
        ("No consigo arrastrar las piezas.",
         "Mantén el dedo pulsado sobre la pieza un momento antes de moverla. Si te resulta "
         "incómodo, toca la pieza y después toca el hueco donde quieres ponerla."),
        ("¿Dónde veo cuánto me falta para una insignia?",
         "En la pantalla de Insignias. Cada insignia pendiente tiene una barra con tu progreso."),
        ("¿Se pierden mis datos si desinstalo la aplicación?",
         "Sí. Todo está guardado dentro de tu móvil, así que al desinstalar se va con ella."),
    ]
    for pregunta, respuesta in preguntas:
        p = m.doc.add_paragraph()
        p.paragraph_format.space_before = Pt(8)
        p.paragraph_format.space_after = Pt(2)
        r = p.add_run(pregunta)
        r.bold = True
        r.font.color.rgb = TURQUESA
        m.texto(respuesta, tamanio=10.5)

    ruta = m.guardar(salida)
    print("Documento generado:", ruta)
    print("Pasos numerados:", m.n_paso)
    print("Marcos de imagen:", m.n_imagen)
    print("Tamanio:", os.path.getsize(ruta), "bytes")
    return ruta


if __name__ == "__main__":
    main()
