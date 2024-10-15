import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------------------------------------");
        System.out.println("Tamaño de página:");
        int P = scanner.nextInt();
        scanner.nextLine();
        

        System.out.println("Ingrese la Ruta (path) de la imagen:");
        // Ejemplo de ruta: "Archivos/caso2-parrots.bmp"
        String rutaImagen = scanner.nextLine();

        System.out.println("Ingrese el nombre que quiere ponerle al archivo de referencias:");
        // Ejemplo de archivo de salida: "referencias.txt"
        String filePath = scanner.nextLine();

        Referencias.generarReferencias(rutaImagen, P, filePath);

        System.out.println("--------------------------------------------------");
        System.out.println("Archivo de Referencias generado correctamente.");
        scanner.close();
    }
}
