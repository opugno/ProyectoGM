package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Pantalla de menú — muestra los controles actualizados (rotación libre).
 */
public class PantallaMenu implements Screen {

    private SpaceNavigation game;
    private OrthographicCamera camera;

    public PantallaMenu(SpaceNavigation game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.12f, 1);
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();

        game.getFont().getData().setScale(3f);
        game.getFont().draw(game.getBatch(), "SPACE NAVIGATION", 200, 570, 800, 1, true);

        game.getFont().getData().setScale(1.8f);
        game.getFont().draw(game.getBatch(),
            "High Score: " + GameManager.getInstance().getHighScore(),
            350, 490, 500, 1, true);

        game.getFont().getData().setScale(1.5f);
        game.getFont().draw(game.getBatch(), "── CONTROLES ──", 400, 425, 400, 1, true);

        game.getFont().getData().setScale(1.3f);
        game.getFont().draw(game.getBatch(), "LEFT / RIGHT  ─  rotar nave",          220, 390);
        game.getFont().draw(game.getBatch(), "UP            ─  empuje (adelante)",    220, 360);
        game.getFont().draw(game.getBatch(), "DOWN          ─  frenar",               220, 330);
        game.getFont().draw(game.getBatch(), "SPACE         ─  disparar",             220, 300);
        game.getFont().draw(game.getBatch(), "E             ─  poder (barra llena)",  220, 270);

        game.getFont().getData().setScale(1.3f);
        game.getFont().draw(game.getBatch(), "── POWER-UPS ──", 400, 235, 400, 1, true);
        game.getFont().draw(game.getBatch(),
            "Upgrade (+bala)  |  Escudo  |  Fuerte x2  |  Laser  |  Vida extra",
            100, 205);

        game.getFont().getData().setScale(1.7f);
        game.getFont().draw(game.getBatch(),
            ">> Pulsa cualquier tecla para comenzar <<", 120, 155);

        game.getBatch().end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            GameManager.getInstance().resetGame();
            Screen ss = new PantallaJuego(game);
            ss.resize(1200, 800);
            game.setScreen(ss);
            dispose();
        }
    }

    @Override public void show()   { }
    @Override public void resize(int w, int h) { }
    @Override public void pause()  { }
    @Override public void resume() { }
    @Override public void hide()   { }
    @Override public void dispose(){ }
}