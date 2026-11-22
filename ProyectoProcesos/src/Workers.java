import java.io.*;
import java.nio.charset.StandardCharsets;
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
