#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""Empaqueta el codigo fuente de SocialKids en un zip con la raiz directa."""
import os
import zipfile

SALIDA = "deliverables/SocialKids-v1.0.0-source.zip"
EXCLUIR_DIRS = {"build", ".gradle", ".git", "deliverables", ".idea", ".kotlin"}
CARPETAS = ("app", "database", "docs", "gradle", "tools", ".github")
ARCHIVOS = (
    "build.gradle.kts", "settings.gradle.kts", "gradle.properties",
    "gradlew", "gradlew.bat", "README.md", ".gitignore",
)


def main():
    raiz = os.getcwd()
    os.makedirs("deliverables", exist_ok=True)
    total = 0
    with zipfile.ZipFile(SALIDA, "w", zipfile.ZIP_DEFLATED) as z:
        for nombre in ARCHIVOS:
            if os.path.exists(nombre):
                z.write(nombre, nombre)
                total += 1
        for carpeta in CARPETAS:
            if not os.path.isdir(carpeta):
                continue
            for dirpath, dirnames, filenames in os.walk(carpeta):
                dirnames[:] = [d for d in dirnames if d not in EXCLUIR_DIRS]
                for fn in filenames:
                    ruta = os.path.join(dirpath, fn)
                    rel = os.path.relpath(ruta, raiz).replace(os.sep, "/")
                    z.write(ruta, rel)
                    total += 1
    with zipfile.ZipFile(SALIDA) as z:
        nivel1 = sorted({x.split("/")[0] for x in z.namelist()})
    print("archivos:", total)
    print("tamanio:", os.path.getsize(SALIDA), "bytes")
    print("raiz del zip:", nivel1)
    assert not any(n.lower().startswith("socialkids") for n in nivel1), \
        "El zip no debe contener una carpeta del proyecto anidada"


if __name__ == "__main__":
    main()
