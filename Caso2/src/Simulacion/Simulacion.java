package Simulacion;

import java.util.*;

public class Simulacion {
    private static int NUM_MARCOS;
    private static final int TIEMPO_ACTUALIZACION = 1;
    private static final int TIEMPO_BIT_R = 2;
    private LinkedHashMap<Integer, Boolean> RAM;
    private int hits = 0;
    private int fallos = 0;
    private List<Integer> tablaPaginas;
    private boolean ejecutar = true;

    public Simulacion(int numMarcos, List<Integer> tablaPaginas) {
        NUM_MARCOS = numMarcos;
        this.RAM = new LinkedHashMap<>(NUM_MARCOS, 0.75f, true);
        this.tablaPaginas = tablaPaginas;
    }
    public int getRemplazada(LinkedHashMap<Integer, Boolean> RAM){
        for (Map.Entry<Integer, Boolean> entry : RAM.entrySet()) {
            if (!entry.getValue()) {
                return entry.getKey();
            }
        }
        return 0;
    }

    public synchronized void cargarPagina(int paginaVirtual) {
        if (RAM.containsKey(paginaVirtual)) {
            hits++;
            RAM.put(paginaVirtual, true);
        } else {
            fallos++;
            if (RAM.size() >= NUM_MARCOS) {
                int paginaAReemplazar = getRemplazada(RAM);
                RAM.remove(paginaAReemplazar);
                RAM.put(paginaVirtual, true);
            } else {
                RAM.put(paginaVirtual, true);
            }
        }
    }

    public void iniciar() {
        PaginaThread paginaThread = this.new PaginaThread(tablaPaginas);
        BitRThread bitRThread = this.new BitRThread();
        paginaThread.start();
        bitRThread.start();
        try {
            paginaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ejecutar = false;
        this.mostrarEstado();
    }

    public void mostrarEstado() {
        System.out.println("RAM: " + RAM);
        System.out.println("Hits: " + hits + ", Fallos: " + fallos);
        System.out.println("Porcentaje de Hits sobre Misses: " + (((float) hits / (float) tablaPaginas.size()) * 100)+"%");
    }

    public class PaginaThread extends Thread {
        private List<Integer> tablaPaginas;

        public PaginaThread(List<Integer> tablaPaginas) {
            this.tablaPaginas = tablaPaginas;
        }

        @Override
        public void run() {
            double total = (double) tablaPaginas.size();
            double progreso = 0.0;
            int porcentajeMostrado = 0;
            for (int referencia : tablaPaginas) {
                    cargarPagina(referencia);
                progreso++;
                int porcentaje = (int) Math.round(((progreso) / total) * 100);
                if (porcentaje % 5 == 0 && porcentaje != porcentajeMostrado) {
                    porcentajeMostrado = porcentaje;
                    System.out.println("Progreso en: " + (porcentajeMostrado) + "%");
                }

                try {
                    Thread.sleep(TIEMPO_ACTUALIZACION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ejecutar = false;
        }
    }

    public class BitRThread extends Thread {
        @Override
        public void run() {
            while (ejecutar) {
                synchronized (Simulacion.this) {
                    for (Map.Entry<Integer, Boolean> entry : RAM.entrySet()) {
                        entry.setValue(false);
                    }
                }
                try {
                    Thread.sleep(TIEMPO_BIT_R);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
