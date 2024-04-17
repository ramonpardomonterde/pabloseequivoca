import json
import re

# Variables PRUEDE HACER FALTA LA RUTA SI NO ESTAN EN LA MISMA CARPETA
archivos_premios = ['premiosInfantiles.txt', 'premiosIngeninoInfantiles.txt']
archivo_objetos = 'falles-infantils-fallas-infantiles.json'
archivo_final = 'falles-fallas_con_premios.json'

premios = {}
premiosE = {}

for archivo in archivos_premios:
    # Leer el archivo de texto
    with open(archivo, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    # Parsear cada línea para extraer la ID y el premio
    for line in lines:
        # Usar una expresión regular para encontrar todos los números en la línea
        numbers = re.findall(r'\d+', line.strip())

        # Si la línea contiene al menos dos números, el primer número es el premio y el segundo número es la ID
        if len(numbers) >= 2:
            premio, id = numbers[0], numbers[1]
            if 'Ingenino' in archivo:
                premiosE[id] = premio
            else:
                premios[id] = premio

# Leer el archivo JSON que contiene los objetos
with open(archivo_objetos, 'r', encoding='utf-8') as f:
    objetos = json.load(f)

# Para cada objeto en el archivo JSON, buscar su ID en los archivos JSON de premios
for objeto in objetos:
    id_falla = str(objeto['id_falla'])
    if id_falla in premios:
        objeto['Premio'] = premios[id_falla]
    if id_falla in premiosE:
        objeto['PremioE'] = premiosE[id_falla]

# Escribir los objetos modificados en un nuevo archivo JSON
with open(archivo_final, 'w', encoding='utf-8') as f:
    json.dump(objetos, f, ensure_ascii=False, indent=4)