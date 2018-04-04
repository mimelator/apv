package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;

/**
 * https://www.openprocessing.org/sketch/396905
 */
public class LightWormSystem extends LiteShapeSystem {

	private static final float COUNTER_INCREMENT = 0.008f;
	private static final int TOTAL_OFFSET_VALS = 8;
	private static final int NUM_SHAPES = 32;//64;
	private static final int SHAPE_SIZE = 4;
	private static final int MOTION_RADIUS = 250;//200;
	private static final int SHAPE_HEAD_COUNT = 16;//32;
	private static final float COLOR_SPEED = 50.0f;
	private static final float COLOR_SCALAR = 10.0f;
	
	private static final float FADE_DIV = 1.0f;
	private static final float FADE_AREA = (float)NUM_SHAPES / FADE_DIV;

	private float[][] ofs; // array of offset locations: Each index has a pair of floats [x,y]
	private float[][] colorTable; // An array of colors: Each index has a trio of floats[r,g,b]
	private float[] fadeTable; // An array of alpha transparency: Each index has a float: a
	private float[][][] positionTable; // A 2D array of positiions: Each item has float: [x,y]

	private float counter = 0.0f;

	public LightWormSystem(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		// initialize arrays
		fadeTable = new float[NUM_SHAPES];
		colorTable = new float[NUM_SHAPES][3];

		positionTable = new float[SHAPE_HEAD_COUNT][NUM_SHAPES][2];
		ofs = new float[SHAPE_HEAD_COUNT][TOTAL_OFFSET_VALS];

		for (int i = 0; i < SHAPE_HEAD_COUNT; i++) {
			float a = (float)i / (SHAPE_HEAD_COUNT - 1.0f);
			positionTable[i] = new float[NUM_SHAPES][2];
			ofs[i] = new float[NUM_SHAPES];

			for (int j = 0; j < TOTAL_OFFSET_VALS; j += 2) {
				float b = (float)j / (SHAPE_HEAD_COUNT - 1.0f);
				ofs[i][j] = ((j % 4) == 0) ? 1.0f : 12.0f; // + random(10, 30)
				ofs[i][j + 1] = 10.0f * PI * a * (1.0f - b * 0.2f);
			}
		}

		for (int i = 0; i < NUM_SHAPES; i++) {
			colorTable[i] = new float[] { 0.0f, 0.0f, 0.0f };

			if (i < FADE_AREA) {
				float x = i / (FADE_AREA - 1.0f);
				fadeTable[i] = cos(x * PI - PI) * 0.5f + 0.5f;
			} else {
				fadeTable[i] = 1.0f;
			}

			for (int j = 0; j < SHAPE_HEAD_COUNT; j++) {
				positionTable[j][i] = new float[] { 0.0f, 0.0f };
			}
		}
	}

	@Override
	public void draw() {
		parent.strokeWeight(1.0f);
		
		int halfWidth = parent.width / 2;
		int halfHeight = parent.height / 2;

		int fillTime = 0;
		for (int i = 0; i < NUM_SHAPES; i++) {  
			float sz = (NUM_SHAPES - i) * SHAPE_SIZE;
			float fidx = i / (NUM_SHAPES - 1.0f);
			float z = 1.0f + 0.2f * fidx;

			float currentFade = fadeTable[i];
			float r = (sin(fidx * COLOR_SCALAR + 1.3f * COLOR_SPEED + 0.0f * PI) * 127 + 127) * currentFade;
			float g = (sin(fidx * COLOR_SCALAR + 1.3f * COLOR_SPEED + 0.3333f * PI) * 127 + 127) * currentFade;
			float b = (sin(fidx * COLOR_SCALAR + 1.3f * COLOR_SPEED + 0.6666f * PI) * 127 + 127) * currentFade;
			parent.fill(r, g, b);

			int fillStart = parent.millis();
			for (int j = 0; j < SHAPE_HEAD_COUNT; j++) {
				float [][] jPos = positionTable[j];
				float [] iPos = jPos[i];

				float shapeX = halfWidth + iPos[0] * z;
				float shapeY = halfHeight + iPos[1] * z;
				
				parent.rect(shapeX, shapeY, sz, sz);
			}
			fillTime += (parent.millis() - fillStart);
		}

		for (int i = 0; i < NUM_SHAPES - 1; i++) {
			colorTable[i] = colorTable[i + 1];

			for (int j = 0; j < SHAPE_HEAD_COUNT; j++) {
				positionTable[j][i] = positionTable[j][i + 1];
			}
		}

		float theta = counter * COLOR_SPEED;
		colorTable[NUM_SHAPES - 1] = new float[]{
				sin(theta) * 127 + 127, 
				sin(theta + 0.333f * PI) * 127 + 127, 
				sin(theta + 0.666f * PI) * 127 + 127};


		for (int i = 0; i < SHAPE_HEAD_COUNT; i++) {
			float [][] iPos = positionTable[i];
			float [] iOfs = ofs[i];

			iPos[NUM_SHAPES - 1] = new float[] { 
					(sin(counter * iOfs[0] + iOfs[1]) + cos(counter * iOfs[2] + iOfs[3])) * MOTION_RADIUS,
					(cos(counter * iOfs[4] + iOfs[5]) + sin(counter * iOfs[6] + iOfs[7])) * MOTION_RADIUS};
		}  

		counter += COUNTER_INCREMENT;
		parent.addDebugMsg("LightWormRendering Speed: " + fillTime);
	}
}
