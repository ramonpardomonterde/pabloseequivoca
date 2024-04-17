import json

# Lista de archivos JSON para formatear
archivos = ['otros.json', 'premios.json', 'premiosE.json']

for archivo in archivos:
    # Leer el archivo JSON existente
    with open(archivo, 'r', encoding='utf-8') as f:
        data = json.load(f)

    # Escribir el archivo JSON formateado
    with open(f'formateado_{archivo}', 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)