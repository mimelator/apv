package com.arranger.apv.archive;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.PulseFader;

/**
 * https://www.openprocessing.org/sketch/492724
 **/

public class Slinky extends LiteShapeSystem {

	private static final int NUM_FRAMES_FOR_PULSE = 15;
	private static final int PULSE_MULTIPLIER = 5;
	private static final int NUM_SHAPES = 4;
	private static final int NUM_LINES_PER_SHAPE = 16;
	private static final float SPEED = 20 / NUM_LINES_PER_SHAPE;
	private static final int SCALAR = 200; //Modulate this to flatten out
	
	private int numShapes;
	private List<Shape> shapes;
	private PulseFader pulseFader;
	private float pulseScalar = 1;
	
	public Slinky(Main parent) {
		super(parent);
	}
	
	public Slinky(Main parent, int numShapes) {
		super(parent);
		this.numShapes = numShapes;
		
		parent.getSetupEvent().register(() -> {
			pulseFader = new PulseFader(parent, NUM_FRAMES_FOR_PULSE, PULSE_MULTIPLIER, pulseScalar);
		});
	}
	
	public Slinky(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, NUM_SHAPES));
	}

	@Override
	public void setup() {
		shapes = new ArrayList<Shape>(numShapes);
		IntStream.range(0, numShapes).forEach(e-> {
			float scalar = parent.random(SCALAR / numShapes, SCALAR);
			shapes.add(new Shape(scalar));
		});
	}
	
	@Override
	public void draw() {
		parent.smooth();
		shapes.forEach(s -> s.draw());
		
		pulseScalar = pulseFader.getValue();
	}

	class Shape {
		float time = 0;
		Particle p1, p2;
		
		Shape(float scalar) {
			p1 = new Particle(scalar);
			p2 = new Particle(scalar);
		}
		
		public void draw() {
			
			float increment = parent.random(.01f, .5f);
			
			int curColor = parent.getColor().getCurrentColor().getRGB();
			parent.noFill();
			parent.stroke(curColor);
			
			for (int index = 0; index < NUM_LINES_PER_SHAPE; index++) {
				parent.bezier(p1.ex, p1.ey, p1.ex + p1.ex2, p1.ey + p1.ey2, p2.ex + p2.ex2, p2.ey + p2.ey2, p2.ex, p2.ey);
				parent.bezier(p1.ex, p1.ey, p1.ex - p1.ex2, p1.ey - p1.ey2, p2.ex - p2.ex2, p2.ey - p2.ey2, p2.ex, p2.ey);

				p1.physics(time);
				p2.physics(time);
				time += increment;
			}
		}
	}

	class Particle {
		
		float angle = random(TWO_PI);
		float x, y, sx = cos(angle) * SPEED, sy = sin(angle) * SPEED;
		float x2, y2;

		float ex, ey, ex2, ey2;
		float scalar;

		public Particle(float scalar) {
			ex = x = random(parent.width);
			ey = y = random(parent.height);
			this.scalar = scalar;
		}

		void physics(float t) {
			x += sx;
			y += sy;
			if (x < 0 || x > parent.width) {
				sx = -sx;
			}
			if (y < 0 || y > parent.height) {
				sy = -sy;
			}

			x2 = (int) (cos((float) t) * scalar * pulseScalar);
			y2 = (int) (sin((float) t) * scalar * pulseScalar);

			ex += (x - ex) / SPEED;
			ey += (y - ey) / SPEED;
			ex2 += (x2 - ex2) / SPEED;
			ey2 += (y2 - ey2) / SPEED;
		}
	}
}
