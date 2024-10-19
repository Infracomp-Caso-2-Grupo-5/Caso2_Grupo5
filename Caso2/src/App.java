import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Simulacion.Simulacion;
public class App {
    public static void main(String[] args) {
        Boolean ejecutar = true;
        Scanner scanner = new Scanner(System.in);
        while (ejecutar) {
            System.out.println("--------------------------------------------------");
            System.out.println("Caso 2 Infraestructura Computacional: Grupo 5");
            System.out.println("1. Generar referencias de un mensaje escondido");
            System.out.println("2. Simular paginacion a partir de un Archivo de referencias");
            System.out.println("3. Esconder mensaje en una imagen");
            System.out.println("4. Revelar mensaje de una imagen");
            System.out.println("5. Salir");
            int opcion = scanner.nextInt();
            if (opcion == 1) {
                System.out.println("Ingrese el tamaño de página:");
                int P = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Ingrese la Ruta (path) de la imagen encriptada:");
                // Ejemplo de ruta: "Archivos/caso2-parrots.bmp"
                String rutaImagen = scanner.nextLine();

                System.out.println("Ingrese el nombre que quiere ponerle al archivo de referencias:");
                // Ejemplo de archivo de salida: "referencias.txt"
                String filePath = scanner.nextLine();

                Referencias.generarReferencias(rutaImagen, P, filePath);

                System.out.println("--------------------------------------------------");
                System.out.println("Archivo de Referencias generado correctamente.");
            } else if (opcion == 2) {
                System.out.print("Ingrese el número de marcos: ");
                int numMarcos = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Ingrese el nombre del archivo de referencias (incluya la extensión): ");
                String nombreArchivo = scanner.nextLine();
                List<Integer> referencias = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo));
                    String linea;
                    int NF = 0, NC = 0, NR = 0, NP = 0, tamanioPagina = 0, pagina;
                    while ((linea = reader.readLine()) != null) {
                        if (linea.startsWith("P=")) {
                            tamanioPagina = Integer.parseInt(linea.split("=")[1]);
                        } else if (linea.startsWith("NF=")) {
                            NF = Integer.parseInt(linea.split("=")[1]);
                        } else if (linea.startsWith("NC=")) {
                            NC = Integer.parseInt(linea.split("=")[1]);
                        } else if (linea.startsWith("NR=")) {
                            NR = Integer.parseInt(linea.split("=")[1]);
                        } else if (linea.startsWith("NP=")) {
                            NP = Integer.parseInt(linea.split("=")[1]);
                        } else if (linea.contains(",")) {
                            pagina = Integer.parseInt(linea.split(",")[1]);
                            referencias.add(pagina);
                        }
                    }
                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Simulacion simulacion = new Simulacion(numMarcos, referencias);
                simulacion.iniciar();
            } else if (opcion==3){
            	scanner.nextLine();
                System.out.println("Ingrese la ruta (path) de la imagen en la que desea esconder el mensaje");
             // Ejemplo de ruta: "Archivos/caso2-parrots.bmp"
                String input = scanner.nextLine();
                System.out.println("Ruta ingresada: " + input);
                Imagen imagen = new Imagen(input);
                System.out.println("Ingrese el mensaje que desea esconder:");
                String mensajeStr = scanner.nextLine();
                char[] mensaje = mensajeStr.toCharArray();
                imagen.esconder(mensaje, mensaje.length);
                System.out.println("Ingrese el nombre para guardar la imagen con el mensaje escondido:");
                String output = scanner.nextLine();
                imagen.escribirImagen(output);

                System.out.println("Mensaje escondido correctamente en la imagen.");

                
            } else if(opcion==4){
            	scanner.nextLine();
                System.out.println("Ingrese la ruta (path) de la imagen encriptada cuyo mensaje desea revelar");
                String input = scanner.nextLine();
                Imagen imagen = new Imagen(input);
                int longitud = imagen.leerLongitud();
                System.out.println("Longitud del mensaje escondido: " + longitud);
                char[] mensajeRecuperado = new char[longitud];

                imagen.recuperar(mensajeRecuperado, longitud);

           
                System.out.println("Mensaje recuperado: " + new String(mensajeRecuperado));


            }else if(opcion==5){
                System.out.println("Gracias por usar el programa");
                ejecutar=false;
            }
        }
        scanner.close();
    }
}
