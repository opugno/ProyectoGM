package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;


/**
 * Pantalla de menu principal. Muestra controles y highscore.
 */

public class PantallaMenu implements Screen 
{

	private SpaceNavigation game;
	private OrthographicCamera camera;

	public PantallaMenu(SpaceNavigation game) 
	{
		this.game = game;
        
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1200, 800);
	}

	@Override
	public void render(float delta) 
	{
		ScreenUtils.clear(0, 0, 0.15f, 1);

		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);

		game.getBatch().begin();
		game.getFont().getData().setScale(3f);
		game.getFont().draw(game.getBatch(), "SPACE BLITZ", 200, 560, 800, 1, true);
	
		game.getFont().getData().setScale(1.8f);
		game.getFont().draw(game.getBatch(),
	            "High Score: " + GameManager.getInstance().getHighScore(),
	            350, 480, 500, 1, true);
		game.getFont().getData().setScale(1.5f);
        game.getFont().draw(game.getBatch(), "── CONTROLES ──", 400, 410, 400, 1, true);
        game.getFont().draw(game.getBatch(),
            "Flechas — mover nave      SPACE — disparar", 150, 370);
        game.getFont().draw(game.getBatch(),
            "E — activar poder (barra llena)", 300, 335);
        game.getFont().getData().setScale(1.2f);
        game.getFont().draw(game.getBatch(), "── POWER-UPS ──", 400, 290, 400, 1, true);
        game.getFont().draw(game.getBatch(),
            "Upgrade  — +1 bala    Escudo — absorbe 1 golpe", 130, 260);
        game.getFont().draw(game.getBatch(),
            "Fuerte — x2 daño 10s    Láser — drop del Boss", 130, 230);
 
        game.getFont().getData().setScale(1.8f);
        game.getFont().draw(game.getBatch(),
            ">> Pulsa cualquier tecla para comenzar <<", 90, 150);
		
		game.getBatch().end();

		if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) 
		{
            GameManager.getInstance().resetGame();
            Screen ss = new PantallaJuego(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
	}
	
	
	@Override
	public void show() {
	}

	@Override
	public void resize(int w, int h) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
   
}