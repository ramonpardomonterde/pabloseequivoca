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
    for fallas in fallasAdultas:
        if fallas['boceto'] is not None and 'mapas.valencia' not in fallas['boceto']:
            if(fallas['id_falla'] < 100):
                id_falla = str(fallas['id_falla']).zfill(3)
            else:
                id_falla = str(fallas['id_falla'])
            if(url_exists(f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bm.jpg")):
                respuesta = input(f"¿Quieres cambiar el boceto de la falla {fallas['id_falla']} por el de mapas.valencia? (s/n): ")

                if respuesta == 's':
                    fallas['boceto'] = f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bm.jpg"
                else:
                    print("No se ha cambiado el boceto")
            else:
                print(f"No se ha encontrado el boceto de la falla {fallas['id_falla']}")

    print("--------- INFANTILES ---------")

    for fallas in fallasInfantiles:
        if fallas['boceto'] is not None and 'mapas.valencia' not in fallas['boceto']:
            if(fallas['id_falla'] < 100):
                id_falla = str(fallas['id_falla']).zfill(3)
            else:
                id_falla = str(fallas['id_falla'])
            if(url_exists(f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bi.jpg")):
                respuesta = input(f"¿Quieres cambiar el boceto de la falla {fallas['id_falla']} por el de mapas.valencia? (s/n): ")

                if respuesta == 's':
                    fallas['boceto'] = f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bi.jpg"
                else:
                    print("No se ha cambiado el boceto")
            else:
                print(f"No se ha encontrado el boceto de la falla {fallas['id_falla']}")

except KeyboardInterrupt:
    print("\nInterrupción del usuario detectada. Guardando los datos...")

with open('fallas_adultas_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)

with open('fallas_infantiles_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)

    
print("Datos guardados con éxito.")