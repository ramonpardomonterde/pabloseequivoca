import json

# Leer el archivo JSON de premios
with open('formateado_premiosInfantiles.json', 'r', encoding='utf-8') as f:
    premios = json.load(f)

# Leer el archivo JSON de premiosE
with open('formateado_premiosIngenioInfantiles.json', 'r', encoding='utf-8') as f:
    premiosE = json.load(f)

with open('formateado_premiosAdultas.json', 'r', encoding='utf-8') as f:
    premiosAdultas = json.load(f)

# Leer el archivo JSON de premiosE
with open('formateado_premiosIngenioAdultas.json', 'r', encoding='utf-8') as f:
    premiosEAdultas = json.load(f)

# Leer el archivo JSON que contiene los objetos
with open('fallas_infantiles_sinPremios.json', 'r', encoding='utf-8') as f:
    data = json.load(f)
    fallasInfantiles = data['result']['records']

fallasInfantiles = sorted(fallasInfantiles, key=lambda x: x['id_falla'] if x['id_falla'] is not None else 1000)

with open('fallas_adultas_sinPremios.json', 'r', encoding='utf-8') as f:
    data = json.load(f)
    fallasAdultas = data['result']['records']

fallasAdultas = sorted(fallasAdultas, key=lambda x: x['id_falla'] if x['id_falla'] is not None else 1000)

# # Para cada objeto en el archivo JSON, buscar su ID en los archivos JSON de premios
# for falla in fallasInfantiles:
#     id_falla = str(falla['id_falla'])
#     if id_falla in premios:
#         falla['Premio'] = premios[id_falla]
#     if id_falla in premiosE:
#         falla['PremioE'] = premiosE[id_falla]

# for falla in fallasAdultas:
#     id_falla = str(falla['id_falla'])
#     if id_falla in premiosAdultas:
#         falla['Premio'] = premiosAdultas[id_falla]
#     if id_falla in premiosEAdultas:
#         falla['PremioE'] = premiosEAdultas[id_falla]

# # Escribir los objetos modificados en un nuevo archivo JSON
# with open('fallas_infantiles.json', 'w', encoding='utf-8') as f:
#     json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)

# with open('fallas_adultas.json', 'w', encoding='utf-8') as f:
#     json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)

# Para cada objeto en el archivo JSON, buscar su ID en los archivos JSON de premios
for falla in fallasInfantiles:
    id_falla = str(falla['id_falla'])
    if id_falla in premios:
        falla['Premio'] = premios[id_falla]
    if id_falla in premiosE:
        falla['PremioE'] = premiosE[id_falla]

for falla in fallasAdultas:
    id_falla = str(falla['id_falla'])
    if id_falla in premiosAdultas:
        falla['Premio'] = premiosAdultas[id_falla]
    if id_falla in premiosEAdultas:
        falla['PremioE'] = premiosEAdultas[id_falla]

# Realizar las modificaciones necesarias en fallasInfantiles y fallasAdultas

# Crear los objetos 'result' para mantener la misma estructura al guardar
resultInfantiles = {'result': {'records': fallasInfantiles}}
resultAdultas = {'result': {'records': fallasAdultas}}

# Escribir los objetos modificados en un nuevo archivo JSON
with open('fallas_infantiles.json', 'w', encoding='utf-8') as f:
    json.dump(resultInfantiles, f, ensure_ascii=False, indent=4)

with open('fallas_adultas.json', 'w', encoding='utf-8') as f:
    json.dump(resultAdultas, f, ensure_ascii=False, indent=4)