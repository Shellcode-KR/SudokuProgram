package com.flavio;

public class TableroSudoku {

    private final int[][] cuadricula = new int[9][9];
    private final boolean[][] celdasFijas = new boolean[9][9];

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

    public boolean esCeldaFija(int fila, int columna) {
        if (!ValidadorSudoku.posicionValida(fila, columna)) {
            return false;
        }
        return celdasFijas[fila][columna];
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
    }

    public boolean cargarTableroInicial(int[][] valores) {
        if (valores == null || valores.length != 9) {
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

    public boolean tableroInvalido() {
        return !ValidadorSudoku.tableroConsistente(cuadricula);
    }
}
