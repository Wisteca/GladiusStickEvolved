package ch.wisteca.anarchy.personnages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import ch.wisteca.anarchy.utils.Position;
import ch.wisteca.anarchy.world.World;

public class LocalPlayer extends Personnage implements InputProcessor {
	
	public LocalPlayer(World world)
	{
		super(world, new Position(10, 1000, 100, 100));
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	protected void death()
	{
		getPosition().setPosX(10).setPosY(1000);
		setHealth(100);
	}
	
	@Override
	protected void onTouchGround()
	{
		super.onTouchGround();
		checkKeys();
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		switch(keycode)
		{
			case Keys.LEFT :
				resetSpeedX();
				move(false);
				break;
				
			case Keys.RIGHT :
				resetSpeedX();
				move(true);
				break;
				
			case Keys.SPACE :
				jump();
				checkKeys();
				break;
				
			case Keys.CONTROL_LEFT :
				attack();
				
			default:
				break;
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if(keycode == Keys.LEFT || keycode == Keys.RIGHT)
		{
			resetSpeedX();
			checkKeys();
		}
		return false;
	}
	
	private void checkKeys()
	{
		if(Gdx.input.isKeyPressed(Keys.LEFT))
			move(false);
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
			move(true);
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
