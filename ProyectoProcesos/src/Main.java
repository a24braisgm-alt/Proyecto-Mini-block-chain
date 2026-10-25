import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Scanner;

class Main{

    public static final int n = 1000;
    private static final int DIFFICULTY = 3;
    private static final int N_PROCESOS = 4;

    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        /*System.out.println("=== Porfavor introduzca la semilla ===");
        String semilla = sc.nextLine();
        System.out.println(" === Porfavor introduzca la ruta del fichero ===");
        String rutaFichero = sc.nextLine();
        */

        // === Vamos a usar chunks para nonce osea dividiremos para cada proceso un rango que seran
        // los nonce que probaran hasta encontrar un resultado
        String semilla = "Atun con mayonesa";
        String rutaFichero = "src/datos.txt";
        int rangosDePrueba = n;
        int numeroProcesos = Math.max(1,N_PROCESOS);
        int chunkSize = (int) Math.ceil((double) rangosDePrueba / numeroProcesos);//Ceil redondea
        String linea;
        String hashAnterior = calcularHash(semilla);

        File file = new File(rutaFichero);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((linea = br.readLine()) != null) {
                if(!linea.isBlank()) {
                    ArrayList<Process> procesos = new ArrayList<>();
                    ArrayList<BufferedReader> lectores = new ArrayList<>();
                for(int i = 0; i<numeroProcesos;i++) {
                    int start = i * chunkSize;
                    int end = Math.min(rangosDePrueba - 1, start + chunkSize - 1);

                    try {
                        String cp = System.getProperty("java.class.path");
                        ProcessBuilder pb = new ProcessBuilder("java", "-cp", cp, "Workers",
                                hashAnterior,
                                linea,
                                Integer.toString(DIFFICULTY),
                                String.valueOf(start),
                                String.valueOf(end));
                        Process procesoHijo = pb.start();
                        procesos.add(procesoHijo);
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(procesoHijo.getInputStream()));
                        lectores.add(br2);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                String hashEncontrado = null;

                for (BufferedReader br2 : lectores) {
                    try {
                        String resultado = br2.readLine();

                        if (resultado != null && !resultado.equals("SIN_RESULTADO") && hashEncontrado == null) {
                            String[] partes = resultado.split("\\|");

                            int nonce = Integer.parseInt(partes[0]);
                            String hash = partes[1];

                            String contenido = hashAnterior + "|" + linea + "|" + nonce;
                            String hashCalculado = calcularHash(contenido);

                            if (hashCalculado.equals(hash)
                                    && hash.startsWith("0".repeat(DIFFICULTY))) {
                                hashEncontrado = hash;
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                for(Process proceso :procesos){
                    try {
                        proceso.waitFor();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (hashEncontrado != null) {
                    hashAnterior = hashEncontrado;
                    System.out.println("Hash aceptado: " + hashAnterior);
                }
            }

        }
        br.close();
    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    }

    public static int chunkSizeCalculator(){
        return n / N_PROCESOS;
    }

    public static String calcularHash(String texto) {
        try {

            // MessageDigest es una clase que permite calcular resúmenes criptográficos
            // (Es un procedimiento matemático definido que se utiliza para transformar
            // información o generar valores criptográficos) y solicita un instacia de SHA-256

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Esta linea realiza dos operaciones: Transforma el texto en una secuencia de bytes y
            // calcula el SHA-256 de esos valores
            byte[] hashBytes = md.digest(texto.getBytes());

            // Retorna los bytes como una cadena hexadecimal. HexFormat es la clase para trabajar
            // con hexadecimal, HexFormat.of obtiene un formateador hexadecimal predeterminado y
            // .formatHex(hashBytes) tranforma el array en texto hexadecimal
            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
