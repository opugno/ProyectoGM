package puppy.code;

/**
 * GM-9: Builder de niveles con una interfaz fluida
 * Construye un NivelConfig paso a paso con metodos encadenados.
 *
 * Uso:
 *   NivelConfig cfg = new NivelBuilder()
 *                          .conBasicos(10)
 *                          .conMedios(4)
 *                          .conVelocidad(2)
 *                          .build();
 */
public class NivelBuilder 
{
	private NivelConfig config;
	 
    public NivelBuilder() 
    {
        config = new NivelConfig(); // inicia con valores por defecto
    }
    
    /** Agrega enemigos bssicos (10pts, sin disparo). */
    public NivelBuilder conBasicos(int cantidad) 
    {
        config.setCantBasicos(cantidad);
        return this;
    }
    
    /** Agrega enemigos medios (30pts, disparo simple). */
    public NivelBuilder conMedios(int cantidad) 
    {
        config.setCantMedios(cantidad);
        return this;
    }
    
    /** Agrega enemigos alto (100pts, disparo triple, sinusoidal). */
    public NivelBuilder conElite(int cantidad) 
    {
        config.setCantAlto(cantidad);
        return this;
    }
    
    /** Activa el boss con una cantidad de vida especifica. */
    public NivelBuilder conBoss(int vidaBoss) 
    {
        config.setTieneBoss(true);
        config.setVidaBoss(vidaBoss);
        return this;
    }
    
    /** Velocidad base de la formación (pixeles por frame). */
    public NivelBuilder conVelocidad(int velocidad) 
    {
        config.setVelocidadBase(velocidad);
        return this;
    }
 
    /** Intervalo base de disparo de los enemigos en segundos. */
    public NivelBuilder conIntervaloDisparo(float segundos) 
    {
        config.setIntervaloDisparo(segundos);
        return this;
    }
    
    /** Se construye y luego retorna el NivelConfig final. */
    public NivelConfig build() 
    {
        NivelConfig resultado = config;
        config = new NivelConfig(); // resetea el builder para reutilizar
        return resultado;
    }
}
