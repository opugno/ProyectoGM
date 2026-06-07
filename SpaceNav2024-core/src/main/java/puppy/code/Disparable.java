package puppy.code;

import java.util.ArrayList;

/**
 * Interfaz donde se marca que un objeto puede disparar
 * Esta la implementan Nave4, EnemigoElite, Boss
 */
public class Disparable 
{
	/**
     * Ejecuta el disparo usando la estrategia actual.
     * las balas generadas se agregan a la lista recibida
     * @param listaBajas lista donde se agregan las nuevas balas
     */
    void disparar(ArrayList<Bullet> listaBalas);
 
    /**
     * Cambia la estrategia de disparo en tiempo de ejecucion
     * @param estrategia la nueva estrategia a usar
     */
    void setEstrategiaDisparo(EstrategiaDisparo estrategia);
}
