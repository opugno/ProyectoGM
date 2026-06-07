package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Pantalla de Game Over. Usa GameManager para mostrar score final y highscore.
 */
public class PantallaGameOver implements Screen 
{

	private SpaceNavigation game;
	private OrthographicCamera camera;
	private GameManager gm;

	public PantallaGameOver(SpaceNavigation game) 
	{
		this.game = game;
        this.gm = GameManager.getInstance();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1200, 800);
	}

	@Override
	public void render(float delta)
	{
		ScreenUtils.clear(0, 0, 0.1f, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
 
        game.getFont().getData().setScale(3f);
        game.getFont().draw(game.getBatch(), "GAME OVER", 400, 480, 400, 1, true);
 
        game.getFont().getData().setScale(2f);
        game.getFont().draw(game.getBatch(),
            "Puntuación final: " + gm.getScore(), 350, 390, 500, 1, true);
        game.getFont().draw(game.getBatch(),
            "High Score: " + gm.getHighScore(), 350, 340, 500, 1, true);
        game.getFont().draw(game.getBatch(),
            "Nivel alcanzado: " + gm.getNivelActual(), 350, 290, 500, 1, true);
 
        game.getFont().getData().setScale(1.5f);
        game.getFont().draw(game.getBatch(),
            "Pulsa cualquier tecla para reiniciar", 200, 200);
 
        game.getBatch().end();
 
        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) 
        {
            // Reset completo del estado antes de reiniciar
            gm.resetGame();
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