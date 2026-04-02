package com.flavio;

public class Vista {
    public void imprimirCuadricula(int[][] cuadricula){
        for (int i = 0; i < cuadricula.length ; i++) {
            for (int j = 0; j < cuadricula[i].length ; j++) {
                int dato;
                String caracterAuxiliar;
                String separadorAuxiliar;

                dato = cuadricula[i][j];

                if( dato==0 ){
                    caracterAuxiliar=" ";
                }else{
                    caracterAuxiliar= String.valueOf(dato);
                }
                if( (j+1)%3==0 ){
                    separadorAuxiliar = "|";
                }
                else{
                    separadorAuxiliar="";
                }
                System.out.print(" ["+caracterAuxiliar+"] "+separadorAuxiliar);

            }
            System.out.println("");
            if( (i+1)%3 ==0 ){
                System.out.println("--------------------------------------------------------------");
            }


        }
    }
}
