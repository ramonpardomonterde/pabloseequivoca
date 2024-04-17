import json
import re
import os

# Lista de archivos de texto para procesar
archivos = ['premiosInfantiles.txt', 'premiosIngenioInfantiles.txt', 'premiosAdultas.txt', 'premiosIngenioAdultas.txt']

for archivo in archivos:
    # Leer el archivo de texto
    with open(archivo, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    data = {}

    # Parsear cada línea para extraer la ID y el premio
    for line in lines:
        # Usar una expresión regular para encontrar todos los números en la línea
        numbers = re.findall(r'\d+', line.strip())

        # Si la línea contiene al menos dos números, el primer número es el premio y el segundo número es la ID
        if len(numbers) >= 2:
            premio, id = numbers[0], numbers[1]
            data[id] = premio
        else:
            # Si la línea no contiene una ID y un premio, la guardamos tal cual
            data[line.strip()] = ""

    nombre_archivo = os.path.splitext(archivo)[0]

    # Escribir el archivo JSON modificado
    with open(f'formateado_{nombre_archivo}.json', 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)