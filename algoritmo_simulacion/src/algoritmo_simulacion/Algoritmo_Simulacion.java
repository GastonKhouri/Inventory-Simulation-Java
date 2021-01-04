/*
* Algoritmo de simulacion de inventarios
*
* Version 2.0
*
* 07/2020
*
* Copyright
*
* Gaston Khouri
*/

package algoritmo_simulacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Algoritmo_Simulacion {

    static List<Faltantes> faltantes = new ArrayList<>();
    
    /*   Variables para guardar los datos 
         introducidos por el usuario        */
    
    static int unidadTiempo = 360;
    static float costoInventario = 52;
    static float costoOrden = 100;
    static float costoCompra;
    static float costoFaltanteConEspera = 20;
    static float costoFaltanteSinEspera = 50;
    static int invInicial = 50;
    static int diasSimular = 6;
    static int Q = 100;
    static int R = 75;
    
    static int DE[] = {25,26,27,28,29,30,31,32,33,34};
    static float ProbDE [] = {2, 4, 6, 12, 20, 24, 15, 10, 5, 2};
    
    static int TENT[] = {1, 2, 3, 4};
    static float ProbTENT[] = {20, 30, 25, 25};
    
    static int TESP[] = {0, 1, 2, 3, 4};
    static float ProbTESP[] = {40, 20, 15, 15, 10};
    
    /*   Los datos que contengan estas variables 
         son las que se van a imprimir             */
    
    static int diaActual;
    static int demandaAct = 0;
    static int invFinalAct = 0;
    static float invPromAct = 0;
    static int faltanteAct = 0;
    static int ordenAct = 0;
    static int entregaAct = 0;
    static int esperaAct = 0;
    
    /*   Estas variables van a guardar el numero 
         aleatorio generado                        */
    
    static float naDemanda = 0;
    static float naEspera = 0;
    static float naEntrega = 0;
    
    static int diaEntrega = 0;
    static float sumProm = 0;
    static int sumConEspera = 0;
    static int sumSinEspera = 0;
    static int sumFaltantes = 0;
    
    static int banOrden = 1;
    static int contOrdenes = 0;
    
    /*   Variables para calcular Q y R   */
    
    static int QMin = 0, QMax = 0;
    static int RMin = 0, RMax = 0;
    static int QDef = 0, RDef = 0;
    
    
    public static void main(String[] args) {
        
        for (diaActual = 1; diaActual <= diasSimular; diaActual++) {
        
            //diaEntrega = diaActual + entregaAct + 1;
            
            if(diaActual != 1){
                invInicial = invFinalAct; 
            }
            
            /*   ALGORITMO LLEGADA DE ENTREGA   */
            
            if (diaEntrega == diaActual){
                invInicial += Q;
                banOrden = 1;
                diaEntrega = 0;
            }
            
            /*   ALGORITMO DEMANDA DADO UN NUMERO ALEATORIO   */
            
            calcularDemanda(numAleatorio());    
            
            /*   ALGORITMO INVENTARIO INICIAL   */
            
            int x = faltantes.size();
            
            for (int i = 0; i < x; i++){
                faltantes.get(i).setDias(faltantes.get(i).getDias()-1);
                if(faltantes.get(i).getDias() < 0){
                    sumSinEspera += faltantes.get(i).getFaltantesT();
                    faltantes.remove(i);
                    i -= 1;
                    x--;
                }
            }
            
            if (diaActual != 1 && invInicial != 0) {

                for (Faltantes faltante : faltantes) {
                    if (invInicial - faltante.getFaltantesT() >= 0) {
                        invInicial -= faltante.getFaltantesT();
                        sumConEspera += faltante.getFaltantesT();
                        faltante.setFaltantesT(0);
                        faltante.setDias(0);
                    } else {
                        sumConEspera = faltante.getFaltantesT() - invInicial;
                        faltante.setFaltantesT(faltante.getFaltantesT() - invInicial);
                        sumSinEspera = invInicial;
                        invInicial = 0;
                        break;
                    }
                }
            }
            
            /*   ALGORITMO TIEMPO ESPERA   */
            
            calcularEspera(numAleatorio());
            
            /*   ALGORITMO FALTANTE   */
            
            if (demandaAct - invInicial <= 0) {
                faltanteAct = 0;
                naEspera = -99;
                esperaAct = -99;
            } else {
                faltanteAct = demandaAct - invInicial;
                faltantes.add(new Faltantes(faltanteAct, esperaAct));
            }

            /*   ALGORITMO INVENTARIO FINAL   */
            
            if (invInicial - demandaAct < 0) {
                invFinalAct = 0;
            } else {
                invFinalAct = invInicial - demandaAct;
            }
            
            /*   ALGORITMO ORDENES   */
            
            if (invFinalAct <= R && banOrden == 1) {
            
                contOrdenes++;
                banOrden = 0;
                ordenAct = contOrdenes;
                diaEntrega = 0;

                calcularEntrega(numAleatorio());
                diaEntrega = diaActual + entregaAct + 1;
                
            } else {
                ordenAct = -99;
                naEntrega = -99;
                entregaAct = -99;
            }
            
            /*   ALGORITMO INVENTARIO PROMEDIO   */
            
            invPromAct = ((float) invInicial + (float) invFinalAct)/2;
            sumProm += invPromAct; 
            
            /*   Imprimir   */

            if (diaActual == 1){
                System.out.println("DIA\tINI\tNAD\tDEM\tFIN\tPROM\tFAL\tORD\tNAENT\tENT\tNAESP\tESP");
                System.out.println("---------------------------------------------------------------------------------------------");
            }
            System.out.println(diaActual + "\t" 
                    + invInicial + "\t" 
                    + naDemanda + "\t" 
                    + demandaAct + "\t" 
                    + invFinalAct + "\t" 
                    + invPromAct + "\t" 
                    + faltanteAct + "\t" 
                    + ordenAct + "\t" 
                    + naEntrega + "\t" 
                    + entregaAct + "\t" 
                    + naEspera + "\t" 
                    + esperaAct);
            System.out.println("---------------------------------------------------------------------------------------------");
        }
        
//        if (esperaAct == 0){
//            sumSinEspera += faltanteAct;
//        }
        
        /*   Se eliminan los que ya se les acabo el tiempo de espera y se agregan a los sin espera   */
        int x = faltantes.size();
        for (int i = 0; i < x; i++) {
            faltantes.get(i).setDias(faltantes.get(i).getDias() - 1);
            if (faltantes.get(i).getDias() < 0) {
                sumSinEspera += faltantes.get(i).getFaltantesT();
                faltantes.remove(i);
                i -= 1;
                x--;
            }
        }
        
        System.out.println();
        System.out.println("Suma de con espera: "+sumConEspera);
        System.out.println("Suma de sin espera: "+sumSinEspera);
        System.out.println("Suma de prom inventarios: "+sumProm);
        
        faltantes.clear();
        
        System.out.println("Costo por unidad: "+costoPorUnidad(unidadTiempo));
        System.out.println("Costo por faltantes: "+costoFaltante(sumConEspera, sumSinEspera));
        System.out.println("Costo por ordenes: "+costoOrden(contOrdenes));
        System.out.println("Costo por inventario: "+costoInv(unidadTiempo, sumProm));
        System.out.println("Costo total: "+costoTotal(unidadTiempo, contOrdenes, sumProm, sumConEspera, sumSinEspera));
        
    }
    
 
    /*   Metodo para calcular un numero aleatorio   */
    
    static int numAleatorio(){
        Random r = new Random();
        int numAleatorio = r.nextInt(1000);
        return (numAleatorio);
    }
  
    /*   Metodo para calcular una demanda dado un numero aleatorio   */
    
    static void calcularDemanda (int numeroAleatorio){
        
        float sum = 0;

        for (int i = 0; i < DE.length; i++) {
            sum += ProbDE[i]* 10;
            if (i == 0) {
                if (numeroAleatorio >= 0 && numeroAleatorio < sum) {
                    demandaAct = DE[i];
                    naDemanda = (float)numeroAleatorio/10;
                    return;
                }
            } else {
                if (numeroAleatorio >= sum - (ProbDE[i] *10) && numeroAleatorio < sum) {
                    demandaAct = DE[i];
                    naDemanda =(float) numeroAleatorio/10;
                    return;
                }
            }
        }
        
    } 
    
    /*   Metodo para calcular una entrega dado un numero aleatorio   */
    
    static void calcularEntrega (int numeroAleatorio){
        
        float sum = 0;

        for (int i = 0; i < TENT.length; i++) {
            sum += ProbTENT[i] * 10;
            if (i == 0) {
                if (numeroAleatorio >= 0 && numeroAleatorio < sum) {
                    entregaAct = TENT[i];
                    naEntrega = (float)numeroAleatorio/10;
                    return;
                }
            } else {
                if (numeroAleatorio >= sum - (ProbTENT[i]*10) && numeroAleatorio < sum) {
                    entregaAct = TENT[i];
                    naEntrega = (float)numeroAleatorio/10;
                    return;
                }
            }
        }   
    }
    
    /*   Metodo para calcular una espera dado un numero aleatorio   */
    
    static void calcularEspera(int numeroAleatorio){
        
        float sum = 0;

        for (int i = 0; i < TESP.length; i++) {
            sum += ProbTESP[i] * 10;
            if (i == 0) {
                if (numeroAleatorio >= 0 && numeroAleatorio < sum) {
                    esperaAct = TESP[i]*10;
                    naEspera = (float)numeroAleatorio/10;
                    return;
                }
            } else {
                if (numeroAleatorio >= sum - (ProbTESP[i]*10) && numeroAleatorio < sum) {
                    esperaAct = TESP[i];
                    naEspera = (float)numeroAleatorio/10;
                    return;
                }
            }
        }
    }
    
    /*   Metodo para calcular costo por unidad  */
    
    static float costoPorUnidad(int unidadT){
        float CPU = costoInventario / (float) unidadT;
        return(CPU);
    }
    
    /*   Metodo para calcular costo por faltantes  */
    
    static float costoFaltante(float faltantesConEspera, float faltantesSinEspera){
        
        float CFT;        
        
        CFT = (float) ((faltantesSinEspera * costoFaltanteSinEspera) + (faltantesConEspera * costoFaltanteConEspera));
        
        return(CFT);
    }
    
    /*   Metodo para calcular costo de orden  */
    
    static float costoOrden(int ordenesTotales){
        float costoO = (float) ordenesTotales * costoOrden;
        return(costoO);
    }
    
    /*   Metodo para calcular costo de inventario  */
    
    static float costoInv(int unidadT, float sumProm){
        float costoInv = sumProm * costoPorUnidad(unidadT);
        return(costoInv);
    }
    
    /*   Metodo para calcular costo de total  */
    
    static float costoTotal(int unidadT, int ordenesTotales, float sumProm, int faltantesConEspera, int faltantesSinEspera){
        float costoTotal = costoFaltante(faltantesConEspera, faltantesSinEspera) + costoOrden(ordenesTotales) + costoInv(unidadT, sumProm);
        return(costoTotal);
    }
  
}
