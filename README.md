# Proyecto-Mini-block-chain

Proyecto para la asignatura **Programación de Servicios y Procesos**. Consiste en desarrollar una aplicación Java que construye una cadena de bloques a partir de los datos de un fichero de texto.

## Descripción

La aplicación procesa un fichero de texto línea por línea. Cada línea representa los datos de un nuevo bloque que debe incorporarse a la cadena.

Para garantizar la integridad y la vinculación entre bloques, se utiliza el algoritmo criptográfico **SHA-256**. Cada bloque incorpora el hash del bloque anterior y requiere encontrar un valor `nonce` que permita obtener un hash que cumpla una dificultad determinada.

Este proceso se basa en el mecanismo **Proof of Work (PoW)**.

## Funcionamiento

El programa sigue estos pasos:

1. Solicita al usuario una semilla inicial y la ruta del fichero de texto.
2. Calcula el hash inicial a partir de la semilla proporcionada.
3. Lee el fichero y procesa su contenido línea por línea.
4. Para cada línea, busca un `nonce` que genere un hash válido utilizando el siguiente contenido:

   `hashAnterior + "|" + datosBloque + "|" + nonce`

5. Comprueba que el hash obtenido cumple la dificultad establecida.
6. Utiliza cuatro procesos externos para repartir el trabajo de búsqueda entre distintos rangos de valores `nonce`.
7. Cuando encuentra una solución válida, la incorpora a la cadena y utiliza su hash como referencia para el siguiente bloque.
8. Repite el procedimiento hasta procesar todas las líneas del fichero.

Si ningún proceso encuentra una solución en el rango actual, el programa amplía el rango de búsqueda y vuelve a repartir el trabajo.

## Tecnologías utilizadas

- **Java**: lenguaje de programación.
- **SHA-256**: algoritmo utilizado para generar los hashes.
- **ProcessBuilder**: creación y ejecución de procesos externos.
- **Process**: gestión y comunicación con los procesos Worker.
- **BufferedReader**: lectura de los datos del fichero y de las respuestas de los procesos.

## Configuración

Los principales parámetros de configuración se encuentran en `Main.java`:

| Parámetro | Valor | Descripción |
|---|---:|---|
| `n` | 1000 | Tamaño inicial del rango de búsqueda del nonce. |
| `DIFFICULTY` | 3 | Número de ceros iniciales que debe tener el hash. |
| `N_PROCESOS` | 4 | Número de procesos externos utilizados para repartir el trabajo. |

## Estructura del proyecto

El proyecto está compuesto por los siguientes archivos:

- `Main.java`: contiene el programa principal. Gestiona la entrada de datos, la lectura del fichero, la creación de procesos, la validación de resultados y la construcción de la cadena.
- `Workers.java`: contiene la lógica de los procesos trabajadores. Cada proceso recibe los datos del bloque y un rango de valores `nonce` para realizar la búsqueda de una solución válida.

## Ejecución

Los archivos `Main.java` y `Workers.java` deben estar compilados y disponibles en el classpath.

Desde la terminal, se puede ejecutar el programa mediante:

```bash
java Main
```

Al iniciarse, la aplicación solicitará:

- Una semilla inicial para generar el primer hash.
- La ruta del fichero de texto que contiene los datos de los bloques.

## Objetivo del proyecto

El objetivo es aplicar los conceptos de programación de servicios y procesos mediante la creación y coordinación de procesos externos, el reparto de tareas, la comunicación entre procesos y la validación de resultados en una aplicación basada en una cadena de bloques.
