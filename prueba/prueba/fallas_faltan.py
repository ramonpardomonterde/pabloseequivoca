import json

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

# try:
#     for id_falla in numeros_faltantes:
#         print(f"------FALLA ADULTA con id_falla {id_falla}------")
#         nombre = input("Introduce el nombre (deja en blanco para null): ")
#         nombre = None if nombre == "" else nombre
#         seccion = input("Introduce la sección (deja en blanco para null): ")
#         seccion = None if seccion == "" else seccion
#         fallera = input("Introduce el nombre de la fallera (deja en blanco para null): ")
#         fallera = None if fallera == "" else fallera
#         presidente = input("Introduce el nombre del presidente (deja en blanco para null): ")
#         presidente = None if presidente == "" else presidente
#         artista = input("Introduce el nombre del artista (deja en blanco para null): ")
#         artista = None if artista == "" else artista
#         lema = input("Introduce el lema (deja en blanco para null): ")
#         lema = None if lema == "" else lema
#         anyo_fundacion = input("Introduce el año de fundación (deja en blanco para null): ")
#         anyo_fundacion = None if anyo_fundacion == "" else int(anyo_fundacion)
#         distintivo = input("Introduce el distintivo (deja en blanco para null): ")
#         distintivo = None if distintivo == "" else distintivo
#         boceto = input("Introduce el URL del boceto (deja en blanco para null): ")
#         boceto = None if boceto == "" else boceto
#         experim = input("Introduce el valor de experim (deja en blanco para null): ")
#         experim = None if experim == "" else int(experim)
#         lon = input("Introduce la longitud (deja en blanco para null): ")
#         lon = None if lon == "" else float(lon)
#         lat = input("Introduce la latitud (deja en blanco para null): ")
#         lat = None if lat == "" else float(lat)

#         # Crear el objeto falla
#         falla = {
#             'id_falla': id_falla,
#             'nombre': nombre,
#             'seccion': seccion,
#             'fallera': fallera,
#             'presidente': presidente,
#             'artista': artista,
#             'lema': lema,
#             'anyo_fundacion': anyo_fundacion,
#             'distintivo': distintivo,
#             'boceto': boceto,
#             'experim': experim,
#             'geo_point_2d': {
#                 'lon': lon,
#                 'lat': lat
#             }
#         }

#         # Agregar la falla a la lista
#         fallasAdultas.append(falla)

#         print(f"------FALLA INFANTIL con id_falla {id_falla}------")
#         seccion = input("Introduce la sección (deja en blanco para null): ")
#         seccion = None if seccion == "" else seccion
#         fallera = input("Introduce el nombre de la fallera (deja en blanco para null): ")
#         fallera = None if fallera == "" else fallera
#         presidente = input("Introduce el nombre del presidente (deja en blanco para null): ")
#         presidente = None if presidente == "" else presidente
#         artista = input("Introduce el nombre del artista (deja en blanco para null): ")
#         artista = None if artista == "" else artista
#         lema = input("Introduce el lema (deja en blanco para null): ")
#         lema = None if lema == "" else lema
#         boceto = input("Introduce el URL del boceto (deja en blanco para null): ")
#         boceto = None if boceto == "" else boceto
#         experim = input("Introduce el valor de experim (deja en blanco para null): ")
#         experim = None if experim == "" else int(experim)

#         # Crear el objeto falla
#         falla = {
#             'id_falla': id_falla,
#             'nombre': nombre,
#             'seccion': seccion,
#             'fallera': fallera,
#             'presidente': presidente,
#             'artista': artista,
#             'lema': lema,
#             'anyo_fundacion': anyo_fundacion,
#             'distintivo': distintivo,
#             'boceto': boceto,
#             'experim': experim,
#             'geo_point_2d': {
#                 'lon': lon,
#                 'lat': lat
#             }
#         }

#         # Agregar la falla a la lista
#         fallasInfantiles.append(falla)

# except KeyboardInterrupt:
#     print("\nInterrupción del usuario detectada. Guardando los datos...")

# # Guardar el archivo JSON
# with open('fallas_adultas_sinPremios.json', 'w', encoding='utf-8') as f:
#     json.dump(fallasAdultas, f, ensure_ascii=False, indent=4)

# with open('fallas_infantiles_sinPremios.json', 'w', encoding='utf-8') as f:
#     json.dump(fallasInfantiles, f, ensure_ascii=False, indent=4)

# print("Datos guardados con éxito.")