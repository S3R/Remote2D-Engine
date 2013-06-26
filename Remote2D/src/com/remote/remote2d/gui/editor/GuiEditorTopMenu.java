package com.remote.remote2d.gui.editor;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.Remote2D;
import com.remote.remote2d.entity.Entity;
import com.remote.remote2d.gui.Gui;
import com.remote.remote2d.gui.GuiInGame;
import com.remote.remote2d.io.R2DFileManager;
import com.remote.remote2d.logic.Vector2D;
import com.remote.remote2d.world.Map;

public class GuiEditorTopMenu extends Gui {
	
	public ArrayList<GuiEditorTopMenuSection> sections;
	public GuiEditor editor;
	
	private int height = 20;
	
	public GuiEditorTopMenu(GuiEditor editor)
	{		
		this.editor = editor;
		
		sections = new ArrayList<GuiEditorTopMenuSection>();
				
		int currentX = 0;
		
		String[] fileContents = {"New Map","Open Map","Save Map Files","Save Global Files"};
		GuiEditorTopMenuSection file = new GuiEditorTopMenuSection(currentX, 0, height, fileContents, "File", this);
		if(file.getEnabled())
			currentX += file.width;
		
		String[] editContents = {"Create Animated Sprite", "Optimize Spritesheet"};
		GuiEditorTopMenuSection edit = new GuiEditorTopMenuSection(currentX, 0, height, editContents, "Edit", this);
		if(edit.getEnabled())
			currentX += edit.width;
		
		String[] worldContents = {"Insert Entity", "Run Map"};
		GuiEditorTopMenuSection world = new GuiEditorTopMenuSection(currentX, 0, height, worldContents, "World", this);
		if(world.getEnabled())
			currentX += world.width;
		
		String[] windowContents = {"Toggle Fullscreen","Exit"};
		GuiEditorTopMenuSection window = new GuiEditorTopMenuSection(currentX, 0, height, windowContents, "Window", this);
		if(window.getEnabled())
			currentX += window.width;
		
		String[] devContents = {"Reinitialize Editor", "View Art Asset", "Fancypants Collider Test","Normal Collider Test", "1D Perlin Noise", "2D Perlin Noise"};
		GuiEditorTopMenuSection dev = new GuiEditorTopMenuSection(currentX, 0, height, devContents, "Developer", this);
		if(dev.getEnabled())
			currentX += dev.width;
		
		sections.add(file);
		sections.add(world);
		sections.add(edit);
		sections.add(window);
		sections.add(dev);
	}
	
	public void initSections()
	{
		int currentX = 0;
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).getEnabled())
			{
				sections.get(x).x = currentX;
				currentX += sections.get(x).width;
			}
		}
		
	}
	
	public GuiEditorTopMenuSection getSectionWithName(String s)
	{
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).title.equals(s))
				return sections.get(x);
		}
		return null;
	}
	
	public boolean isMenuHovered(int i, int j)
	{
		boolean z = false;
		for(int x=0;x<sections.size();x++)
		{
			if(sections.get(x).isSelected)
				z = true;
		}
		return (i>0 && j>0 && i<Remote2D.getInstance().displayHandler.width && j<height) || z;
	}

	@Override
	public void render() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 0.2f, 0.2f, 1);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(Remote2D.getInstance().displayHandler.width, 0);
			GL11.glVertex2f(Remote2D.getInstance().displayHandler.width,height);
			GL11.glVertex2f(0,height);
		GL11.glEnd();
		GL11.glColor4f(1, 1, 1, 1);
		
		for(int x=0;x<sections.size();x++)
		{
			sections.get(x).render();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public void tick(int i, int j, int k, double delta) {
		getSectionWithName("World").setEnabled(editor.getMap() != null);
		
		if(isMenuHovered(i,j))
			editor.disableElementPlace();
		
		for(int x=0;x<sections.size();x++)
		{
			sections.get(x).tick(i,j,k,delta);
		}
		
		String secTitle = "NONE";
		String secSubTitle = "NONE";
		
		for(int x=0;x<sections.size();x++)
		{
			int selectedSubSec = sections.get(x).popSelectedBox();
			if(selectedSubSec != -1)
			{
				secTitle = sections.get(x).title;
				secSubTitle = sections.get(x).values[selectedSubSec];
				Log.info("Selected box: " + secTitle + ">" + secSubTitle+" ");
			}
		}
		
		if(secTitle.equalsIgnoreCase("File"))
		{
			if(secSubTitle.equalsIgnoreCase("New Map"))
			{
				Log.info("Opening new Document!");
				editor.setMap(new Map());
			} else if(secSubTitle.equalsIgnoreCase("Save Local Files"))
			{
				Log.info("Saving!");
			} else if(secSubTitle.equalsIgnoreCase("Open Map"))
			{
				Log.info("Opening!");
				Map newMap = new Map();
				R2DFileManager mapManager = new R2DFileManager("/res/maps/map.r2d", "Test Map", newMap);
				editor.setMap(newMap);
			} else if(secSubTitle.equalsIgnoreCase("Save Map Files"))
			{
				Map map = editor.getMap();
				R2DFileManager mapManager = new R2DFileManager("/res/maps/map.r2d", "Test Map", map,false);
				mapManager.write();
				editor.setMap(map);
			}
		} else if(secTitle.equalsIgnoreCase("Edit"))
		{
			if(secSubTitle.equalsIgnoreCase("Create Animated Sprite"))
			{
				Remote2D.getInstance().guiList.push(new GuiCreateSpriteSheet());
			} else if(secSubTitle.equalsIgnoreCase("Optimize Spritesheet"))
			{
				Remote2D.getInstance().guiList.push(new GuiOptimizeSpriteSheet());
			}
		} else if(secTitle.equalsIgnoreCase("Window"))
		{
			if(secSubTitle.equalsIgnoreCase("Toggle Fullscreen"))
			{
				Remote2D.getInstance().displayHandler.setDisplayMode(Display.getDesktopDisplayMode().getWidth(),
						Display.getDesktopDisplayMode().getHeight(), !Display.isFullscreen(), 
						false);
			}else if(secSubTitle.equalsIgnoreCase("Exit"))
			{
				Remote2D.getInstance().guiList.pop();
			}
		} else if(secTitle.equalsIgnoreCase("Developer"))
		{
			if(secSubTitle.equals("Reinitialize Editor"))
			{
				Remote2D.getInstance().guiList.pop();
				Remote2D.getInstance().guiList.push(new GuiEditor());
			} else if(secSubTitle.equals("View Art Asset"))
			{
				editor.attemptToPutWindowOnTop(new GuiWindowViewArtAsset(editor, new Vector2D(200,200), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("Fancypants Collider Test"))
			{
				editor.attemptToPutWindowOnTop(new GuiWindowCollisionTest(editor, new Vector2D(300,300), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("Normal Collider Test"))
			{
				editor.attemptToPutWindowOnTop(new GuiWindowGeneralColliderTest(editor, new Vector2D(300,300), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("1D Perlin Noise"))
			{
				editor.attemptToPutWindowOnTop(new GuiWindowPerlin1D(editor, new Vector2D(10,30), editor.getWindowBounds()));
			} else if(secSubTitle.equalsIgnoreCase("2D Perlin Noise"))
			{
				editor.attemptToPutWindowOnTop(new GuiWindowPerlin2D(editor, new Vector2D(20,30), editor.getWindowBounds()));
			}
		} else if(secTitle.equalsIgnoreCase("World"))
		{
			if(secSubTitle.equalsIgnoreCase("Insert Entity"))
			{
				editor.setActiveEntity(new Entity());
			} else if(secSubTitle.equalsIgnoreCase("Run Map"))
			{
				Remote2D.getInstance().map = editor.getMap().copy();
				Remote2D.getInstance().guiList.push(new GuiInGame());
			}
		}
	}
	
}
