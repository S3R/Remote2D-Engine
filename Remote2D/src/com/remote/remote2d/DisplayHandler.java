package com.remote.remote2d;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.esotericsoftware.minlog.Log;
import com.remote.remote2d.art.TextureLoader;
import com.remote.remote2d.logic.Vector2;

public class DisplayHandler {
	
	public int width;
	public int height;
	public boolean fullscreen;
	public boolean borderless;
	
	public DisplayHandler(int width, int height, boolean fullscreen, boolean borderless)
	{
		this.width = borderless ? Display.getDesktopDisplayMode().getWidth() : width;
		this.height = borderless ? Display.getDesktopDisplayMode().getHeight() : height;
		this.fullscreen = fullscreen;
		this.borderless = borderless;
		
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			Display.setTitle("Remote2D");
			Display.setResizable(Remote2D.RESIZING_ENABLED);
			Display.setFullscreen(fullscreen && !borderless);
			
			setIcons(Remote2D.getInstance().getGame().getIconPath());
			
			Display.create();
		} catch (LWJGLException e) {
			throw new Remote2DException(e,"Failed to create LWJGL Display");
		}
		
		initGL();
	}
	
	public void setIcons(String[] icons)
	{
		if(icons == null)
			return;
		ByteBuffer[] buffers = new ByteBuffer[icons.length];
		for(int x=0;x<icons.length;x++)
			buffers[x] = getBufferFromImage(TextureLoader.loadImage(icons[x]),4);
		
		Display.setIcon(buffers);
	}
	
	public Vector2 getDimensions()
	{
		return new Vector2(width,height);
	}
	
	public ByteBuffer getBufferFromImage(BufferedImage image, int BYTES_PER_PIXEL)
	{
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        
        return buffer;
	}
	
	public void checkDisplayResolution()
	{
		if(Display.getWidth() != width || Display.getHeight() != height)
		{
			width = Display.getWidth();
			height = Display.getHeight();
			initGL();
			
			for(int x=0;x<Remote2D.getInstance().guiList.size();x++)
				Remote2D.getInstance().guiList.get(x).initGui();
		}
	}
	
	public void initGL()
	{
		Log.info("Initializing OpenGL");
		GL11.glEnable(GL11.GL_TEXTURE_2D);               
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
        
    	// enable alpha blending
    	GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
    	GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);//Note, the GL coordinates are flipped!
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		if(Remote2D.getInstance().artLoader != null)
		{
			Remote2D.getInstance().artLoader.reloadArt();
		}
		
		//CursorLoader.setCursor(new Texture("/res/gui/mouse.png"), new Vector2D(22,22));
	}
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen, boolean borderless) {
		int posX = Display.getX();
		int posY = Display.getY();
		
		if(borderless && fullscreen)
		{
			posX = 0;
			posY = 0;
			width = Display.getDesktopDisplayMode().getWidth();
			height = Display.getDesktopDisplayMode().getWidth();
			fullscreen = false;
		}

	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
	        (Display.isFullscreen() == fullscreen) &&
	        this.borderless == borderless)
	    {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		int freq = 0;
		
		for (int i=0;i<modes.length;i++) {
			DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
				if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
					if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
						targetDisplayMode = current;
						freq = targetDisplayMode.getFrequency();
                }
            }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            Log.warn("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }
	        	        
	        this.width = targetDisplayMode.getWidth();
	        this.height = targetDisplayMode.getHeight();
	        this.fullscreen = fullscreen;
	        
	        if(fullscreen == true)
	        	Display.destroy();
	        System.setProperty("org.lwjgl.opengl.Window.undecorated", borderless ? "true" : "false");
	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
	        Display.setLocation(posX, posY);
	        setIcons(Remote2D.getInstance().getGame().getIconPath());
	        Display.setVSyncEnabled(fullscreen);
	        if(fullscreen == true)
	        	Display.create();
	        
	        initGL();
	    } catch (LWJGLException e) {
	        Log.warn("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	    
	    for(int x=0;x<Remote2D.getInstance().guiList.size();x++)
			Remote2D.getInstance().guiList.get(x).initGui();
	}

}
