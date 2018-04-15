package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PImage;

/**
 * From: https://www.openprocessing.org/sketch/402928
 */
public class PlasmaSystem extends LiteShapeSystem {
	
	private static final int TSIZE = 8; 
	private static final float RATIO = 0.35f;
	private static final int DISTANCE = 500;
	private static final int DEFAULT_ALPHA = 255;
	
	
	int initialSeed = 127;
	int maxSize;
	int featuresize;
	int[][] plasma;

	float a1, ai1;

	float[][] coords;
	
	int xr, yr, xg, yg, xb, yb;
	
	int alpha = DEFAULT_ALPHA;
	PImage img;
	PImage result;

	public PlasmaSystem(Main parent, int alpha) {
		super(parent);
		this.alpha = alpha;
	}
	
	public PlasmaSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_ALPHA));
	}
	
	@Override
	public String getConfig() {
		//{PlasmaSystem : [${PLASMA_ALPHA_HIGH}]}
		return String.format("{%1s : [%2s]}", getName(), alpha);
	}

	@Override
	public void setup() {
		
		//size(513, 513);  //Size is 2^tsize + 1
		
		maxSize = (int)(Math.pow(2, TSIZE)) + 1;
		featuresize = (int)(maxSize / 2);
		plasma = new int[maxSize][maxSize];
		coords = new float[3][11];

		for (int y = 0; y < maxSize; y += featuresize) {
			for (int x = 0; x < maxSize; x += featuresize) {
				setSample(x, y, (int) (random(-initialSeed, initialSeed)));
			}
		}

		int samplesize = (int)(featuresize);

		while (samplesize > 1) {
			DiamondSquare(samplesize);
			samplesize = samplesize >> 1;
			initialSeed *= RATIO;
		}
		
		for (int v = 0; v < 3; v++) {
			coords[v][0] = (int) (random(-(maxSize / 2), maxSize / 2));
			coords[v][1] = (int) (random(-(maxSize / 2), maxSize / 2));
			coords[v][2] = (int) (random(-255, 255));

			coords[v][3] = random(TWO_PI);
			coords[v][4] = random(TWO_PI);
			coords[v][5] = random(TWO_PI);

			coords[v][6] = random(0.002f, 0.005f);
			coords[v][7] = random(0.002f, 0.005f);
			coords[v][8] = random(0.002f, 0.005f);
		}
		a1 = random(TWO_PI);
		ai1 = 0.1f;
		
		img = parent.createImage(maxSize, (2 * maxSize), ARGB);
		result = parent.createImage(parent.width, parent.height, ARGB);
	}

//	int lowH = Integer.MAX_VALUE;
//	int highH = Integer.MIN_VALUE;
	
	@Override
	public void draw() {
		int width = maxSize;

		parent.addSettingsMessage("  --alpha: " + alpha);
		
//		parent.addDebugMsg("img pixel size: " + img.pixels.length);
//		parent.addDebugMsg("lowH: " + lowH + " highH: " + highH);
		
		
		for (int v = 0; v < 3; v++) {
			float x, y, z, ax, ay, az, ix, iy, iz;
			x = coords[v][0];
			y = coords[v][1];
			z = coords[v][2];
			ax = coords[v][3];
			ay = coords[v][4];
			az = coords[v][5];
			ix = coords[v][6];
			iy = coords[v][7];
			iz = coords[v][8];

			float rx1, ry1, rz1, rx2, ry2, rz2, rx3, ry3, rz3;
			float sx, sy;
			//Rotate around the Z axis
			rx1 = x * cos(az) - y * sin(az);
			ry1 = x * sin(az) + y * cos(az);
			rz1 = z;

			//Rotate around the Y axis
			rx2 = rx1 * cos(ay) - rz1 * sin(ay);
			rz2 = rx1 * sin(ay) + rz1 * cos(ay);
			ry2 = ry1;

			//Rotate around the X axis
			ry3 = ry2 * cos(ax) - rz2 * sin(ax);
			rz3 = ry2 * sin(ax) + rz2 * cos(ax);
			rx3 = rx2;

			//Perspective projection on screen
			sx = rx3 * 512 / (rz3 + DISTANCE);
			sy = ry3 * 512 / (rz3 + DISTANCE);

			coords[v][9] = sx + ((maxSize -1) / 2);
			coords[v][10] = sy + ((maxSize -1) / 2);

			coords[v][3] += ix;
			coords[v][4] += iy;
			coords[v][5] += iz;
		}

		xr = (int)(coords[0][9]);
		yr = (int)(coords[0][10]);
		xg = (int)(coords[1][9]);
		yg = (int)(coords[1][10]);
		xb = (int)(coords[2][9]);
		yb = (int)(coords[2][10]);

		int rc, gc, bc;
		img.loadPixels();
		a1=ai1;
		
		for (int i = 0; i < maxSize-1; i++) {
			int s1 = (int)(sin(a1) * (width >> 5));
			for (int j = 0; j < maxSize-1; j++) {
				int x1 = (j + xr + maxSize-1) % (maxSize-1);
				int x2 = (maxSize-2)-(i + xg + maxSize-1) % (maxSize-1);
				int x3 = (i + xb + maxSize-1 + s1) % (maxSize-1);

				int y1 = (i + yr + maxSize-1) % (maxSize-1);
				int y2 = (j + yg + maxSize-1 + s1) % (maxSize-1);
				int y3 = (j + yb + maxSize-1) % (maxSize-1);

				rc = plasma[x1][y1] + 128;
				gc = plasma[x2][y2] + 128;
				bc = plasma[x3][y3] + 128;
				int h = i + i + ((j + j) * width); //range of h = (255 + 255) + ((255 + 255) * 255)
				
//				lowH = Math.min(lowH, h);
//				highH = Math.max(highH, h);
				
				img.pixels[h] = color(rc, gc, bc);
				img.pixels[h+1] = color(rc, gc, bc);
				img.pixels[h+width] = color(rc, gc, bc);
				img.pixels[h+1+width] = color(rc, gc, bc);
			}
			a1+=0.02;
		}
		
		ai1 += 0.05;
		
		result.updatePixels();
		result.pixels = resizePixels(img.pixels, img.width, img.height, parent.width, parent.height);
		result.loadPixels();
		
		parent.image(result, 0, 0);
	}
	
	/**
	 * Taken from http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
	 */
	public int[] resizePixels(int[] pixels,int w1,int h1,int w2,int h2) {
	    int[] temp = new int[w2*h2] ;
	    int x_ratio = (int)((w1<<16)/w2) +1;
	    int y_ratio = (int)((h1<<16)/h2) +1;
	    int x2, y2 ;
	    for (int i=0;i<h2;i++) {
	        for (int j=0;j<w2;j++) {
	            x2 = ((j*x_ratio)>>16) ;
	            y2 = ((i*y_ratio)>>16) ;
	            temp[(i*w2)+j] = pixels[(y2*w1)+x2] ;
	        }                
	    }                
	    return temp ;
	}


	int getSample(int x, int y) {
		return plasma[(x + (maxSize - 1)) % (maxSize - 1)][(y + (maxSize - 1)) % (maxSize - 1)];
	}

	void setSample(int x, int y, int value) {
		plasma[(x + (maxSize - 1)) % (maxSize - 1)][(y + (maxSize - 1)) % (maxSize - 1)] = value;
	}

	void sampleSquare(int x, int y, int s, int r) {
		int hs = s / 2;

		int a = getSample(x - hs, y - hs);
		int b = getSample(x + hs, y - hs);
		int c = getSample(x - hs, y + hs);
		int d = getSample(x + hs, y + hs);

		setSample(x, y, ((a + b + c + d) / 4) + r);
	}

	void sampleDiamond(int x, int y, int s, int r) {
		int hs = s / 2;

		int a = getSample(x - hs, y);
		int b = getSample(x + hs, y);
		int c = getSample(x, y - hs);
		int d = getSample(x, y + hs);

		setSample(x, y, ((a + b + c + d) / 4) + r);
	}

	void DiamondSquare(int stepsize) {
		int halfstep = stepsize / 2;
		for (int y = halfstep; y < maxSize + halfstep; y += stepsize) {
			for (int x = halfstep; x < maxSize + halfstep; x += stepsize) {
				sampleSquare(x, y, stepsize, (int)(random(-initialSeed, initialSeed)));
			}
		}

		for (int y = 0; y < maxSize; y += stepsize) {
			for (int x = 0; x < maxSize; x += stepsize) {
				sampleDiamond(x + halfstep, y, stepsize, (int)(random(-initialSeed, initialSeed)));
				sampleDiamond(x, y + halfstep, stepsize, (int)(random(-initialSeed, initialSeed)));
			}
		}
	}

	int color(int r, int g, int b) {
		return parent.color(r, g, b, alpha);
	}
}
