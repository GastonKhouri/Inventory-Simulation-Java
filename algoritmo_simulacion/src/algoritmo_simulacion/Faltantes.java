/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmo_simulacion;

/**
 *
 * @author Gastón
 */
public class Faltantes {

    private int faltantesT;
    private int dias;

    public Faltantes(int faltantesT, int dias) {
        this.faltantesT = faltantesT;
        this.dias = dias;
    }
    
    /*   GETS   */
    
    public int getFaltantesT() {
        return faltantesT;
    }

    public int getDias() {
        return dias;
    }
    
    /*   SETS   */

    public void setFaltantesT(int faltantesT) {
        this.faltantesT = faltantesT;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }
   
}
