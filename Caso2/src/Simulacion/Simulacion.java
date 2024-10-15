package Simulacion;
import java.util.*;
import java.io.*;

public class Simulacion {
    private static int NUM_MARCOS;
    private static final int TIEMPO_ACTUALIZACION = 1; 
    private static final int TIEMPO_BIT_R = 2;

    private LinkedHashMap<Integer, Boolean> pageTable;
    private int[] RAM;
    private int hits = 0;
    private int fallos = 0;
    private List<Integer> referencias;

    public Simulacion(int numMarcos, List<Integer> referencias) {
        NUM_MARCOS = numMarcos;
        this.pageTable = new LinkedHashMap<>(NUM_MARCOS, 0.75f, true);
        this.RAM = new int[NUM_MARCOS];
        Arrays.fill(RAM, -1);
        this.referencias= referencias;
    }

    public synchronized void cargarPagina(int paginaVirtual) {
        if (pageTable.containsKey(paginaVirtual)) {
            hits++;
            pageTable.put(paginaVirtual, true);
            System.out.println("Hit: Página " + paginaVirtual + " ya está en RAM.");
        } else {
            fallos++;
            if (pageTable.size() >= NUM_MARCOS) {
                int paginaAReemplazar = pageTable.entrySet().iterator().next().getKey();
                int marco = getPosicionEnRam(paginaAReemplazar);
                pageTable.remove(paginaAReemplazar);
                RAM[marco] = paginaVirtual;
                pageTable.put(paginaVirtual, true);
                System.out.println("Reemplazando página " + paginaAReemplazar + " por página " + paginaVirtual);
            } else {
                int marcoLibre = pageTable.size();
                RAM[marcoLibre] = paginaVirtual;
                pageTable.put(paginaVirtual, true);
            }
            System.out.println("Fallo: Página " + paginaVirtual + " cargada en RAM.");
        }
        notifyAll();
    }

    public void iniciar(){
        PaginaThread paginaThread = this.new PaginaThread(referencias);
        BitRThread bitRThread = this.new BitRThread();
        paginaThread.start();
        bitRThread.start();
        try {
            paginaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Mostrar el estado final
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
    }

    public class PaginaThread extends Thread {
        private List<Integer> referencias;

        public PaginaThread(List<Integer> referencias) {
            this.referencias = referencias;
        }
        @Override
        public void run() {
            for (int referencia : referencias) {
                synchronized (Simulacion.this) {
                    cargarPagina(referencia);
                    try {
                        Thread.sleep(TIEMPO_ACTUALIZACION);
                    } catch (InterruptedException e) {
                       e.printStackTrace();
                    }
                }
            }
        }
    }

    public class BitRThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (Simulacion.this) {
                    for (Map.Entry<Integer, Boolean> entry : pageTable.entrySet()) {
                        entry.setValue(false);
                    }
                    System.out.println("Bit R actualizado para todas las páginas.");
                    notifyAll();
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
