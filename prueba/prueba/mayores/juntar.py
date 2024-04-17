import json

# Leer el archivo JSON de premios
with open('formateado_premios.json', 'r', encoding='utf-8') as f:
    premios = json.load(f)

# Leer el archivo JSON de premiosE
with open('formateado_premiosE.json', 'r', encoding='utf-8') as f:
    premiosE = json.load(f)

# Leer el archivo JSON de otros premios
with open('formateado_otros.json', 'r', encoding='utf-8') as f:
    otros = json.load(f)

# Leer el archivo JSON que contiene los objetos
with open('falles-fallas.json', 'r', encoding='utf-8') as f:
    objetos = json.load(f)

# Para cada objeto en el archivo JSON, buscar su ID en los archivos JSON de premios
for objeto in objetos:
    id_falla = str(objeto['id_falla'])
    if id_falla in premios:
        objeto['Premio'] = premios[id_falla]
    if id_falla in premiosE:
        objeto['PremioE'] = premiosE[id_falla]
    if id_falla in otros:
        objeto['PremioOtro'] = otros[id_falla]

# Escribir los objetos modificados en un nuevo archivo JSON
with open('falles-fallas_con_premios.json', 'w', encoding='utf-8') as f:
    json.dump(objetos, f, ensure_ascii=False, indent=4)