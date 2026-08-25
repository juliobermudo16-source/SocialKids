#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Convierte los documentos Markdown de SocialKids en PDF reales.

Uso:
    python tools/md_a_pdf.py docs/MEMORIA_DESCRIPTIVA.md docs/pdf/MEMORIA_DESCRIPTIVA.pdf "Memoria descriptiva"

Soporta encabezados, parrafos, listas, tablas, bloques de codigo, citas y reglas.
No necesita conexion: solo reportlab y las fuentes base de PDF.
"""
import io
import os
import re
import sys

from reportlab.lib import colors
from reportlab.lib.enums import TA_JUSTIFY
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (
    BaseDocTemplate, Frame, PageBreak, PageTemplate, Paragraph, Preformatted,
    Spacer, Table, TableStyle,
)

TURQUESA = colors.HexColor("#12B5B0")
CORAL = colors.HexColor("#FF6B5A")
VIOLETA = colors.HexColor("#7C5CFF")
NOCHE = colors.HexColor("#1B2B3F")
GRIS = colors.HexColor("#6B7A8D")
PAPEL = colors.HexColor("#FFF7EC")


def estilos():
    base = getSampleStyleSheet()
    s = {}
    s["cuerpo"] = ParagraphStyle(
        "cuerpo", parent=base["BodyText"], fontName="Helvetica", fontSize=9.6,
        leading=14.4, alignment=TA_JUSTIFY, textColor=NOCHE, spaceAfter=5,
    )
    s["h1"] = ParagraphStyle(
        "h1", parent=base["Heading1"], fontName="Helvetica-Bold", fontSize=19,
        leading=23, textColor=TURQUESA, spaceBefore=14, spaceAfter=8,
    )
    s["h2"] = ParagraphStyle(
        "h2", parent=base["Heading2"], fontName="Helvetica-Bold", fontSize=14,
        leading=18, textColor=CORAL, spaceBefore=12, spaceAfter=5,
    )
    s["h3"] = ParagraphStyle(
        "h3", parent=base["Heading3"], fontName="Helvetica-Bold", fontSize=11.5,
        leading=15, textColor=VIOLETA, spaceBefore=9, spaceAfter=4,
    )
    s["h4"] = ParagraphStyle(
        "h4", parent=s["h3"], fontSize=10.2, textColor=NOCHE,
    )
    s["lista"] = ParagraphStyle(
        "lista", parent=s["cuerpo"], leftIndent=11, bulletIndent=3, spaceAfter=2,
    )
    s["cita"] = ParagraphStyle(
        "cita", parent=s["cuerpo"], leftIndent=12, rightIndent=8,
        textColor=GRIS, fontName="Helvetica-Oblique", borderPadding=4,
    )
    s["codigo"] = ParagraphStyle(
        "codigo", parent=base["Code"], fontName="Courier", fontSize=7.6, leading=10,
        textColor=NOCHE,
    )
    s["celda"] = ParagraphStyle(
        "celda", parent=s["cuerpo"], fontSize=8, leading=11, alignment=0, spaceAfter=0,
    )
    s["celdaCab"] = ParagraphStyle(
        "celdaCab", parent=s["celda"], fontName="Helvetica-Bold", textColor=colors.white,
    )
    s["portadaTitulo"] = ParagraphStyle(
        "portadaTitulo", parent=s["h1"], fontSize=32, leading=37, alignment=1,
        textColor=TURQUESA, spaceBefore=0,
    )
    s["portadaSub"] = ParagraphStyle(
        "portadaSub", parent=s["cuerpo"], fontSize=13, leading=18, alignment=1,
        textColor=CORAL,
    )
    s["portadaPie"] = ParagraphStyle(
        "portadaPie", parent=s["cuerpo"], fontSize=9.5, leading=14, alignment=1,
        textColor=GRIS,
    )
    return s


ESCAPES = (("&", "&amp;"), ("<", "&lt;"), (">", "&gt;"))


def en_linea(texto):
    """Traduce el marcado en linea de Markdown a las etiquetas de reportlab."""
    for a, b in ESCAPES:
        texto = texto.replace(a, b)
    texto = re.sub(r"`([^`]+)`", r'<font face="Courier" size="8.6">\1</font>', texto)
    texto = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", texto)
    texto = re.sub(r"(?<!\*)\*([^*]+)\*(?!\*)", r"<i>\1</i>", texto)
    # Enlaces: se conserva solo el texto visible.
    texto = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", texto)
    return texto


def fila_tabla(linea):
    partes = [c.strip() for c in linea.strip().strip("|").split("|")]
    return partes


def convertir(md_path, pdf_path, subtitulo):
    s = estilos()
    lineas = io.open(md_path, encoding="utf-8").read().split("\n")

    titulo = "SocialKids"
    for l in lineas:
        if l.startswith("# "):
            titulo = l[2:].strip()
            break

    flujo = []

    # ------------------------------------------------------------ portada
    flujo.append(Spacer(1, 55 * mm))
    flujo.append(Paragraph("SocialKids", s["portadaTitulo"]))
    flujo.append(Spacer(1, 3 * mm))
    flujo.append(Paragraph("Isla Conecta", s["portadaSub"]))
    flujo.append(Spacer(1, 14 * mm))
    flujo.append(Paragraph(en_linea(titulo), ParagraphStyle(
        "pt2", parent=s["h2"], alignment=1, fontSize=17, leading=22, textColor=NOCHE)))
    flujo.append(Spacer(1, 4 * mm))
    flujo.append(Paragraph(subtitulo, s["portadaPie"]))
    flujo.append(Spacer(1, 30 * mm))
    flujo.append(Paragraph(
        "Aplicacion Android educativa para ninias y ninios de 8 a 12 anios<br/>"
        "Empatia · Comunicacion asertiva · Habilidades sociales<br/><br/>"
        "Version 1.0.0 · Kotlin · Jetpack Compose · Room · Sin conexion",
        s["portadaPie"]))
    flujo.append(PageBreak())

    # ------------------------------------------------------------ contenido
    i = 0
    primer_h1_saltado = False
    while i < len(lineas):
        linea = lineas[i]
        despojada = linea.strip()

        if not despojada:
            i += 1
            continue

        # Bloque de codigo
        if despojada.startswith("```"):
            i += 1
            buffer = []
            while i < len(lineas) and not lineas[i].strip().startswith("```"):
                buffer.append(lineas[i])
                i += 1
            i += 1
            if buffer:
                tabla = Table(
                    [[Preformatted("\n".join(buffer), s["codigo"])]],
                    colWidths=[165 * mm],
                )
                tabla.setStyle(TableStyle([
                    ("BACKGROUND", (0, 0), (-1, -1), PAPEL),
                    ("BOX", (0, 0), (-1, -1), 0.5, colors.HexColor("#E3D9C8")),
                    ("LEFTPADDING", (0, 0), (-1, -1), 7),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 7),
                    ("TOPPADDING", (0, 0), (-1, -1), 5),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
                ]))
                flujo.append(tabla)
                flujo.append(Spacer(1, 4))
            continue

        # Tabla
        if despojada.startswith("|") and i + 1 < len(lineas) and \
                re.match(r"^\|[\s:\-|]+\|$", lineas[i + 1].strip()):
            cabecera = fila_tabla(despojada)
            i += 2
            filas = []
            while i < len(lineas) and lineas[i].strip().startswith("|"):
                filas.append(fila_tabla(lineas[i].strip()))
                i += 1
            n = len(cabecera)
            datos = [[Paragraph(en_linea(c), s["celdaCab"]) for c in cabecera]]
            for f in filas:
                f = (f + [""] * n)[:n]
                datos.append([Paragraph(en_linea(c), s["celda"]) for c in f])
            ancho = 165.0 * mm
            tabla = Table(datos, colWidths=[ancho / n] * n, repeatRows=1)
            tabla.setStyle(TableStyle([
                ("BACKGROUND", (0, 0), (-1, 0), TURQUESA),
                ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#D6DEE7")),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, PAPEL]),
                ("LEFTPADDING", (0, 0), (-1, -1), 5),
                ("RIGHTPADDING", (0, 0), (-1, -1), 5),
                ("TOPPADDING", (0, 0), (-1, -1), 3.5),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 3.5),
            ]))
            flujo.append(tabla)
            flujo.append(Spacer(1, 6))
            continue

        # Regla horizontal
        if re.match(r"^-{3,}$", despojada) or re.match(r"^={3,}$", despojada):
            flujo.append(Spacer(1, 3))
            regla = Table([[""]], colWidths=[165 * mm], rowHeights=[0.8])
            regla.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#E0E6EC"))]))
            flujo.append(regla)
            flujo.append(Spacer(1, 5))
            i += 1
            continue

        # Encabezados
        m = re.match(r"^(#{1,4})\s+(.*)$", despojada)
        if m:
            nivel = len(m.group(1))
            texto = m.group(2)
            if nivel == 1 and not primer_h1_saltado:
                primer_h1_saltado = True
                i += 1
                continue
            flujo.append(Paragraph(en_linea(texto), s["h%d" % nivel]))
            i += 1
            continue

        # Cita
        if despojada.startswith(">"):
            buffer = []
            while i < len(lineas) and lineas[i].strip().startswith(">"):
                buffer.append(lineas[i].strip().lstrip(">").strip())
                i += 1
            flujo.append(Paragraph(en_linea(" ".join(buffer)), s["cita"]))
            flujo.append(Spacer(1, 3))
            continue

        # Lista
        if re.match(r"^([-*]|\d+\.)\s+", despojada):
            while i < len(lineas) and re.match(r"^\s*([-*]|\d+\.)\s+", lineas[i]):
                bruto = lineas[i].strip()
                mm_ = re.match(r"^([-*]|\d+\.)\s+(.*)$", bruto)
                vinieta = "\u2022" if mm_.group(1) in ("-", "*") else mm_.group(1)
                flujo.append(Paragraph(en_linea(mm_.group(2)), s["lista"], bulletText=vinieta))
                i += 1
            flujo.append(Spacer(1, 3))
            continue

        # Parrafo (une lineas consecutivas)
        buffer = []
        while i < len(lineas) and lineas[i].strip() and \
                not re.match(r"^(#{1,4}\s|[-*]\s|\d+\.\s|\||>|```|-{3,}$)", lineas[i].strip()):
            buffer.append(lineas[i].strip())
            i += 1
        if buffer:
            flujo.append(Paragraph(en_linea(" ".join(buffer)), s["cuerpo"]))

    # ------------------------------------------------------------ documento
    os.makedirs(os.path.dirname(pdf_path) or ".", exist_ok=True)

    def decorar(canvas, doc):
        canvas.saveState()
        if doc.page > 1:
            canvas.setStrokeColor(colors.HexColor("#E0E6EC"))
            canvas.setLineWidth(0.5)
            canvas.line(22 * mm, 283 * mm, 188 * mm, 283 * mm)
            canvas.setFont("Helvetica-Bold", 8)
            canvas.setFillColor(TURQUESA)
            canvas.drawString(22 * mm, 286 * mm, "SocialKids")
            canvas.setFont("Helvetica", 8)
            canvas.setFillColor(GRIS)
            canvas.drawRightString(188 * mm, 286 * mm, subtitulo)
            canvas.setFont("Helvetica", 8)
            canvas.drawCentredString(105 * mm, 12 * mm, "%d" % doc.page)
        else:
            # Chispa decorativa de la portada
            canvas.setFillColor(colors.HexColor("#FFC24B"))
            cx, cy, r = 105 * mm, 232 * mm, 13 * mm
            p = canvas.beginPath()
            p.moveTo(cx, cy + r)
            p.curveTo(cx + r * 0.22, cy + r * 0.22, cx + r * 0.22, cy + r * 0.22, cx + r, cy)
            p.curveTo(cx + r * 0.22, cy - r * 0.22, cx + r * 0.22, cy - r * 0.22, cx, cy - r)
            p.curveTo(cx - r * 0.22, cy - r * 0.22, cx - r * 0.22, cy - r * 0.22, cx - r, cy)
            p.curveTo(cx - r * 0.22, cy + r * 0.22, cx - r * 0.22, cy + r * 0.22, cx, cy + r)
            canvas.drawPath(p, fill=1, stroke=0)
            canvas.setFillColor(TURQUESA)
            canvas.rect(22 * mm, 40 * mm, 166 * mm, 1.6 * mm, fill=1, stroke=0)
        canvas.restoreState()

    doc = BaseDocTemplate(
        pdf_path, pagesize=A4,
        leftMargin=22 * mm, rightMargin=23 * mm,
        topMargin=25 * mm, bottomMargin=20 * mm,
        title="SocialKids - " + titulo,
        author="Proyecto SocialKids",
        subject=subtitulo,
    )
    marco = Frame(doc.leftMargin, doc.bottomMargin, doc.width, doc.height, id="principal")
    doc.addPageTemplates([PageTemplate(id="normal", frames=[marco], onPage=decorar)])
    doc.build(flujo)
    return pdf_path


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)
    salida = convertir(sys.argv[1], sys.argv[2], sys.argv[3] if len(sys.argv) > 3 else "")
    print("PDF generado:", salida, os.path.getsize(salida), "bytes")
