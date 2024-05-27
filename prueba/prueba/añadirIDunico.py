import json

# Leer el archivo de fallas infantiles
with open('fallas_infantiles_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasInfantiles = json.load(f)

# Encontrar el último objectid existente
lastObjectIdInfantiles = max(obj['objectid'] for obj in fallasInfantiles if 'objectid' in obj)

# Iterar a través de cada objeto y asignar un nuevo objectid si no existe
for obj in fallasInfantiles:
    if 'objectid' not in obj:
        lastObjectIdInfantiles += 1
        obj['objectid'] = lastObjectIdInfantiles

# Escribir los objetos actualizados de nuevo al archivo
with open('fallas_infantiles_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)

# Repetir los pasos para el archivo de fallas adultas
with open('fallas_adultas_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasAdultas = json.load(f)

lastObjectIdAdultas = max(obj['objectid'] for obj in fallasAdultas if 'objectid' in obj)

for obj in fallasAdultas:
    if 'objectid' not in obj:
        lastObjectIdAdultas += 1
        obj['objectid'] = lastObjectIdAdultas

with open('fallas_adultas_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)