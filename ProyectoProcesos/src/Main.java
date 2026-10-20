import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Scanner;

class Main{

    private static int DIFFICULTY = 5;

    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        /*System.out.println("=== Porfavor introduzca la semilla ===");
        String semilla = sc.nextLine();
        System.out.println(" === Porfavor introduzca la ruta del fichero ===");
        String rutaFichero = sc.nextLine();
        */
        String semilla = "Atun con mayonesa";
        String rutaFichero = "src/datos.txt";
        Integer nonce = 0;
        String dificultad = "";
        boolean valido = true;
        for (int i = 0; i < DIFFICULTY; i++){
            dificultad = dificultad + "0";
        }



        try {
            File file = new File(rutaFichero);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String linea;

            String hashOrigenSemilla = calcularHash(semilla);

            while((linea = br.readLine()) != null) {
                if(!linea.isBlank()) { //Si la linea no esta en blanco imprime en la terminal la linea
                    nonce = 0;
                    valido = true;
                    while(valido){
                        String contenidoParaElNuevoHash = hashOrigenSemilla + "|" + linea +  "|" + nonce.toString();
                        String hashNuevo = calcularHash(contenidoParaElNuevoHash);

                        if (!hashNuevo.startsWith(dificultad)){
                            nonce++;

                        } else{
                            System.out.println("Datos: " + linea);
                            System.out.println("Nonce: " + nonce);
                            System.out.println("Contenido: " + contenidoParaElNuevoHash);
                            System.out.println("Hash Original de la Semilla: " + hashOrigenSemilla);
                            System.out.println("Hash nuevo: " + hashNuevo);

                            System.out.println("\n");
                            hashOrigenSemilla = hashNuevo;
                            valido = false;
                        }
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
