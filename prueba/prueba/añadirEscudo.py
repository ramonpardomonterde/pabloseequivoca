import json
import requests
import webbrowser

def url_exists(url):
    try:
        response = requests.get(url)
        webbrowser.open(url)
        return response.status_code == 200
    except requests.exceptions.RequestException:
        return False
    
with open('fallas_adultas_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasAdultas = json.load(f)

    # Cargar el archivo JSON
with open('fallas_infantiles_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasInfantiles = json.load(f)

try:
    for fallaA in fallasAdultas:
        if(fallaA['id_falla'] < 100):
            id_falla = str(fallaA['id_falla']).zfill(3)
            url_escudo = f"https://bdfallas.com/img/escudos/{id_falla}.jpg"
            
            for fallaI in fallasInfantiles:
                if(fallaA['id_falla'] == fallaI['id_falla']):
                    if url_exists(url_escudo):
                        fallaA['escudo'] = url_escudo
                        fallaI['escudo'] = url_escudo


except requests.exceptions.RequestException as e:
    print(f"Error al comprobar la URL del escudo: {e}")

# Guardar las listas modificadas en archivos JSON
with open('fallas_adultas_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)

with open('fallas_infantiles_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)