package com.flavio;

public class Motor {

    private int[][] cuadricula = new int[9][9];

    public Motor() {
        inicializarCuadricula();
    }

    public int[][] getCuadricula() {
        return cuadricula;
    }

    public void inicializarCuadricula() {
        for (int i = 0; i < cuadricula.length; i++) {
            for (int j = 0; j < cuadricula[i].length; j++) {
                cuadricula[i][j] = 0;
            }
        }
    }

    // Solo permitimos números del 1 al 9 para jugar
    public boolean esNumeroPermitido(int numero) {
        return numero >= 1 && numero <= 9;
    }

    public boolean existeEnFila(int fila, int numero, int columnaActual) {
        for (int i = 0; i < 9; i++) {
            if (i != columnaActual && cuadricula[fila][i] == numero) {
                return true;
            }
        }
        return false;
    }

    public boolean existeEnColumna(int columna, int numero, int filaActual) {
        for (int i = 0; i < 9; i++) {
            if (i != filaActual && cuadricula[i][columna] == numero) {
                return true;
            }
        }
        return false;
    }

    public boolean existeEnBloque(int fila, int columna, int numero) {
        int filaInicial = (fila / 3) * 3;
        int columnaInicial = (columna / 3) * 3;

        for (int i = filaInicial; i < filaInicial + 3; i++) {
            for (int j = columnaInicial; j < columnaInicial + 3; j++) {
                if ((i != fila || j != columna) && cuadricula[i][j] == numero) {
                    return true;
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
        if (!posicionValida(fila, columna)) {
            return false;
        }

        return !existeEnFila(fila, numero, columna)
                && !existeEnColumna(columna, numero, fila)
                && !existeEnBloque(fila, columna, numero);
    }

    public boolean setNumero(int fila, int columna, int numero) {
        if (sePuedeColocar(fila, columna, numero)) {
            cuadricula[fila][columna] = numero;
            return true;
        }
        return false;
    }

    public void vaciarCelda(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            cuadricula[fila][columna] = 0;
        }

    }
    public int getValor(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            return -1;
        }
        return cuadricula[fila][columna];
    }
}