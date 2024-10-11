import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws Exception {

        // TODO: Revisar implementación.

        InputStreamReader isr = new InputStreamReader(System.in); 
        BufferedReader br = new BufferedReader(isr);

        try {
            System.out.println("Nombre del archivo con la imagen a procesar: ");
            String ruta = br.readLine();
            Imagen imagen = new Imagen(ruta);

            System.out.println("Nombre del archivo con el mensaje a esconder: ");
            String ruta2 = br.readLine();

            int longitud = leerArchivoTexto(ruta2);
            char[] mensaje = new char[longitud];
            imagen.esconder(mensaje, longitud);
            imagen.escribirImagen("salida" + ruta2);
            // Ud deberia poder abrir el bitmap de salida en un editor de imágenes y no
            // debe percibir ningún cambio en la imagen, pese a tener modificaciones por
            // el mensaje que esconde.
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Nombre del archivo con el mensaje escondido: ");
            String ruta = br.readLine();
            System.out.println("Nombre del archivo para almacenar el mensaje recuperado: ");
            String salida = br.readLine();
            Imagen imagen = new Imagen(ruta);

            int longitud = imagen.leerLongitud();
            char[] mensaje = new char[longitud];
            imagen.recuperar(mensaje, longitud);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int leerArchivoTexto(String ruta) {
        int length = 0;
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(ruta))) {
            while (br.readLine() != null) {
            length++;
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return length;
    }
}
