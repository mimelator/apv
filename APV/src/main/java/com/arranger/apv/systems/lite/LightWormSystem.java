package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.util.List;
import java.util.stream.IntStream;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.Reverser;

/**
 * https://www.openprocessing.org/sketch/396905
 */
public class LightWormSystem extends LiteShapeSystem {

	private static final int DEFAULT_REVERSE_PULSES = 2; //Direction Change every two pulses
	private static final float STROKE_WEIGHT = 1.0f;
	private static final float SHAPE_SIZE = 5f;//4; Accept Param (.5-20)  Large numbers MUST have small num dragons
	private static final int NUM_TRAILS = 32;//64; Trails Min(12) Max(64)
	
	private static final float MOTION_SPEED = 0.004f;//0.008f; Modulate (0.0005f - 0.012f);
	private static final int MOTION_RADIUS = 250;//200; TODO base this on screen size
	private static final int DEFAULT_NUM_DRAGONS = 7;//32;  Accept Param (2-10)
	
	private static final boolean USE_ORIG_COLORS_DEFAULT = true;
	private static final float DEFAULT_COLOR_SPEED = 5.0f;//50.0f;Modulate (4, 100)
	private static final float COLOR_SCALAR = 5.5f; //10  //higher number rainbow
	private static final int COLOR_ALPHA = 30;
	
	private static final float FADE_DIV = 5.0f;
	private static final float FADE_AREA = (float)NUM_TRAILS / FADE_DIV;

	private float[] fadeTable; // An array of alpha transparency: Each index has a float: a
	private float[][] ofs; // array of offset locations: Each index has a pair of floats [x,y]
	private float[][][] positionTable; // A 2D array of positiions: Each item has float: [x,y]

	private float counter = 0.0f;
	
	//params
	private boolean useOrigColors = USE_ORIG_COLORS_DEFAULT;
	private int numDragons = DEFAULT_NUM_DRAGONS;
	private float colorSpeed = DEFAULT_COLOR_SPEED;
	private ColorSystem [] colorSystems;
	
	private Reverser reverser;
	private APVShape shape;
	
	private class LightWormShapeData extends Data {
		
	}
	
	public LightWormSystem(Main parent) {
		super(parent);
	}
	
	public LightWormSystem(Main parent, boolean useOrigColors, int numDragons, float colorSpeed, ShapeFactory factory) {
		super(parent);
		this.useOrigColors = useOrigColors;
		this.numDragons = numDragons;
		this.colorSpeed = colorSpeed;
		if (factory != null) {
			setShapeFactory(factory);
			shape = factory.createShape(new LightWormShapeData());
		}
		
		if (!useOrigColors) {
			parent.getSetupEvent().register(() -> {
				APV<ColorSystem> colors = parent.getColors();
				ColorSystem cs = colors.getFirstInstanceOf(OscillatingColor.class);
				List<ColorSystem> siblings = colors.getSiblings(cs);
				colorSystems = new ColorSystem[numDragons];
				IntStream.range(0, colorSystems.length).forEach(i -> {
					colorSystems[i] = siblings.get((int)parent.random(siblings.size()));
				});
			});
		}
	}
	
	public LightWormSystem(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getBoolean(0, USE_ORIG_COLORS_DEFAULT),
				ctx.getInt(1, DEFAULT_NUM_DRAGONS),
				ctx.getFloat(2, DEFAULT_COLOR_SPEED),
				(ShapeFactory)ctx.loadPlugin(3));
	}
	
	@Override
	public String getConfig() {
		//{LightWormSystem : [false, 4, 16]}
		return String.format("{%s : [%b, %s, %s]}", getName(), useOrigColors, numDragons, colorSpeed);
	}

	private static final int TOTAL_OFFSET_VALS = 8; //Hard coded for the offset table
	
	@Override
	public void setup() {
		reverser = new Reverser(parent, DEFAULT_REVERSE_PULSES); 
		parent.getCommandSystem().registerHandler(Command.REVERSE, (command, source, modifiers) -> reverser.reverse());
		
		// initialize arrays
		fadeTable = new float[NUM_TRAILS];
		positionTable = new float[numDragons][NUM_TRAILS][2];
		ofs = new float[numDragons][TOTAL_OFFSET_VALS];

		for (int i = 0; i < numDragons; i++) {
			float a = (float)i / (numDragons - 1.0f);
			positionTable[i] = new float[NUM_TRAILS][2];
			ofs[i] = new float[NUM_TRAILS];

			for (int j = 0; j < TOTAL_OFFSET_VALS; j += 2) {
				float b = (float)j / (numDragons - 1.0f);
				ofs[i][j] = ((j % 4) == 0) ? 1.0f : 12.0f; // + random(10, 30)
				ofs[i][j + 1] = 10.0f * PI * a * (1.0f - b * 0.2f);
			}
		}

		for (int i = 0; i < NUM_TRAILS; i++) {
			if (i < FADE_AREA) {
				float x = i / (FADE_AREA - 1.0f);
				fadeTable[i] = cos(x * PI - PI) * 0.5f + 0.5f;
			} else {
				fadeTable[i] = 1.0f;
			}

			for (int j = 0; j < numDragons; j++) {
				positionTable[j][i] = new float[] { 0.0f, 0.0f };
			}
		}
	}

	
	@Override
	public void draw() {
		boolean reverse = reverser.isReverse();
		
		parent.addSettingsMessage("  --reverse: " + reverse);
		parent.strokeWeight(STROKE_WEIGHT);
		
		int halfWidth = parent.width / 2;
		int halfHeight = parent.height / 2;
		
		int fillTime = 0;
		for (int i = 0; i < NUM_TRAILS; i++) {  
			float sz = (NUM_TRAILS - i) * SHAPE_SIZE;
			float fidx = i / (NUM_TRAILS - 1.0f);
			float z = 1.0f + 0.2f * fidx;

			Color targetColor;
			if (useOrigColors) {
				float currentFade = fadeTable[i];
				int r = (int)((sin(fidx * COLOR_SCALAR + 1.3f * colorSpeed + 0.0f * PI) * 127 + 127) * currentFade);
				int g = (int)((sin(fidx * COLOR_SCALAR + 1.3f * colorSpeed + 0.3333f * PI) * 127 + 127) * currentFade);
				int b = (int)((sin(fidx * COLOR_SCALAR + 1.3f * colorSpeed + 0.6666f * PI) * 127 + 127) * currentFade);
				
				targetColor = new Color(r, g, b, 128);
			} else {
				Color color = colorSystems[i % colorSystems.length].getCurrentColor();
				targetColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), COLOR_ALPHA);
			}
			setColor(targetColor);

			int fillStart = parent.millis();
			
			for (int j = 0; j < numDragons; j++) {
				float [][] jPos = positionTable[j];
				float [] iPos = jPos[i];

				float shapeX = halfWidth + iPos[0] * z;
				float shapeY = halfHeight + iPos[1] * z;
				
				if (shape != null) {
					parent.shape(shape.getShape(), shapeX, shapeY, sz, sz);
				} else {
					parent.rect(shapeX, shapeY, sz, sz);
				}
			}
			
			fillTime += (parent.millis() - fillStart);
		}

		for (int i = 0; i < NUM_TRAILS - 1; i++) {
			for (int j = 0; j < numDragons; j++) {
				positionTable[j][i] = positionTable[j][i + 1];
			}
		}
		
		for (int i = 0; i < numDragons; i++) {
			float [][] iPos = positionTable[i];
			float [] iOfs = ofs[i];

			iPos[NUM_TRAILS - 1] = new float[] { 
					(sin(counter * iOfs[0] + iOfs[1]) + cos(counter * iOfs[2] + iOfs[3])) * MOTION_RADIUS,
					(cos(counter * iOfs[4] + iOfs[5]) + sin(counter * iOfs[6] + iOfs[7])) * MOTION_RADIUS};
		}
		

		if (reverse) {
			counter -= MOTION_SPEED;
		} else {
			counter += MOTION_SPEED;
		}
		parent.addSettingsMessage("  --fillTime: " + fillTime);
	}
	
	protected void setColor(Color c) {
		if (shape != null) {
			shape.setColor(c.getRGB(), c.getAlpha());
		}
		parent.fill(c.getRGB(), c.getAlpha());
	}
}
