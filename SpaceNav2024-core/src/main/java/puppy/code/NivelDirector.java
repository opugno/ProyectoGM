package puppy.code;

/**
 * GM-9: Director del Builder
 * Conoce la estructura de cada nivel y las construye usando NivelBuilder
 * PantallaJuego solo llama a director.crearNivel(numero)
 */
public class NivelDirector 
{
	private final NivelBuilder builder;
	 
    public NivelDirector() 
    {
        builder = new NivelBuilder();
    }
    
    /**
     * Crea la configuracion del nivel indicado
     * Niveles 1-7 estan predefinidos. Los niveles 7 y 14 tienen un boss
     * Luego del 7 escala automaticamente.
     *
     * @param numero numero de nivel, se empieza en 1
     * @return NivelConfig listo para usar en PantallaJuego
     */
    public NivelConfig crearNivel(int numero) 
    {
        switch (numero) 
        {
            case 1:
                return builder.conBasicos(12).conVelocidad(1).build();
            case 2:
                return builder.conBasicos(15).conVelocidad(1).build();
            case 3:
                return builder.conBasicos(10).conMedios(4).conVelocidad(2).build();
            case 4:
                return builder.conBasicos(8).conMedios(6).conVelocidad(2).build();
            case 5:
                return builder.conBasicos(6).conMedios(6).conElite(2).conVelocidad(3).build();
            case 6:
                return builder.conBasicos(4).conMedios(8).conElite(4).conVelocidad(3).build();
            case 7:
                // BOSS NIVEL 7 con enemigos de medio y alto
                return builder.conMedios(2).conElite(2).conBoss(30).conVelocidad(3)
                              .conIntervaloDisparo(2.5f).build();
            default:
                return crearNivelEscalado(numero);
        }
    }
    
    /**
     * Para niveles 8 y mas: repite el ciclo de 7 pero escalando enemigos y velocidad.
     * Hay un boss cada 7 niveles 
     */
    private NivelConfig crearNivelEscalado(int numero) 
    {
        // ciclo interno 1-7
        int ciclo   = ((numero - 1) % 7) + 1;
        // cuantas veces se ha completado el ciclo (0 para nivel 1-7)
        int ronda   = (numero - 1) / 7;
        // mas enemigos y velocidad con cada ciclo
        int extraEnemigos  = ronda * 3;
        int extraVelocidad = ronda;
        int vidaBossExtra  = ronda * 10;
 
        switch (ciclo) 
        {
            case 1:
                return builder.conBasicos(12 + extraEnemigos)
                              .conVelocidad(1 + extraVelocidad).build();
            case 2:
                return builder.conBasicos(15 + extraEnemigos)
                              .conVelocidad(1 + extraVelocidad).build();
            case 3:
                return builder.conBasicos(10 + extraEnemigos).conMedios(4 + ronda)
                              .conVelocidad(2 + extraVelocidad).build();
            case 4:
                return builder.conBasicos(8 + extraEnemigos).conMedios(6 + ronda)
                              .conVelocidad(2 + extraVelocidad).build();
            case 5:
                return builder.conBasicos(6 + extraEnemigos).conMedios(6 + ronda)
                              .conElite(2 + ronda).conVelocidad(3 + extraVelocidad).build();
            case 6:
                return builder.conBasicos(4 + extraEnemigos).conMedios(8 + ronda)
                              .conElite(4 + ronda).conVelocidad(3 + extraVelocidad).build();
            case 7:
                // boss mas fuerte en cada ciclo
                return builder.conMedios(2 + ronda).conElite(2 + ronda)
                              .conBoss(30 + vidaBossExtra)
                              .conVelocidad(3 + extraVelocidad)
                              .conIntervaloDisparo(Math.max(1.0f, 2.5f - ronda * 0.3f))
                              .build();
            default:
                return builder.conBasicos(10).conVelocidad(1).build();
        }
    }
}
