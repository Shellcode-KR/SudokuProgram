package com.flavio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableroSudoku {

    private final int[][] cuadricula = new int[9][9];

    public static class PasoResolucion {
        private final boolean resuelto;
        private final int fila;
        private final int columna;
        private final int numero;
        private final String metodo;
        private final String explicacion;

        public PasoResolucion(boolean resuelto, int fila, int columna, int numero, String metodo, String explicacion) {
            this.resuelto = resuelto;
            this.fila = fila;
            this.columna = columna;
            this.numero = numero;
            this.metodo = metodo;
            this.explicacion = explicacion;
        }

        public boolean isResuelto() {
            return resuelto;
        }

        public int getFila() {
            return fila;
        }

        public int getColumna() {
            return columna;
        }

        public int getNumero() {
            return numero;
        }

        public String getMetodo() {
            return metodo;
        }

        public String getExplicacion() {
            return explicacion;
        }

        @Override
        public String toString() {
            if (!resuelto) {
                return "No se encontró una jugada resoluble con los métodos actuales.";
            }
            return "Fila " + (fila + 1)
                    + ", columna " + (columna + 1)
                    + " = " + numero
                    + " (método: " + metodo + ")\n"
                    + explicacion;
        }
    }

    public TableroSudoku() {
        inicializarCuadricula();
    }

    public int[][] getCuadricula() {
        int[][] copia = new int[9][9];
        for (int fila = 0; fila < 9; fila++) {
            System.arraycopy(cuadricula[fila], 0, copia[fila], 0, 9);
        }
        return copia;
    }

    public int getValor(int fila, int columna) {
        if (!ValidadorSudoku.posicionValida(fila, columna)) {
            return -1;
        }
        return cuadricula[fila][columna];
    }

    public void cargarTablero(int[][] valores) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                cuadricula[fila][columna] = valores[fila][columna];
            }
        }
    }

    // Solo permitimos números del 1 al 9 para jugar
    public boolean esNumeroPermitido(int numero) {
        return numero >= 1 && numero <= 9;
    }

    public boolean setNumero(int fila, int columna, int numero) {
        if (!ValidadorSudoku.posicionValida(fila, columna) || esCeldaFija(fila, columna)) {
            return false;
        }
        if (numero == 0) {
            cuadricula[fila][columna] = 0;
            return true;
        }
        if (!ValidadorSudoku.puedeColocar(cuadricula, fila, columna, numero)) {
            return false;
        }
        cuadricula[fila][columna] = numero;
        return true;
    }

    public boolean vaciarCelda(int fila, int columna) {
        return setNumero(fila, columna, 0);
    }

    public void limpiarTablero() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                cuadricula[fila][columna] = 0;
                celdasFijas[fila][columna] = false;
            }
        }
    }

    public void limpiarNoFijas() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (!celdasFijas[fila][columna]) {
                    cuadricula[fila][columna] = 0;
                }
            }
        }
        return false;
    }

    public boolean posicionValida(int fila, int columna) {
        return fila >= 0 && fila < 9 && columna >= 0 && columna < 9;
    }

    public boolean sePuedeColocar(int fila, int columna, int numero) {
        if (!esNumeroPermitido(numero)) {
            return false;
        }
        for (int fila = 0; fila < 9; fila++) {
            if (valores[fila] == null || valores[fila].length != 9) {
                return false;
            }
        }
        if (!ValidadorSudoku.tableroConsistente(valores)) {
            return false;
        }

        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                int valor = valores[fila][columna];
                cuadricula[fila][columna] = valor;
                celdasFijas[fila][columna] = valor != 0;
            }
        }
        return true;
    }

    public boolean tableroCompleto() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (cuadricula[fila][columna] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getValor(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            return -1;
        }
        return cuadricula[fila][columna];
    }

    public List<Integer> numerosPosiblesEnCelda(int fila, int columna) {
        if (!posicionValida(fila, columna) || cuadricula[fila][columna] != 0) {
            return Collections.emptyList();
        }

        List<Integer> posibles = new ArrayList<>();
        for (int numero = 1; numero <= 9; numero++) {
            if (sePuedeColocar(fila, columna, numero)) {
                posibles.add(numero);
            }
        }
        return posibles;
    }

    public List<Integer> numerosPosiblesEnFila(int fila) {
        if (fila < 0 || fila >= 9) {
            return Collections.emptyList();
        }
        return numerosFaltantesEnFila(fila);
    }

    public List<Integer> numerosPosiblesEnColumna(int columna) {
        if (columna < 0 || columna >= 9) {
            return Collections.emptyList();
        }
        return numerosFaltantesEnColumna(columna);
    }

    public List<Integer> numerosPosiblesEnBloque(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            return Collections.emptyList();
        }
        return numerosFaltantesEnBloque(fila, columna);
    }

    public PasoResolucion resolverSiguienteNumero() {
        PasoResolucion paso = resolverPorUnicoCandidatoEnCelda();
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverPorUnicoCandidatoEnFila();
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverPorUnicoCandidatoEnColumna();
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverPorUnicoCandidatoEnBloque();
        if (paso.isResuelto()) {
            return paso;
        }

        return new PasoResolucion(false, -1, -1, -1,
                "Sin resolución",
                "No se encontró una jugada válida con único candidato o único lugar.");
    }

    private PasoResolucion resolverPorUnicoCandidatoEnCelda() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (cuadricula[fila][columna] != 0) {
                    continue;
                }

                List<Integer> posibles = numerosPosiblesEnCelda(fila, columna);
                if (posibles.size() == 1) {
                    int numero = posibles.get(0);
                    setNumero(fila, columna, numero);
                    return new PasoResolucion(true, fila, columna, numero,
                            "Único candidato en celda",
                            "La celda solo acepta el número " + numero + ".");
                }
            }
        }
        return new PasoResolucion(false, -1, -1, -1, "", "");
    }

    private PasoResolucion resolverPorUnicoCandidatoEnFila() {
        for (int fila = 0; fila < 9; fila++) {
            List<Integer> faltantes = numerosFaltantesEnFila(fila);
            for (Integer numero : faltantes) {
                int apariciones = 0;
                int columnaElegida = -1;

                for (int columna = 0; columna < 9; columna++) {
                    if (cuadricula[fila][columna] == 0 && sePuedeColocar(fila, columna, numero)) {
                        apariciones++;
                        columnaElegida = columna;
                    }
                }

                if (apariciones == 1) {
                    setNumero(fila, columnaElegida, numero);
                    return new PasoResolucion(true, fila, columnaElegida, numero,
                            "Único lugar en fila",
                            "En la fila " + (fila + 1) + " el número " + numero
                                    + " solo podía ir en la columna " + (columnaElegida + 1) + ".");
                }
            }
        }
        return new PasoResolucion(false, -1, -1, -1, "", "");
    }

    private PasoResolucion resolverPorUnicoCandidatoEnColumna() {
        for (int columna = 0; columna < 9; columna++) {
            List<Integer> faltantes = numerosFaltantesEnColumna(columna);
            for (Integer numero : faltantes) {
                int apariciones = 0;
                int filaElegida = -1;

                for (int fila = 0; fila < 9; fila++) {
                    if (cuadricula[fila][columna] == 0 && sePuedeColocar(fila, columna, numero)) {
                        apariciones++;
                        filaElegida = fila;
                    }
                }

                if (apariciones == 1) {
                    setNumero(filaElegida, columna, numero);
                    return new PasoResolucion(true, filaElegida, columna, numero,
                            "Único lugar en columna",
                            "En la columna " + (columna + 1) + " el número " + numero
                                    + " solo podía ir en la fila " + (filaElegida + 1) + ".");
                }
            }
        }
        return new PasoResolucion(false, -1, -1, -1, "", "");
    }

    private PasoResolucion resolverPorUnicoCandidatoEnBloque() {
        for (int filaInicial = 0; filaInicial < 9; filaInicial += 3) {
            for (int columnaInicial = 0; columnaInicial < 9; columnaInicial += 3) {
                List<Integer> faltantes = numerosFaltantesEnBloque(filaInicial, columnaInicial);
                for (Integer numero : faltantes) {
                    int apariciones = 0;
                    int filaElegida = -1;
                    int columnaElegida = -1;

                    for (int fila = filaInicial; fila < filaInicial + 3; fila++) {
                        for (int columna = columnaInicial; columna < columnaInicial + 3; columna++) {
                            if (cuadricula[fila][columna] == 0 && sePuedeColocar(fila, columna, numero)) {
                                apariciones++;
                                filaElegida = fila;
                                columnaElegida = columna;
                            }
                        }
                    }

                    if (apariciones == 1) {
                        setNumero(filaElegida, columnaElegida, numero);
                        return new PasoResolucion(true, filaElegida, columnaElegida, numero,
                                "Único lugar en bloque 3x3",
                                "En el bloque 3x3 que inicia en fila " + (filaInicial + 1)
                                        + " y columna " + (columnaInicial + 1)
                                        + ", el número " + numero + " solo podía ir en esa celda.");
                    }
                }
            }
        }
        return new PasoResolucion(false, -1, -1, -1, "", "");
    }

    private List<Integer> numerosFaltantesEnFila(int fila) {
        boolean[] presentes = new boolean[10];
        for (int columna = 0; columna < 9; columna++) {
            int valor = cuadricula[fila][columna];
            if (valor >= 1 && valor <= 9) {
                presentes[valor] = true;
            }
        }
        return extraerNoPresentes(presentes);
    }

    private List<Integer> numerosFaltantesEnColumna(int columna) {
        boolean[] presentes = new boolean[10];
        for (int fila = 0; fila < 9; fila++) {
            int valor = cuadricula[fila][columna];
            if (valor >= 1 && valor <= 9) {
                presentes[valor] = true;
            }
        }
        return extraerNoPresentes(presentes);
    }

    private List<Integer> numerosFaltantesEnBloque(int fila, int columna) {
        int filaInicial = (fila / 3) * 3;
        int columnaInicial = (columna / 3) * 3;

        boolean[] presentes = new boolean[10];
        for (int i = filaInicial; i < filaInicial + 3; i++) {
            for (int j = columnaInicial; j < columnaInicial + 3; j++) {
                int valor = cuadricula[i][j];
                if (valor >= 1 && valor <= 9) {
                    presentes[valor] = true;
                }
            }
        }
        return extraerNoPresentes(presentes);
    }

    private List<Integer> extraerNoPresentes(boolean[] presentes) {
        List<Integer> faltantes = new ArrayList<>();
        for (int numero = 1; numero <= 9; numero++) {
            if (!presentes[numero]) {
                faltantes.add(numero);
            }
        }
        return faltantes;
    }
}
