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

        try {
            String cp = System.getProperty("java.class.path");
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", cp, "Workers", semilla, rutaFichero, Integer.toString(DIFFICULTY));
            pb.inheritIO();
            Process procesoHijo = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }



}
