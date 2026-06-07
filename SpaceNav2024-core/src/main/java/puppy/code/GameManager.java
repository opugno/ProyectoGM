package puppy.code;

/**
 * GM-6: Patrón Singleton
 * Unica instancia que centraliza todo el estado del juego
 * ES accesible desde cualquier clase que posea GameManager.getInstance()
 */
public class GameManager 
{
	// SINGLETON, instancia única, constructor privado
	private static GameManager instance;
	 
    private GameManager() 
    {
        resetGame();
    }
 
    public static GameManager getInstance() 
    {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    
    // ESTADO DEL JUEGO
    //atributos
    private int score;
    private int highScore;
    private int nivelActual;
    private int vidas;
    private float barraPoder;    // 0.0 a 1.0
    private int nivelDisparo;    // 1 a 6
    private boolean escudoActivo;
    private boolean disparoFuerteActivo;
    private float tiempoDisparoFuerte;   // segundos restantes
    private boolean disparoEspecialActivo;
    private float tiempoDisparoEspecial; // segundos restantes
    private boolean barraPoderLista;     // true si llego al 100%
    
    //CONTROL DEL JUEGO
    
    /** Reinicia todo al estado inicial, nueva partida. */
    public void resetGame() 
    {
        score          = 0;
        nivelActual    = 1;
        vidas          = 3;
        barraPoder     = 0f;
        nivelDisparo   = 1;
        escudoActivo   = false;
        disparoFuerteActivo   = false;
        tiempoDisparoFuerte   = 0f;
        disparoEspecialActivo = false;
        tiempoDisparoEspecial = 0f;
        barraPoderLista = false;
    }
    
    /**Avanza al siguiente nivel conservabdo sus vidas, el score y el nivel de disparo*/
    public void subirNivel()
    {
    	nivelActual++;
    	barraPoder = 0f;
    	barraPoderLista = false;
    }
    
    //PUNTUACION
    public void agregarPuntos(int puntos)
    {
    	score += puntos;
    	if (score > highScore) highScore = score;
    }
    
    //VIDAS
    /** Quita una vida. si hay un escudo este se termina */
    public boolean recibirDano() 
    {
        if (escudoActivo) 
        {
            escudoActivo = false;
            return false; // el escudo obtiene el dano y no se quita la vida
        }
        vidas--;
        
        // al morir se baja 1 nivel el disparo
        if (nivelDisparo > 1) 
        {
        	nivelDisparo--;
        }
        return vidas <= 0;
    }
 
    public void agregarVida() 
    {
        vidas++;
    }
    
    //BARRA DE PODER
    
    /** Incrementa la barra de poder, esta llega a 1.0 y queda llena */
    public void incrementarBarra(float cantidad) 
    {
        if (barraPoderLista) return;
        barraPoder = Math.min(1.0f, barraPoder + cantidad);
        if (barraPoder >= 1.0f) barraPoderLista = true;
    }
 
    /** Activa la ráfaga si la barra está llena. */
    public boolean activarBarraPoder() 
    {
        if (!barraPoderLista) return false;
        barraPoder      = 0f;
        barraPoderLista = false;
        return true;
    }
    
    //UPGRADES/POWER-UPS
    
    //Upgrade, subir el nivel del disparo
    public void subirNivelDisparo()
    {
    	if (nivelDisparo < 6) nivelDisparo++;
    }
    
    //Powerup de activar escudo
    public void activarEscudo()
    {
    	escudoActivo = true;
    }
    
    //PowerUp un disparo mas fuerte
    public void activarDisparoFuerto(float duracion)
    {
    	disparoFuerteActivo = true;
    	tiempoDisparoFuerte = duracion;
    }
    
    //PowerUp un disparo especial
    public void activarDisparoEspecial(float duracion)
    {
    	disparoEspecialActivo = true;
    	tiempoDisparoEspecial = duracion;
    }
    
    /** Actualizar temporizadores de Powerups. */
    public void actualizarTimers(float delta) 
    {
        if (disparoFuerteActivo) 
        {
            tiempoDisparoFuerte -= delta;
            if (tiempoDisparoFuerte <= 0) disparoFuerteActivo = false;
        }
        if (disparoEspecialActivo) 
        {
            tiempoDisparoEspecial -= delta;
            if (tiempoDisparoEspecial <= 0) disparoEspecialActivo = false;
        }
    }
    
    //GETTERS
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public int getNivelActual() { return nivelActual; }
    public int getVidas() { return vidas; }
    public float getBarraPoder() { return barraPoder; }
    public int getNivelDisparo() { return nivelDisparo; }
    public boolean isEscudoActivo() { return escudoActivo; }
    public boolean isDisparoFuerteActivo() { return disparoFuerteActivo; }
    public boolean isDisparoEspecialActivo() { return disparoEspecialActivo; }
    public boolean isBarraPoderLista() { return barraPoderLista; }
 
    // Setter manual de highScore para persistencia entre las partidas
    public void setHighScore(int hs) 
    {
        if (hs > highScore) highScore = hs;
    }
}
