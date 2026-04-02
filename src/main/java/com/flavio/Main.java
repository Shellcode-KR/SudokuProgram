package com.flavio;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        TableroSudoku tablero = new TableroSudoku();
        Vista vista = new Vista();

        vista.imprimirCuadricula(tablero.getCuadricula());
    }
}