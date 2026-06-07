package puppy.code;

/**
 * GM-9: Producto del patróon Builder
 * Contiene toda la configuración de un nivel de juego
 */
public class NivelConfig 
{
	//ATRIBUTOS
	private int cantBasicos;
    private int cantMedios;
    private int cantAlto;
    private boolean tieneBoss;
    private int vidaBoss;
    private int velocidadBase;
    private float intervaloDisparoEnemigos; // segundos entre disparos base
    
    //Constructor con valores inicializados
    public NivelConfig() {
        cantBasicos = 0;
        cantMedios = 0;
        cantAlto = 0;
        tieneBoss = false;
        vidaBoss = 30;
        velocidadBase = 1;
        intervaloDisparoEnemigos = 3.0f;
    }

    //Getters
    public int getCantBasicos() { return cantBasicos; }
    public int getCantMedios() { return cantMedios; }
    public int getCantAlto() { return cantAlto; }
    public boolean isTieneBoss() { return tieneBoss; }
    public int getVidaBoss() { return vidaBoss; }
    public int getVelocidadBase() { return velocidadBase; }
    public float getIntervaloDisparo() { return intervaloDisparoEnemigos; }
 
    //Setters (solo NivelBuilder los llama)
    void setCantBasicos(int n) { this.cantBasicos = n; }
    void setCantMedios(int n) { this.cantMedios  = n; }
    void setCantAlto(int n) { this.cantAlto   = n; }
    void setTieneBoss(boolean b) { this.tieneBoss   = b; }
    void setVidaBoss(int v) { this.vidaBoss    = v; }
    void setVelocidadBase(int v) { this.velocidadBase = v; }
    void setIntervaloDisparo(float t) { this.intervaloDisparoEnemigos = t; }
    
    @Override
    public String toString() 
    {
        return String.format(
            "NivelConfig[basicos=%d, medios=%d, elite=%d, boss=%b(%d HP), vel=%d]",
            cantBasicos, cantMedios, cantAlto, tieneBoss, vidaBoss, velocidadBase
        );
    }
}
