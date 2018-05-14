package com.arranger.apv.archive;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

import processing.core.PImage;
import processing.opengl.PShader;

public class BumpMapTest extends LiteShapeSystem {
	
	public static final float DEFAULT_LIGHT_Z = 0.075f;

	public static final float[] LIGHT_POS = new float[] { 0f, 0f, DEFAULT_LIGHT_Z };

	// Light RGB and intensity (alpha)
	public static final float[] LIGHT_COLOR = new float[] { 1f, 0.8f, 0.6f, 1f };

	// Ambient RGB and intensity (alpha)
	public static final float[] AMBIENT_COLOR = new float[] { 0.6f, 0.6f, 1f, 0.2f };

	// Attenuation coefficients for light falloff
	public static final float[] FALLOFF = new float[] { .4f, 3f, 20f };
	
	PShader shader;
	PImage rock, rockNormals;

	public BumpMapTest(Main parent) {
		super(parent);
		shader = parent.loadShader("shader/lesson6a.frag");//parent.loadShader("shader/lesson6a.frag", "shader/lesson6.vert");
		
		rock = parent.loadImage("rock.png");
		rockNormals = parent.loadImage("rock_n.png");
	}

	@Override
	public void draw() {
//		shader.set("resolution", (float) parent.width, (float) parent.height);
//		shader.set("time", parent.millis() / 200);
		
		
//		shader.set("u_normals", rockNormals); 
//		shader.set("u_texture", rock); 
//		
//		shader.set("LightPos", parent.mouseX, parent.mouseY, DEFAULT_LIGHT_Z); //could update this position with the mouse
//		
//		//light/ambient colors
//		shader.set("LightColor", LIGHT_COLOR);
//		shader.set("AmbientColor", AMBIENT_COLOR);
//		shader.set("Falloff", FALLOFF);
		
//		parent.fill(130);
//		parent.rect(0, 0, 400, 400);
		//parent.image(rock, 0, 0);
		//parent.filter(shader);
		parent.shader(shader);
	}
}
