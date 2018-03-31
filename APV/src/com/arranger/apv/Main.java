package com.arranger.apv;

import com.arranger.apv.factories.CircleFactory;
import com.arranger.apv.systems.ParticleSystem;

import processing.core.PApplet;

public class Main extends PApplet {

	public static final String SONG = "03 When Things Get Strange v10.mp3";
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024
	
	private static final int NUMBER_PARTICLES = 1000;

	
	protected ShapeSystem sys;
	protected Audio audio;
	protected Gravity gravity;
	
	public static void main(String[] args) {
		PApplet.main(new String[] {Main.class.getName()});
	}

	public void settings() {
		size(WIDTH, HEIGHT, P3D);
	}
	
	public Audio getAudio() {
		return audio;
	}
	
	public Gravity getGravity() {
		return gravity;
	}

	public void setup() {
		gravity = new Gravity(this);
		audio = new Audio(this, SONG, BUFFER_SIZE);

		//Graphics hints
		orientation(LANDSCAPE);
		hint(DISABLE_DEPTH_MASK);
		
		//Create Shape Factories and Shape Systems
		CircleFactory fact = new CircleFactory(this);
		sys = new ParticleSystem(this, fact, NUMBER_PARTICLES);
		sys.setup();
	}
	
	public void draw() {
		background(0);
		sys.draw();
		
		fill(255);
		textSize(16);
//		text("Frame rate: " + (int)frameRate, 10, 20);
//		text("mouseXY:  " + mouseX + " " + mouseY, 10, 36);
//		text("Gravity: " + gravity.getCurrentGravity(), 10, 52);
	}
}
