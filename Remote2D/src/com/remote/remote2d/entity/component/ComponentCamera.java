package com.remote.remote2d.entity.component;

import com.remote.remote2d.Remote2D;
import com.remote.remote2d.art.Fonts;
import com.remote.remote2d.art.Renderer;
import com.remote.remote2d.entity.Entity;
import com.remote.remote2d.logic.Vector2;
import com.remote.remote2d.world.Camera;

public class ComponentCamera extends Component {
	
	public boolean useMultiples = true;
	public boolean blackBars = true;
	public Vector2 targetResolution = new Vector2(720,480);

	public ComponentCamera(Entity e) {
		super(e);
	}

	@Override
	public void tick(int i, int j, int k) {
		
	}

	@Override
	public void renderBefore(boolean editor, float interpolation) {
		
		if(editor)
		{
			Renderer.drawRect(entity.pos, new Vector2(Fonts.get("Arial").getStringDim("CAMERA", 20)[0]+10,20), 1, 1, 0, 1);
			Fonts.get("Arial").drawString("CAMERA", entity.pos.x+5, entity.pos.y, 20, 0x000000);
		}
		
	}

	@Override
	public void onEntitySpawn() {
		Camera camera = new Camera(entity.pos,targetResolution);
		camera.blackBars = blackBars;
		camera.useMultiples = useMultiples;
		Remote2D.getInstance().map.camera = camera;
	}

	@Override
	public void renderAfter(boolean editor, float interpolation) {
		if(editor)
		{
			Renderer.drawLineRect(entity.pos, targetResolution, 0, 0, 1, 1);
			
		}
	}

	@Override
	public Component clone() {
		ComponentCamera newColl = new ComponentCamera(entity);
		newColl.blackBars = this.blackBars;
		newColl.useMultiples = this.useMultiples;
		if(targetResolution != null)
			newColl.targetResolution = this.targetResolution.copy();
		return newColl;
	}

	@Override
	public void apply() {
		
		
		
	}

}