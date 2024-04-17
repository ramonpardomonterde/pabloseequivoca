import json
import re

# Leer el archivo de texto
with open('otros.txt', 'r', encoding='utf-8') as f:
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

# Escribir el archivo JSON modificado
with open('otros.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False)