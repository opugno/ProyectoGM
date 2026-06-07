package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Clase principal del juego. Usa GameManager (Singleton) para el highScore.
 */
public class SpaceNavigation extends Game 
{
	//private String nombreJuego = "Space Navigation";
	private SpriteBatch batch;
	private BitmapFont font;
	//private int highScore;	

	@Override
	public void create() 
	{
		//highScore = 0;
		batch = new SpriteBatch();
		font = new BitmapFont(); 
		font.getData().setScale(2f);
		
		//GameManager inicia automaticamente su estado al primer getInstance()
        GameManager.getInstance().resetGame();
        
		Screen ss = new PantallaMenu(this);
		this.setScreen(ss);
	}

	@Override
	public void render() 
	{
		super.render(); // important!
	}

	@Override
	public void dispose() 
	{
		batch.dispose();
		font.dispose();
	}

	public SpriteBatch getBatch() 
	{
		return batch;
	}

	public BitmapFont getFont() 
	{
		return font;
	}

	/**Esto lo hace el GameManager*/
	public int getHighScore() 
	{
		return GameManager.getInstance().getHighScore(); 
	}

	public void setHighScore(int hs) 
	{
		GameManager.getInstance().setHighScore(hs);
	}
	
	

}