package com.arranger.apv;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

public class Main extends PApplet {

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;

	//This is a tradeoff between performance and precision
	private static final int BUFFER_SIZE = 512; //Default is 1024

	public static void main(String[] args) {
		PApplet.main(new String[] {Main.class.getName()});
	}

	public void settings() {
		size(WIDTH, HEIGHT, P3D);
	}

	public void setup() {
		//Minim
		Minim minim = new Minim(this);
		AudioPlayer song = minim.loadFile("03 When Things Get Strange v10.mp3", BUFFER_SIZE);
		song.play();

	}
}
