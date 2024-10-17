import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Referencias {
    public static void generarReferencias(String rutaImagen, int P, String filePath) {
        
        Imagen imagen = new Imagen(rutaImagen);

        int longitud = imagen.leerLongitud();

        char[] vector = new char[longitud];

        imagen.recuperar(vector, longitud);

        int NF = imagen.alto;
        int NC = imagen.ancho;
        int NR = (longitud * 17) + 16;

        double div = (double) longitud / P;
        int res = (int) Math.ceil(div);

        int NP = (imagen.alto * 3 * imagen.ancho) / P + res;

        byte[][][] copyImagen = imagen.imagen.clone();

        int longitudMensaje = imagen.leerLongitud();

        char[] colores = { 'R', 'G', 'B' };

        int desplazamiento = 0;

        int desplazamientoMensaje = 0;

        int pagMensaje;
        int posCaracter;
        int bytesTotales = 0;
        int bitsCaracter = 0;

        String txtReferencias = "";
        pagMensaje= NP-res;
        
        for (int i = 0; i < copyImagen.length; i++) { // Fila i
            for (int j = 0; j < copyImagen[i].length; j++) { // Columna j
                for (int k = 0; k < copyImagen[i][j].length; k++) { // k = R, G, B

                    if (bytesTotales < (longitudMensaje * 8) + 16) {

                        if (bytesTotales >= 15 && bytesTotales%8 == 0 && bitsCaracter%8 == 0){
                            posCaracter = bitsCaracter/8;
                            int nextDespl = ((imagen.alto * 3 + imagen.ancho) + (bitsCaracter/8))%P;
                            int nextPg = pagMensaje;
                            if (nextDespl == 0 && bytesTotales != 16) {
                                nextPg++;
                            }
                            txtReferencias += "Mensaje[" + posCaracter + "]," + nextPg + "," + nextDespl + " ,W\n";

                        }

                        int pagina =bytesTotales/P;
                        desplazamiento = bytesTotales % P;
                        char comp = colores[k % 3];
                        txtReferencias += "Imagen[" + i + "][" + j + "][" + k + "]." + comp + "," + pagina + "," + desplazamiento + " ,R\n";

                        if (bytesTotales >= 15) {
                            posCaracter = bitsCaracter/8;

                            if (bytesTotales != 15) {
                                desplazamientoMensaje = ((imagen.alto * 3 * imagen.ancho) + (bitsCaracter/8)) % P;
                                pagMensaje = ((imagen.alto * 3 * imagen.ancho) + (bitsCaracter/8)) / P;
                                txtReferencias += "Mensaje[" + posCaracter + "]," + pagMensaje + "," + desplazamientoMensaje + " ,W\n";
                                bitsCaracter ++;

                            }
                        }
                        bytesTotales++;  
                    }
                }
            }
        }
        // Ensure the directory exists
        java.nio.file.Path directoryPath = java.nio.file.Paths.get("Referencias");
        if (!java.nio.file.Files.exists(directoryPath)) {
            try {
                java.nio.file.Files.createDirectories(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        filePath = "Referencias/" + filePath;

        try (FileWriter fileWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("P=" + P + "\nLongitud=" + longitud + "\n");
            bufferedWriter.write("NC=" + NC + "\n");
            bufferedWriter.write("NF=" + NF + "\n");
            bufferedWriter.write("NR=" + NR + "\n");
            bufferedWriter.write("NP=" + NP + "\n");
            bufferedWriter.write(txtReferencias);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
