package com.flavio;

public final class ValidadorSudoku {

    private ValidadorSudoku() {
    }

    public static boolean posicionValida(int fila, int columna) {
        return fila >= 0 && fila < 9 && columna >= 0 && columna < 9;
    }

    public static boolean esNumeroPermitido(int numero) {
        return numero >= 1 && numero <= 9;
    }

    public static boolean existeEnFila(int[][] tablero, int fila, int numero, int columnaActual) {
        for (int columna = 0; columna < 9; columna++) {
            if (columna != columnaActual && tablero[fila][columna] == numero) {
                return true;
            }
        }
        return false;
    }

    public static boolean existeEnColumna(int[][] tablero, int columna, int numero, int filaActual) {
        for (int fila = 0; fila < 9; fila++) {
            if (fila != filaActual && tablero[fila][columna] == numero) {
                return true;
            }
        }
        return false;
    }

    public static boolean existeEnBloque(int[][] tablero, int fila, int columna, int numero) {
        int filaInicial = (fila / 3) * 3;
        int columnaInicial = (columna / 3) * 3;
        for (int i = filaInicial; i < filaInicial + 3; i++) {
            for (int j = columnaInicial; j < columnaInicial + 3; j++) {
                if ((i != fila || j != columna) && tablero[i][j] == numero) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean puedeColocar(int[][] tablero, int fila, int columna, int numero) {
        if (!posicionValida(fila, columna) || !esNumeroPermitido(numero)) {
            return false;
        }
        return !existeEnFila(tablero, fila, numero, columna)
                && !existeEnColumna(tablero, columna, numero, fila)
                && !existeEnBloque(tablero, fila, columna, numero);
    }

    public static boolean tableroConsistente(int[][] tablero) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                int valor = tablero[fila][columna];
                if (valor == 0) {
                    continue;
                }
                if (!esNumeroPermitido(valor)) {
                    return false;
                }
                tablero[fila][columna] = 0;
                boolean valido = puedeColocar(tablero, fila, columna, valor);
                tablero[fila][columna] = valor;
                if (!valido) {
                    return false;
                }
            }
        }
        return true;
    }
}
