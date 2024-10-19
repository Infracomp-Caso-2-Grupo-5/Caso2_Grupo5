package Simulacion;

import java.util.*;

public class Simulacion {
    private static int NUM_MARCOS;
    private static final int TIEMPO_ACTUALIZACION = 1;
    private static final int TIEMPO_BIT_R = 2;

    private LinkedHashMap<Integer, Boolean> pageTable;
    private int[] RAM;
    private int hits = 0;
    private int fallos = 0;
    private List<Integer> referencias;
    private boolean ejecutar = true;

    public Simulacion(int numMarcos, List<Integer> referencias) {
        NUM_MARCOS = numMarcos;
        this.pageTable = new LinkedHashMap<>(NUM_MARCOS, 0.75f, true);
        this.RAM = new int[NUM_MARCOS];
        Arrays.fill(RAM, -1);
        this.referencias = referencias;
    }

    public synchronized void cargarPagina(int paginaVirtual) {
        if (pageTable.containsKey(paginaVirtual)) {
            hits++;
            pageTable.put(paginaVirtual, true);
        } else {
            fallos++;
            if (pageTable.size() >= NUM_MARCOS) {
                int paginaAReemplazar = pageTable.entrySet().iterator().next().getKey();
                int marco = getPosicionEnRam(paginaAReemplazar);
                pageTable.remove(paginaAReemplazar);
                RAM[marco] = paginaVirtual;
                pageTable.put(paginaVirtual, true);
            } else {
                int marcoLibre = pageTable.size();
                RAM[marcoLibre] = paginaVirtual;
                pageTable.put(paginaVirtual, true);
            }
        }
    }

    public void iniciar() {
        PaginaThread paginaThread = this.new PaginaThread(referencias);
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

    private synchronized int getPosicionEnRam(int paginaVirtual) {
        for (int i = 0; i < RAM.length; i++) {
            if (RAM[i] == paginaVirtual) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void mostrarEstado() {
        System.out.println("RAM: " + Arrays.toString(RAM));
        System.out.println("Tabla de páginas: " + pageTable);
        System.out.println("Hits: " + hits + ", Fallos: " + fallos);
        System.out.println("Porcentaje de Hits sobre Misses: " + (((float) hits / (float) referencias.size()) * 100)+"%");
    }

    public class PaginaThread extends Thread {
        private List<Integer> referencias;

        public PaginaThread(List<Integer> referencias) {
            this.referencias = referencias;
        }

        @Override
        public void run() {
            double total = (double) referencias.size();
            double progreso = 0.0;
            int porcentajeMostrado = 0;
            for (int referencia : referencias) {
                synchronized (Simulacion.this) {
                    cargarPagina(referencia);
                }
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
                    for (Map.Entry<Integer, Boolean> entry : pageTable.entrySet()) {
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
