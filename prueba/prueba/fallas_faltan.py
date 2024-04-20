import json
import requests

def url_exists(url):
    try:
        response = requests.get(url)
        return response.status_code == 200
    except requests.exceptions.RequestException:
        return False

# Cargar el archivo JSON
with open('fallas_adultas_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasAdultas = json.load(f)

    # Cargar el archivo JSON
with open('fallas_infantiles_sinPremios.json', 'r', encoding='utf-8') as f:
    fallasInfantiles = json.load(f)

# Extraer los valores de id_falla
id_fallas = [falla['id_falla'] for falla in fallasAdultas if 'id_falla' in falla]

# Crear una lista de todos los números desde 1 hasta 395
todos_los_numeros = list(range(1, 396))

# Encontrar los números que faltan
numeros_faltantes = [num for num in todos_los_numeros if num not in id_fallas]

print(numeros_faltantes)

try:
    for id_falla in numeros_faltantes:
        if(id_falla != 301 and id_falla != 315 and id_falla != 337 and id_falla != 345 and id_falla != 372 and id_falla != 376 and id_falla != 378 and id_falla != 382 and id_falla != 390):
            print(f"****** FALLA con id_falla {id_falla} ******")
            nombre = input("Introduce el nombre (deja en blanco para null): ")
            nombre = None if nombre == "" else nombre
            falleraInf = input("Introduce el nombre de la fallera infantil (deja en blanco para null): ")
            falleraInf = "NO HAY" if falleraInf == "" else falleraInf
            presidenteInf = input("Introduce el nombre del presidente infantil (deja en blanco para null): ")
            presidenteInf = "NO HAY" if presidenteInf == "" else presidenteInf
            fallera = input("Introduce el nombre de la fallera (deja en blanco para null): ")
            fallera = "NO HAY" if fallera == "" else fallera
            presidente = input("Introduce el nombre del presidente (deja en blanco para null): ")
            presidente = "NO HAY" if presidente == "" else presidente
            anyo_fundacion = input("Introduce el año de fundación (deja en blanco para null): ")
            anyo_fundacion = None if anyo_fundacion == "" else int(anyo_fundacion)
            distintivo = input("Introduce el distintivo (deja en blanco para null): ")
            distintivo = None if distintivo == "" else distintivo
            lon = input("Introduce la longitud (deja en blanco para null): ")
            lon = None if lon == "" else float(lon)
            lat = input("Introduce la latitud (deja en blanco para null): ")
            lat = None if lat == "" else float(lat)

            print(f"----- ADULTA {id_falla} -----")
            seccion = input("Introduce la sección (deja en blanco para null): ")
            seccion = None if seccion == "" else seccion
            artista = input("Introduce el nombre del artista (deja en blanco para null): ")
            artista = None if artista == "" else artista
            lema = input("Introduce el lema (deja en blanco para null): ")
            lema = None if lema == "" else lema
            if(url_exists(f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bm.jpg")):
                print("Boceto encontrado y añadido automáticamente.")
                boceto = f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bm.jpg"
            else:
                boceto = input("Introduce el URL del boceto (deja en blanco para null): ")
                boceto = None if boceto == "" else boceto
            experim = input("Introduce el valor de experim (deja en blanco para null): ")
            experim = None if experim == "" else int(experim)

            # Crear el objeto falla
            falla = {
                'id_falla': id_falla,
                'nombre': nombre,
                'seccion': seccion,
                'fallera': fallera,
                'presidente': presidente,
                'artista': artista,
                'lema': lema,
                'anyo_fundacion': anyo_fundacion,
                'distintivo': distintivo,
                'boceto': boceto,
                'experim': experim,
                'geo_point_2d': {
                    'lon': lon,
                    'lat': lat
                }
            }

            # Agregar la falla a la lista
            fallasAdultas.append(falla)

            print(f"------ INFANTIL {id_falla} ------")
            seccion = input("Introduce la sección (deja en blanco para null): ")
            seccion = None if seccion == "" else seccion
            artista = input("Introduce el nombre del artista (deja en blanco para null): ")
            artista = None if artista == "" else artista
            lema = input("Introduce el lema (deja en blanco para null): ")
            lema = None if lema == "" else lema
            if(url_exists(f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bi.jpg")):
                print("Boceto encontrado y añadido automáticamente.")
                boceto = f"https://mapas.valencia.es/WebsMunicipales/layar/img/fallasvalencia/2024_{id_falla}_bi.jpg"
            else:
                boceto = input("Introduce el URL del boceto (deja en blanco para null): ")
                boceto = None if boceto == "" else boceto
            experim = input("Introduce el valor de experim (deja en blanco para null): ")
            experim = None if experim == "" else int(experim)

            # Crear el objeto falla
            falla = {
                'id_falla': id_falla,
                'nombre': nombre,
                'seccion': seccion,
                'fallera': falleraInf,
                'presidente': presidenteInf,
                'artista': artista,
                'lema': lema,
                'anyo_fundacion': anyo_fundacion,
                'distintivo': distintivo,
                'boceto': boceto,
                'experim': experim,
                'geo_point_2d': {
                    'lon': lon,
                    'lat': lat
                }
            }

            # Agregar la falla a la lista
            fallasInfantiles.append(falla)

except KeyboardInterrupt:
    print("\nInterrupción del usuario detectada. Guardando los datos...")

# Guardar el archivo JSON
with open('fallas_adultas_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)

with open('fallas_infantiles_sinPremios.json', 'w', encoding='utf-8') as f:
    json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)

print("Datos guardados con éxito.")