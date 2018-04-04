package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;

/**
 * https://www.openprocessing.org/sketch/396905
 */
public class LightWormSystem extends LiteShapeSystem {

	int circleCount = 64;
	int circleSize = 4;
	int radius = 200;
	int snakeCount = 32;
	float colorSpeed = 50.0f;

	float[][] ofs; // array of offset locations: Each index has a pair of floats [x,y]
	float[][] colorTable; // An array of colors: Each index has a trio of floats[r,g,b]
	float[] fadeTable; // An array of alpha transparency: Each index has a float: a
	float[][][] positionTable; // A 2D array of positiions: Each item has float: [x,y]

	float fadeDiv = 1;
	float fadeArea = circleCount / fadeDiv;
	float fadeStart = circleCount - fadeArea;

	float time = 0.0f;

	public LightWormSystem(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {

		// initialize arrays
		fadeTable = new float[circleCount];
		colorTable = new float[circleCount][3];

		positionTable = new float[snakeCount][circleCount][2];
		ofs = new float[snakeCount][circleCount];

		for (int i = 0; i < snakeCount; i++) {
			int a = i / (snakeCount - 1);
			positionTable[i] = new float[circleCount][2];
			ofs[i] = new float[circleCount];

			for (int j = 0; j < 8; j += 2) {
				int b = j / (snakeCount - 1);
				ofs[i][j] = ((j % 4) == 0) ? 1.0f : 12.0f; // + random(10, 30)
				ofs[i][j + 1] = 10.0f * PI * a * (1 - b * 0.2f);
			}
		}

		for (int i = 0; i < circleCount; i++) {
			colorTable[i] = new float[] { 0.0f, 0.0f, 0.0f };

			if (i < fadeArea) {
				float x = i / (fadeArea - 1.0f);
				fadeTable[i] = cos(x * PI - PI) * 0.5f + 0.5f;
			} else {
				fadeTable[i] = 1.0f;
			}

			// println("fadeTable[" + i + "]=" + fadeTable[i]);

			for (int j = 0; j < snakeCount; j++) {
				positionTable[j][i] = new float[] { 0.0f, 0.0f };
			}
		}
	}
	
	boolean debugRGB = true;
	float cc = 10.0f;

	@Override
	public void draw() {
	    //noStroke();
	    //fill(255);

	    int fillTime = 0;
	    for (int i = 0; i < circleCount; i++) {  
	        int sz = (circleCount - i) * circleSize;
	        float fidx = i / (circleCount - 1.0f);
	        float z = 1.0f + 0.2f * fidx;
	        
	        float r = (sin(fidx * cc + 1.3f * colorSpeed + 0.0f * PI) * 127 + 127) * fadeTable[i];
	        float g = (sin(fidx * cc + 1.3f * colorSpeed + 0.3333f * PI) * 127 + 127) * fadeTable[i];
	        float b = (sin(fidx * cc + 1.3f * colorSpeed + 0.6666f * PI) * 127 + 127) * fadeTable[i];
	      
	        parent.fill(r, g, b);
	        
	      if (debugRGB) {
	          println("rgb[" + i + "]: " + r + " " + g + " " + b);
	          
	          println("sz: " + sz);
	          println("fidx: " + fidx);
	          println("z: " + z);
	        }
	      
	      int fillStart = parent.millis();
	        for (int j = 0; j < snakeCount; j++) {
	            float ellipseWidth = parent.width / 2 + positionTable[j][i][0] * z;
	            float ellipseHeight = parent.height / 2 + positionTable[j][i][1] * z;
	            parent.ellipse(ellipseWidth, ellipseHeight, sz, sz);
	            //println("ellipseWidth: " + ellipseWidth + " ellipseHeight: " + ellipseHeight + " sz: " + sz);
	        }
	        fillTime += (parent.millis() - fillStart);
	    }
	    
	  
	     //for (int i = 0; i < circleCount - 1; i++) {
	     //    colorTable[i] = colorTable[i + 1];
	      
	     //    for (int j = 0; j < snakeCount; j++) {
	     //        positionTable[j][i] = positionTable[j][i + 1];
	     //    }
	     //}
	  
	    //    *********************CAN BE REMOVED*********************
	    // colorTable[circleCount - 1] = new float[]{sin(time * colorSpeed) * 127 + 127, 
	    //                                           sin(time * colorSpeed + 0.333 * PI) * 127 + 127, 
	    //                                           sin(time * colorSpeed + 0.666 * PI) * 127 + 127};
	    //    *********************CAN BE REMOVED*********************
	  
	  
	  //RE_ADD
	   //for (int i = 0; i < snakeCount; i++) {
	   //    positionTable[i][circleCount - 1] = new float[] { 
	   //                                            (sin(time * ofs[i][0] + ofs[i][1]) + cos(time * ofs[i][2] + ofs[i][3])) * radius,
	   //                                            (cos(time * ofs[i][4] + ofs[i][5]) + sin(time * ofs[i][6] + ofs[i][7])) * radius};
	   //}  
	  
	  println(parent.frameCount + ": " + fillTime);
	  time += 0.008f;
	  debugRGB = false;
	}
	
	private void println(String msg) {
		System.out.println(msg);
	}

}
