#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Resume los resultados de la compilacion para el panel de GitHub Actions."""
import glob
import io
import os
import re
import sys


def resumen_pruebas():
    tot = fal = err = omit = 0
    for ruta in glob.glob("app/build/test-results/testDebugUnitTest/*.xml"):
        cab = io.open(ruta, encoding="utf-8").read(800)
        m = re.search(
            r'tests="(\d+)" skipped="(\d+)" failures="(\d+)" errors="(\d+)"', cab
        )
        if m:
            tot += int(m.group(1))
            omit += int(m.group(2))
            fal += int(m.group(3))
            err += int(m.group(4))
    return tot, omit, fal, err


def resumen_lint():
    ruta = "app/build/reports/lint-results-debug.xml"
    if not os.path.exists(ruta):
        return None
    s = io.open(ruta, encoding="utf-8", errors="replace").read()
    pares = re.findall(r'id="[^"]+"\s+severity="([^"]+)"', s)
    errores = sum(1 for p in pares if p == "Error" or p == "Fatal")
    avisos = sum(1 for p in pares if p == "Warning")
    return errores, avisos


def main():
    lineas = ["## SocialKids - resultado de la compilacion", ""]

    apk = "deliverables/SocialKids-v1.0.0.apk"
    if os.path.exists(apk):
        lineas.append("### APK")
        lineas.append("- Archivo: `SocialKids-v1.0.0.apk`")
        lineas.append("- Tamanio: %.1f MB" % (os.path.getsize(apk) / 1048576.0))
        sumas = "deliverables/SHA256SUMS.txt"
        if os.path.exists(sumas):
            for linea in io.open(sumas, encoding="utf-8"):
                lineas.append("- `%s`" % linea.strip())
        lineas.append("")

    tot, omit, fal, err = resumen_pruebas()
    lineas.append("### Pruebas unitarias")
    lineas.append("- Ejecutadas: %d" % tot)
    lineas.append("- Fallidas: %d" % fal)
    lineas.append("- Errores: %d" % err)
    lineas.append("- Omitidas: %d" % omit)
    lineas.append("")

    lint = resumen_lint()
    if lint:
        lineas.append("### Lint")
        lineas.append("- Errores: %d" % lint[0])
        lineas.append("- Avisos: %d" % lint[1])
        lineas.append("")

    texto = "\n".join(lineas) + "\n"
    destino = os.environ.get("GITHUB_STEP_SUMMARY")
    if destino:
        with io.open(destino, "a", encoding="utf-8") as f:
            f.write(texto)
    sys.stdout.write(texto)
    return 1 if (fal or err) else 0


if __name__ == "__main__":
    sys.exit(main())
