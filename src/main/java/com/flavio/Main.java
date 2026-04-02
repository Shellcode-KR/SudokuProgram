package com.flavio;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");
        Motor motor = new Motor();
        Vista vista = new Vista();

        vista.imprimirCuadricula(motor.getCuadricula());
    }
}