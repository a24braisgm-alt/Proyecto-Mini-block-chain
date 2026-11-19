import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Workers {
    public static void main(String[] args) {
        String dificultad = "";
        String hashAnterior = args[0];
        String linea = args[1];
        int difficultad = Integer.parseInt(args[2]);
        int start = Integer.parseInt(args[3]);
        int end = Integer.parseInt(args[4]);

        for (int i = 0; i < difficultad; i++){
            dificultad = dificultad + "0";
        }

        String hashOrigenSemilla = hashAnterior;
        for(int nonce = start; nonce <= end; nonce++){
            String contenidoParaElNuevoHash = hashOrigenSemilla + "|" + linea +  "|" + Integer.toString(nonce);
            String hashNuevo = calcularHash(contenidoParaElNuevoHash);

            if (hashNuevo.startsWith(dificultad)){
                System.out.println(nonce + "|" + hashNuevo);                /*
                System.out.println("Datos: " + linea);
                System.out.println("Nonce: " + nonce);
                System.out.println("Contenido: " + contenidoParaElNuevoHash);
                System.out.println("Hash Original de la Semilla: " + hashOrigenSemilla);
                System.out.println("Hash nuevo: " + hashNuevo);

                System.out.println("\n");
                hashOrigenSemilla = hashNuevo;*/
                return;
            }
        }
        System.out.println("SIN_RESULTADO");
    }

    public static String calcularHash(String texto) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(texto.getBytes());

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
