import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Scanner;

public class Main {

    private static final int n = 1000;
    private static final int DIFFICULTY = 3;
    private static final int N_PROCESOS = 4;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numeroProcesos = N_PROCESOS;

        //String semilla = "Atun con mayonesa";
        //String rutaFichero = "src/datos.txt";

        System.out.println("=== Porfavor introduzca la semilla ===");
        String semilla = sc.nextLine();

        if(semilla.isBlank()){
            System.out.println("Semilla proporcionada invalida");
            return;
        }

        System.out.println(" === Porfavor introduzca la ruta del fichero ===");
        String rutaFichero = sc.nextLine();

        if (rutaFichero.isBlank()){
            System.out.println("Ruta proporcionada inválida");
            return;
        }

        File f = new File(rutaFichero);

        if (!f.exists() || !f.isFile() || !f.canRead()) {
            System.out.println("El fichero no existe o no se puede leer: " + f.getAbsolutePath());
            return;
        }

        String hashAnterior = calcularHash(semilla);


        try (BufferedReader br = new BufferedReader(
                new FileReader(f, StandardCharsets.UTF_8))) {

            String linea;

            //Recorre las lineas del fichero
            while ((linea = br.readLine()) != null) {

                if (linea.isBlank()) {
                    continue;
                }

                String hashEncontrado = null;
                int inicio = 0;
                int rango = n;

                // Busca un hash válido para la línea actual
                while (hashEncontrado == null) {

                    int finLote = inicio + rango - 1;
                    int chunkSize = (int) Math.ceil(
                            (double) rango / numeroProcesos);

                    ArrayList<Process> procesos = new ArrayList<>();
                    java.util.Set<Process> cancelados = new java.util.HashSet<>();
                    ArrayList<BufferedReader> lectores = new ArrayList<>();

                    // Lanzamos los cuatro workers para buscar un hash valido
                    for (int i = 0; i < numeroProcesos; i++) {

                        int start = inicio + i * chunkSize;
                        int end = Math.min(
                                finLote, start + chunkSize - 1);

                        if (start > finLote) {
                            break;
                        }

                        try {
                            String cp = System.getProperty("java.class.path");

                            ProcessBuilder pb = new ProcessBuilder(
                                    "java", "-cp", cp, "Workers",
                                    hashAnterior,
                                    linea,
                                    String.valueOf(DIFFICULTY),
                                    String.valueOf(start),
                                    String.valueOf(end)
                            );

                            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                            Process proceso = pb.start();
                            procesos.add(proceso);

                            BufferedReader lector = new BufferedReader(
                                    new InputStreamReader(
                                            proceso.getInputStream()));

                            lectores.add(lector);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Leemos los resultados y miramos si el hash es valido
                    for (int i = 0; i < lectores.size(); i++) {

                        try {
                            String resultado = lectores.get(i).readLine();

                            if (resultado == null
                                    || resultado.equals("SIN_RESULTADO")) {
                                continue;
                            }

                            String[] partes = resultado.split("\\|");

                            if (partes.length < 2) {
                                continue;
                            }

                            int nonce = Integer.parseInt(partes[0]);
                            String hash = partes[1];

                            String contenido = hashAnterior + "|"
                                    + linea + "|" + nonce;

                            String hashCalculado = calcularHash(contenido);

                            if (hashCalculado.equals(hash)
                                    && hash.startsWith(
                                    "0".repeat(DIFFICULTY))) {

                                hashEncontrado = hash;

                                System.out.println("Hash encontrado: " + hash +" Nonce usado: " + nonce);

                                // Detenemos los otros procesos
                                for (int j = 0; j < procesos.size(); j++) {
                                    if (j != i) {
                                        // destroyForcibly fuerza la terminación del proceso
                                        // Es diferente del destroy normal que solicita la terminación
                                        // Es equivalente en comportamiento a SIGKILL
                                        // Se uso debido a que permite terminar un proceso directamente desde java y
                                        // SIGKILL no funcion en mi equipo de casa que es un Windows
                                        cancelados.add(procesos.get(j));
                                        procesos.get(j).destroyForcibly();
                                    }
                                }

                                break;
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Esperamos a que terminen los procesos.
                    for (Process proceso : procesos) {
                        try {
                            int codigo = proceso.waitFor();

                            if (codigo != 0 && !cancelados.contains(proceso)) {
                                System.out.println(
                                        "Error en un proceso Worker. Código: " + codigo
                                );
                            }

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println("Se interrumpió la espera de los procesos.");
                            return;
                        }
                    }

                    // Si nadie encontró un hash, ampliamos el rango.
                    if (hashEncontrado == null) {
                        inicio += rango;
                        rango *= 2;

                        System.out.println(
                                "Ampliando búsqueda. Próximo inicio: "
                                        + inicio);
                    }

                    for (BufferedReader lector : lectores) {
                        try {
                            lector.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                // Actualizamos la cadena para el siguiente bloque del fichero.
                hashAnterior = hashEncontrado;

                System.out.println("Hash aceptado: " + hashAnterior);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcularHash(String texto) {
        try {
            // MessageDigest es una clase que permite calcular resúmenes criptográficos
            // (Es un procedimiento matemático definido que se utiliza para transformar
            // información o generar valores criptográficos) y solicita un instacia de SHA-256

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Esta linea realiza dos operaciones: Transforma el texto en una secuencia de bytes y
            // calcula el SHA-256 de esos valores
            byte[] hashBytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));

            // Retorna los bytes como una cadena hexadecimal. HexFormat es la clase para trabajar
            // con hexadecimal, HexFormat.of obtiene un formateador hexadecimal predeterminado y
            // .formatHex(hashBytes) tranforma el array en texto hexadecimal
            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}