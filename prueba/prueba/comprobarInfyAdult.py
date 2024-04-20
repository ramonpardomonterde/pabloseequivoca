import json

# Cargar los archivos JSON en listas de Python
with open('fallas_adultas_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasAdultas = json.load(f)

with open('fallas_infantiles_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasInfantiles = json.load(f)

# Crear conjuntos de los 'id_falla' que tienen un boceto en cada lista
ids_con_boceto_adultas = {falla['id_falla'] for falla in fallasAdultas if falla['boceto'] is not None}
ids_con_boceto_infantiles = {falla['id_falla'] for falla in fallasInfantiles if falla['boceto'] is not None}

# Encontrar los 'id_falla' que tienen un boceto en una lista pero no en la otra
ids_con_boceto_solo_en_adultas = ids_con_boceto_adultas - ids_con_boceto_infantiles
ids_con_boceto_solo_en_infantiles = ids_con_boceto_infantiles - ids_con_boceto_adultas

print("IDs con boceto solo en fallas adultas:", ids_con_boceto_solo_en_adultas)
print("IDs con boceto solo en fallas infantiles:", ids_con_boceto_solo_en_infantiles)