package puppy.code;

import java.util.ArrayList;
import java.util.Random;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Pantalla principal del juego, modificada para usar:
 *   - GameManager (Singleton)  centralizado
 *   - NivelDirector + NivelBuilder (Builder) configuración de nivel
 *   - Enemigos polimorficos (Enemigo → EnemigoBasico / Medio / Alto / Boss)
 *   - Strategy de disparo en Nave4 y enemigos
 */
public class PantallaJuego implements Screen 
{
    //CORE
	private SpaceNavigation game;
	private OrthographicCamera camera;	
	private SpriteBatch batch;
	private GameManager gm;
	private NivelDirector nivelDirector;
	private NivelConfig nivelConfig;
	//private int score;
	//private int ronda;
	//private int velXAsteroides; 
	//private int velYAsteroides; 
	//private int cantAsteroides;
	
	//AUDIO
	private Sound explosionSound;
	private Music gameMusic;
	
	//OBJETOS DEL JUEGO
	private Nave4 nave;
	private ArrayList<Bullet>  balas = new ArrayList<>();
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private ArrayList<PowerUp> powerUps = new ArrayList<>();
    private Boss boss = null; // referencia especial al boss si existe


    //FORMACION, para mover a los enemigos juntos
    private float velFormX;          // velocidad lateral actual de la formación
    private float velFormY;          // velocidad de bajada al rebotar
    private boolean formBajando = false;
    private float bajadaRestante = 0f;
    private static final float BAJADA = 20f; // px que baja al rebotar
    
    //TEXTURAS
    private Texture txNavePrincipal, txBalaJugador, txBalaEnemigo;
    private Texture txEnemBasico, txEnemMedio, txEnemElite, txBoss;
    private Texture txPowerUpDisparo, txPowerUpEscudo, txPowerUpFuerte, txPowerUpEspecial, txPowerUpVida;
    private Texture[] txPowerUps;
    
    //HUD
    private ShapeRenderer shapeRenderer;
    private static final float ANCHO_PANTALLA = 800f;
    private static final float ALTO_PANTALLA = 640f;
 
    
    //CONSTRUCTOR
    public PantallaJuego(SpaceNavigation game) 
    {
        this.game = game;
        this.gm = GameManager.getInstance();
 
        batch = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, ANCHO_PANTALLA, ALTO_PANTALLA);
        shapeRenderer = new ShapeRenderer();
        nivelDirector = new NivelDirector();
 
        // Cargar audio
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);
        gameMusic.play();
 
        cargarTexturas();
 
        // Nave
        nave = new Nave4(
            (int)(ANCHO_PANTALLA / 2 - 22), 30,
            txNavePrincipal,
            Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")),
            txBalaJugador,
            Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"))
        );
     
        // Inicializar nivel
        iniciarNivel();
    }
    
    private void cargarTexturas() {
        txNavePrincipal  = new Texture(Gdx.files.internal("MainShip3.png"));
        txBalaJugador    = new Texture(Gdx.files.internal("Rocket2.png"));
        txBalaEnemigo    = new Texture(Gdx.files.internal("BulletEnemy.png"));
        txEnemBasico     = new Texture(Gdx.files.internal("enemigo_basico.png"));  // ← nuevo
        txEnemMedio      = new Texture(Gdx.files.internal("enemigo_medio.png"));   // ← nuevo
        txEnemElite      = new Texture(Gdx.files.internal("enemigo_elite.png"));   // ← nuevo
        txBoss           = new Texture(Gdx.files.internal("boss_nave.png"));            // ← nuevo
        txPowerUpDisparo  = new Texture(Gdx.files.internal("powerup_disparo.png"));     // ← nuevo
        txPowerUpEscudo   = new Texture(Gdx.files.internal("powerup_escudo.png"));      // ← nuevo
        txPowerUpFuerte   = new Texture(Gdx.files.internal("pu_fuerte.png"));
        txPowerUpEspecial = new Texture(Gdx.files.internal("pu_laser.png"));
        txPowerUpVida     = new Texture(Gdx.files.internal("pu_vida.png"));
        txPowerUps = new Texture[]{
            txPowerUpDisparo, txPowerUpEscudo,
            txPowerUpFuerte,  txPowerUpEspecial, txPowerUpVida
        };
    }
    
    /** Crea los enemigos segun la configuracion del nivel actual. */
    private void iniciarNivel()
    {
        enemigos.clear();
        powerUps.clear();
        balas.clear();
        boss = null;
 
        nivelConfig = nivelDirector.crearNivel(gm.getNivelActual());
        velFormX = nivelConfig.getVelocidadBase();
        velFormY = 0;
 
        colocarFormacion();
    }
    
    /** Coloca los enemigos en la grilla de formación. */
    private void colocarFormacion() 
    {
        int filaAltura = 55;
        int colAncho = 60;
        int margenIzq = 80;
        int filaBase = (int)(ALTO_PANTALLA - 120);
        int fila = 0;
 
        // Fila 1: basicos
        int basicos = nivelConfig.getCantBasicos();
        for (int i = 0; i < basicos; i++) 
        {
            float x = margenIzq + (i % 10) * colAncho;
            float y = filaBase  - (i / 10) * filaAltura;
            enemigos.add(new EnemigoBasico(x, y, txEnemBasico));
        }
        fila = basicos / 10 + 1;
 
        // Fila siguiente: medios
        for (int i = 0; i < nivelConfig.getCantMedios(); i++) 
        {
            float x = margenIzq + (i % 8) * (colAncho + 5);
            float y = filaBase  - fila * filaAltura - (i / 8) * filaAltura;
            EnemigoMedio em = new EnemigoMedio(x, y, txEnemMedio, txBalaEnemigo);
            enemigos.add(em);
        }
        fila += nivelConfig.getCantMedios() / 8 + 1;
 
        // Fila siguiente: alto
        for (int i = 0; i < nivelConfig.getCantAlto(); i++) 
        {
            float x = margenIzq + 50 + (i % 6) * (colAncho + 10);
            float y = filaBase  - fila * filaAltura - (i / 6) * filaAltura;
            EnemigoAlto ee = new EnemigoAlto(x, y, txEnemElite, txBalaEnemigo);
            enemigos.add(ee);
        }
 
        // Boss (solo si el nivel lo incluye)
        if (nivelConfig.isTieneBoss()) 
        {
            boss = new Boss(
                (int)(ANCHO_PANTALLA / 2 - 50),
                (int)(ALTO_PANTALLA  - 100),
                nivelConfig.getVidaBoss(),
                txBoss, txBalaEnemigo
            );
            enemigos.add(boss);
        }
    }
    
    
    //RENDER, bucle principal
    @Override
	public void render(float delta) 
    {
    	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	 
        gm.actualizarTimers(delta);
        moverFormacion();
 
        batch.begin();
 
        // 1. HUD
        dibujarHUD();
 
        // 2. Powerups
        actualizarPowerUps();
 
        // 3. Balas del jugador vs enemigos
        if (!nave.estaHerido()) 
        {
            procesarBalasJugador();
        }
 
        // 4. Balas enemigas vs nave
        procesarBalasEnemigos(delta);
 
        // 5. Actualizar y dibujar enemigos
        actualizarEnemigos(delta);
 
        // 6. Nave
        nave.draw(batch, this);
 
        batch.end();
 
        // 7. HUD de barras (ShapeRenderer necesita estar fuera del batch)
        dibujarBarras();
 
        // 8. ¿Boss vivo? dibujar su barra de vida
        if (boss != null && !boss.isDestroyed()) 
        {
            batch.begin();
            boss.dibujarBarraVida(batch);
            batch.end();
        }
 
        // 9. Verificar fin de nivel / game over
        verificarEstadoJuego();	 
	}
    
    private void moverFormacion() 
    {
        boolean debeRebotar = false;
 
        // Ver si algun basico o medio toca el borde
        for (Enemigo e : enemigos) 
        {
            if (e.isDestroyed() || e instanceof Boss) continue;
            float nx = e.getX() + velFormX;
            if (nx < 10 || nx + e.getWidth() > ANCHO_PANTALLA - 10) 
            {
                debeRebotar = true;
                break;
            }
        }
 
        if (debeRebotar) 
        {
            velFormX *= -1;
            // Bajar la formacion
            for (Enemigo e : enemigos) 
            {
                if (!e.isDestroyed() && !(e instanceof Boss)) 
                {
                    e.setVelFormacion(velFormX, -BAJADA);
                }
            }
        } 
        else 
        {
            for (Enemigo e : enemigos)
            {
                if (!e.isDestroyed() && !(e instanceof Boss))
                {
                    e.setVelFormacion(velFormX, 0);
                }
            }
        }
    }
    
    private void procesarBalasJugador() 
    {
        for (int i = balas.size() - 1; i >= 0; i--) 
        {
            Bullet b = balas.get(i);
            if (b.isEsEnemiga()) continue; // solo balas del jugador
            b.update();
 
            // Comprobar colision con cada enemigo
            for (int j = enemigos.size() - 1; j >= 0; j--) 
            {
                Enemigo e = enemigos.get(j);
                if (e.isDestroyed()) continue;
                if (b.getArea().overlaps(e.getArea())) 
                {
                    explosionSound.play();
                    boolean destruido = e.recibirDano(b.getDano());
                    b.setDestroyed(true);
                    if (destruido) 
                    {
                        gm.agregarPuntos(e.getPuntos());
                        // incrementar barra de poder segun tipo
                        float incremento = (e instanceof EnemigoAlto || e instanceof Boss) ? 0.20f
                                         : (e instanceof EnemigoMedio) ? 0.10f : 0.05f;
                        gm.incrementarBarra(incremento);
                        // Drop de poweup
                        PowerUp drop = e.generarDrop(txPowerUps);
                        if (drop != null) powerUps.add(drop);
                        e.setDestroyed(true);
                        enemigos.remove(j);
                    }
                    break;
                }
            }
            if (b.isDestroyed()) 
            { 
            	balas.remove(i); 
            }
        }
    }
    
    private void procesarBalasEnemigos(float delta) 
    {
        for (int i = balas.size() - 1; i >= 0; i--) 
        {
            Bullet b = balas.get(i);
            if (!b.isEsEnemiga()) continue;
            b.update();
            if (nave.checkCollisionBala(b))
            {
                explosionSound.play();
            }
            if (b.isDestroyed()) balas.remove(i);
        }
    }
    
    private void actualizarEnemigos(float delta) 
    {
        for (Enemigo e : enemigos)
        {
            if (e.isDestroyed()) continue;
            e.actualizar();   // Template Method
            //Usar la interfaz Disparable polimorficamente
            if (e instanceof Disparable)
            {
            	e.acumularTiempoDisparo(delta);
            	if (e.debeDisparar())
            	{
            		((Disparable) e).disparar(balas);
            		e.resetTiempoDisparo();
            	}
            }
            
            
            e.dibujar(batch);
            // Colisin directa nave-enemigo
            nave.checkCollisionEnemigo(e);
        }
    }
    
    private void actualizarPowerUps() 
    {
        for (int i = powerUps.size() - 1; i >= 0; i--) 
        {
            PowerUp pu = powerUps.get(i);
            pu.actualizar();
            pu.dibujar(batch);
            if (pu.colisionaConNave(nave)) 
            {
                pu.aplicar(nave);
            }
            if (pu.isDestroyed()) powerUps.remove(i);
        }
    }
    
    private void verificarEstadoJuego() 
    {
        // Game Over
        if (nave.estaDestruido()) 
        {
            Screen ss = new PantallaGameOver(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
            return;
        }
        // Nivel completado: no quedan enemigos
        boolean sinEnemigos = enemigos.stream().allMatch(GameObject::isDestroyed);
        if (sinEnemigos || enemigos.isEmpty()) 
        {
            gm.subirNivel();
            Screen ss = new PantallaJuego(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }
    
    //HUD
    private void dibujarHUD() 
    {
        game.getFont().getData().setScale(1.5f);
        game.getFont().draw(batch,
            "Vidas: " + gm.getVidas() + "  Nivel: " + gm.getNivelActual(),
            10, 25);
        game.getFont().draw(batch,
            "Score: " + gm.getScore(),
            ANCHO_PANTALLA - 200, 25);
        game.getFont().draw(batch,
            "HiScore: " + gm.getHighScore(),
            ANCHO_PANTALLA / 2 - 80, 25);
 
        // Indicadores de powerups activos
        float ix = 10;
        if (gm.isEscudoActivo()) 
        {
            game.getFont().draw(batch, "[ESCUDO]", ix, 48); ix += 120;
        }
        if (gm.isDisparoFuerteActivo()) 
        {
            game.getFont().draw(batch, "[FUERTE]", ix, 48); ix += 120;
        }
        if (gm.isDisparoEspecialActivo()) 
        {
            game.getFont().draw(batch, "[LASER]", ix, 48);
        }
        // Indicador barra de poder lista
        if (gm.isBarraPoderLista()) 
        {
            game.getFont().draw(batch, ">> PRESIONA E <<", ANCHO_PANTALLA / 2 - 90, 48);
        }
    }
    
    private void dibujarBarras() 
    {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
 
        // Barra de poder (abajo a la izquierda)
        float bw = 120f, bh = 10f, bx = 10f, by = ALTO_PANTALLA - 15f;
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(bx, by, bw, bh);
        shapeRenderer.setColor(gm.isBarraPoderLista() ? Color.CYAN : Color.BLUE);
        shapeRenderer.rect(bx, by, bw * gm.getBarraPoder(), bh);
 
        shapeRenderer.end();
    }
    
    //API publica para Nave4
    public ArrayList<Bullet> getListaBalas() { return balas; }
    
    /** Compatibilidad con el codigo original */
    public boolean agregarBala(Bullet bb) { return balas.add(bb); }
    
    
    //SCREEN LIFECYCLE
    @Override
    public void show() { gameMusic.play(); }
 
    @Override
    public void resize(int width, int height) { }
 
    @Override
    public void pause() { gameMusic.pause(); }
 
    @Override
    public void resume() { gameMusic.play(); }
 
    @Override
    public void hide() { }
 
    @Override
    public void dispose() 
    {
        explosionSound.dispose();
        gameMusic.dispose();
        shapeRenderer.dispose();
        if (boss != null) boss.dispose();
    }
}
