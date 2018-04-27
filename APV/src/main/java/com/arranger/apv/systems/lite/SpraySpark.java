package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;
import com.arranger.apv.util.draw.DrawHelper;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/100893
 * @author markimel, Gregory Bush
 *
 */
public class SpraySpark extends LiteShapeSystem {
	
	private static final float SLOW_TIME = .25f;
	private static final float PREV_LOC_OFFSET = .5f; 
	private static final float LOC_INCREMENT = .025f; //.05f;
	private static final int NUM_SPARKS_LOW = 10;
	private static final int NUM_SPARKS_HIGH = 40;

	private DrawHelper drawHelper;
	
	public SpraySpark(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getPulseListener().registerHandler(() -> {
				spark();
			});
		});
		
		parent.getSparkEvent().register(() -> {
			if (drawHelper == null) {
				spark();
				drawHelper = new DrawHelper(parent, this, () -> drawHelper = null);
			}
		});
	}

	/*
	 * The maximum number of particles to display at once. Lowering this will
	 * improve performance on slow systems.
	 */
	int PARTICLE_COUNT = 256;

	/*
	 * The lowest ratio of vertical speed retained after a spark bounces.
	 */
	float LOW_BOUNCE = 0.5f;

	/*
	 * The highest ratio of vertical speed retained after a spark bounces.
	 */
	float HIGH_BOUNCE = 0.8f;

	/*
	 * The variation in velocity of newly-created sparks.
	 */
	float SPRAY_SPREAD = 2.0f;

	/*
	 * Some predefined gravity settings to play with.
	 */
	float EARTH_GRAVITY = 1.0f / 16.0f;
	float MOON_GRAVITY = EARTH_GRAVITY / 6.0f;
	float JUPITER_GRAVITY = EARTH_GRAVITY * 2.5f;

	/*
	 * The amount of acceleration due to gravity.
	 */
	float GRAVITY = EARTH_GRAVITY;

	/*
	 * The amount of error allowed in model coordinate measurements. Lowering this
	 * will let sparks have tiny bounces longer.
	 */
	float TOLERANCE = 0.2f;

	/**
	 * The focal length from the viewer to the screen in model coordinates.
	 */
	float FOCAL_LENGTH = 1000;

	/**
	 * The distance in model coordinates from the viewer to where new sparks are
	 * created. Increasing this number will move the created sparks further away.
	 */
	float INTERACTION_DISTANCE = 7 * FOCAL_LENGTH;

	/*
	 * A custom 3D canvas used to draw the world.
	 */
	Canvas3D canvas;

	/*
	 * A collection of Particles that represent the spraying sparks.
	 */
	Particle sparks[] = new Particle[PARTICLE_COUNT];

	/*
	 * The index of the Particle to use for the next spark created.
	 */
	int nextSpark = 0;
	
	//internal state
	long lastFrameDrawn;
	float averageElapsedMillis = 20.0f;

	/**
	 * Perform initial setup needed for the sketch.
	 *
	 * @author Gregory Bush https://www.openprocessing.org/sketch/100893
	 */
	@Override
	public void setup() {
		canvas = new Canvas3D(FOCAL_LENGTH, INTERACTION_DISTANCE);
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			sparks[i] = new Particle(random(256), random(256), random(256));
		}
		lastFrameDrawn = millis();
	}

	/**
	 * When the mouse is dragged, create a new spark at the mouse position with a
	 * velocity based on the drag speed and direction.
	 *
	 * @author Gregory Bush
	 */
	public void spark() {
		if (canvas == null) {
			setup();
		}
		
		float prevOffset = PREV_LOC_OFFSET;
		
		java.awt.geom.Point2D locPt = parent.getCurrentPoint();
		int curLocX = (int)locPt.getX();
		int curLocY = (int)locPt.getY();
		
		int numSparks = (int)parent.random(NUM_SPARKS_LOW, NUM_SPARKS_HIGH);
		for (int index = 0; index < numSparks; index++) {
			prevOffset += LOC_INCREMENT;
			
			int prevLocX = (int)(curLocX * prevOffset);
			int prevLocY = (int)(curLocY * prevOffset);
			
			/*
			 * Convert the prior and current mouse screen coordinates to model coordinates.
			 */
			Point3D prior = canvas.toModelCoordinates(prevLocX, prevLocY);
			Point3D current = canvas.toModelCoordinates(curLocX, curLocY);

			/*
			 * The spark's initial velocity is the difference between the current and prior
			 * points, randomized a bit to create a "spray" effect and scaled by the elapsed
			 * time.
			 */
			Vector3D velocity = current.diff(prior);
			velocity.shift(new Vector3D(
					random(-SPRAY_SPREAD, SPRAY_SPREAD), 
					0,
					random(-SPRAY_SPREAD, SPRAY_SPREAD) * velocity.x));
			velocity.scale(1.0f / averageElapsedMillis);

			/*
			 * Set the spark's intital motion and queue up the next particle.
			 */
			sparks[nextSpark].initializeMotion(current, velocity);
			nextSpark = (nextSpark + 1) % PARTICLE_COUNT;
		}
	}
	
	protected long millis() {
		return (long)(SLOW_TIME * parent.millis());
	}

	@Override
	public void draw() {
		long now = millis();
		long elapsedMillis = now - lastFrameDrawn;
		lastFrameDrawn = now;
		averageElapsedMillis = .90f * averageElapsedMillis + .10f * elapsedMillis;

		for (Particle spark : sparks) {
			if (spark.isActive()) {
				spark.paint(elapsedMillis);
				spark.evolve(elapsedMillis);
			}
		}
	}

	/******************************************************************************
	 * A rudimentary 3D graphics library.
	 *
	 * I realized recently that Processing already has 3D graphics capabilities, so
	 * much of this could be done natively. However, this way does load quite
	 * quickly comparatively.
	 *
	 * @author Gregory Bush
	 */

	/**
	 * A point in 2D screen coordinates.
	 *
	 * @author Gregory Bush
	 */
	private static class Point2D {
		public final float x;
		public final float y;

		public Point2D(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * A vector in 3D model coordinates.
	 *
	 * @author Gregory Bush
	 */
	private static class Vector3D {
		public float x;
		public float y;
		public float z;

		public Vector3D(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void shift(Vector3D v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		public void scale(float c) {
			x *= c;
			y *= c;
			z *= c;
		}

		public Vector3D mul(float c) {
			return new Vector3D(c * x, c * y, c * z);
		}
	}

	/**
	 * A point in 3D model coordinates.
	 *
	 * @author Gregory Bush
	 */
	private static class Point3D {
		public float x;
		public float y;
		public float z;

		public Point3D(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void shift(Vector3D v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		public Point3D add(Vector3D v) {
			return new Point3D(x + v.x, y + v.y, z + v.z);
		}

		public Vector3D diff(Point3D p) {
			return new Vector3D(x - p.x, y - p.y, z - p.z);
		}
	}

	/**
	 * A Canvas3D allows drawing graphics primitives in a 3D coordinate system.
	 *
	 * @author Gregory Bush
	 */
	public class Canvas3D {
		private static final int MAX_LINE_WIDTH = 5;

		private final float focalLength;

		private final float interactionPlane;

		public Canvas3D(float focalLength, float interactionPlane) {
			this.focalLength = focalLength;
			this.interactionPlane = interactionPlane;
		}

		/**
		 * Convert a point in the 3D model to a point on the 2D screen.
		 */
		public Point2D toScreenCoordinates(Point3D p) {
			float scale = focalLength / p.z;

			return new Point2D(p.x * scale + parent.width / 2, p.y * scale + parent.height / 2);
		}

		/**
		 * Convert a point on the 2D screen to a point in the 3D model, projected on the
		 * interaction plane.
		 */
		public Point3D toModelCoordinates(float x, float y) {
			float scale = interactionPlane / focalLength;

			return new Point3D((x - parent.width / 2) * scale, (y - parent.height / 2) * scale, interactionPlane);
		}
		

		/**
		 * Scale the diameter of a sphere whose center is at a particular Z distance to
		 * its diameter on the screen.
		 */
		public float scaleToScreen(float diameter, float distance) {
			float results = diameter * focalLength / distance;
			return PApplet.constrain(results, 0, MAX_LINE_WIDTH);
		}

		private void drawLine(Point2D from, Point2D to) {
			parent.line(from.x, from.y, to.x, to.y);
		}

		private void drawPoint(Point2D p) {
			parent.point(p.x, p.y);
		}
		
		/**
		 * Draw a line between 3D points.
		 */
		public void drawLine(Point3D from, Point3D to, float weight) {
			parent.strokeWeight(scaleToScreen(weight, to.z));
			drawLine(toScreenCoordinates(from), toScreenCoordinates(to));
		}

		/**
		 * Draw a point in 3D.
		 */
		public void drawPoint(Point3D p, float weight) {
			parent.strokeWeight(scaleToScreen(weight, p.z));
			drawPoint(toScreenCoordinates(p));
		}

		/**
		 * Draw a circle with vertical normal vector.
		 */
		public void drawHorizontalCircle(Point3D center, float radius) {
			float screenRadius = canvas.scaleToScreen(radius, center.z);
			Point2D p = toScreenCoordinates(center);
			/*
			 * This is a cheat, but it looks fine and is faster than doing it right.
			 */
			parent.ellipse(p.x, p.y, screenRadius, screenRadius * .3f);
		}
	}

	/**
	 * Increase the intensity of a color value.
	 */
	float amplify(float n) {
		return PApplet.constrain(4 * n, 0, 255);
	}

	/******************************************************************************
	 * A Particle is a representation of a bouncing, colored spark that plays a
	 * sound when it strikes the ground.
	 *
	 * @author Gregory Bush
	 */
	public class Particle {
		/*
		 * The coordinates of the particle's current location.
		 */
		private Point3D location;

		/*
		 * The particle's velocity.
		 */
		private Vector3D velocity;

		/*
		 * The particle's color.
		 */
		private float red;
		private float green;
		private float blue;

		/*
		 * Was the particle drawn off the left of the screen?
		 */
		private boolean pastLeftWall;

		/*
		 * Was the particle drawn off the right of the screen?
		 */
		private boolean pastRightWall;
		
		private boolean pastTop;

		/**
		 * Create a Particle with a specified color and characteristic sound.
		 */
		public Particle(float red, float green, float blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		/**
		 * Initialize or reset all variables describing the motion of the particle to
		 * the specified values.
		 */
		public void initializeMotion(Point3D location, Vector3D velocity) {
			this.location = location;
			this.velocity = velocity;
			//weight is based on velocity
			//System.out.printf("initializeMotion: [x,y,z] [%1f, %2f, %3f]\n", velocity.x, velocity.y, velocity.z);
		}

		/**
		 * Returns true if the Particle should still be actively evolving in time.
		 */
		public boolean isActive() {
			/*
			 * We will consider the Particle active as long as it is on the other side of
			 * the screen than the viewer.
			 */
			return location != null && location.z >= FOCAL_LENGTH;
		}

		/*
		 * Draw a motion-blurred trajectory of a particular stroke weight and opacity.
		 * The stroke weight will be scaled based on the Particle's distance from the
		 * viewer.
		 */
		private void drawMotion(Point3D from, Point3D to, float weight, float opacity) {
			parent.stroke(red, green, blue, opacity);
			canvas.drawLine(from, to, weight);
		}

		/**
		 * Draw the Particle on the screen.
		 */
		public void paint(float elapsedMillis) {
			Point3D from = location;
			Point3D to = location.add(velocity.mul(elapsedMillis));

			/*
			 * Draw three motion blurs, successively narrower and brighter.
			 */
			drawMotion(from, to, 40, 4);
			drawMotion(from, to, 20, 32);
			drawMotion(from, to, 8, 255);

			/*
			 * Draw a splash and play the Particle's characteristic note if it has struck
			 * the ground.
			 */
			if (isUnderground(elapsedMillis)) {
				splash(to);
			}

			/*
			 * Remember if we drew off of the left or right of the screen. This is a bit
			 * awkward. Bouncing off geometry in the model coordinates would be better.
			 */
			Point2D p = canvas.toScreenCoordinates(to);
			pastLeftWall = p.x < 0;
			pastRightWall = p.x >= parent.width;
			pastTop = p.y < 0;
		}

		/*
		 * Draw the splash when the Particle strikes the ground and play the Particle's
		 * characteristic note if sound is enabled.
		 */
		private void splash(Point3D to) {
			/*
			 * The splash is a circle on the ground with dim illumination in its interior
			 * and a bright ring on its circumference.
			 */
			parent.stroke(red, green, blue, 128);
			parent.fill(red, green, blue, 64);
			canvas.drawHorizontalCircle(to, 128);

			/*
			 * At the point where the Particle touched the ground, draw a small but bright
			 * flash of light.
			 */
			parent.stroke(amplify(red), amplify(green), amplify(blue), 255);
			canvas.drawPoint(to, 16);
		}

		/*
		 * Is the Particle's next position beneath the surface of the ground?
		 */
		private boolean isUnderground(float elapsedMillis) {
			//return location.y + velocity.y * elapsedMillis > parent.height;
			return location.y + velocity.y > parent.height;
		}

		/*
		 * Various functions to determine the direction of the Particle's motion.
		 */

		private boolean isMovingLeft() {
			return velocity.x <= -TOLERANCE;
		}

		private boolean isMovingRight() {
			return velocity.x >= TOLERANCE;
		}

		private boolean isMovingUp() {
			return velocity.y <= -TOLERANCE;
		}

		private boolean isMovingDown() {
			return velocity.y >= TOLERANCE;
		}

		private boolean isMovingVertically() {
			return isMovingUp() || isMovingDown();
		}

		/*
		 * Reverse the horizontal motion of the Particle.
		 */
		private void bounceHorizontal() {
			velocity.x = -velocity.x * random(LOW_BOUNCE / 2, HIGH_BOUNCE / 2);
		}

		/*
		 * Reverse the vertical motion of the Particle.
		 */
		private void bounceVertical() {
			/*
			 * The Particle's kinetic energy will be scaled down randomly. It will lose
			 * energy with every bounce.
			 */
			//only change velocity if we're bouncing off the bottom
			if (pastTop) {
				velocity.y = -velocity.y;
			} else {
				velocity.y = -velocity.y * random(LOW_BOUNCE, HIGH_BOUNCE);
			}
		}

		/*
		 * Give the particle an inactive status, indicating it no longer needs to be
		 * evolved in time.
		 */
		private void deactivate() {
			location.z = 0;
		}

		/**
		 * Evolve the Particle's motion over the specified amount of time in millis.
		 */
		public void evolve(float elapsedMillis) {
			/*
			 * Bounce off of the left or right borders of the screen if the Particle has
			 * gone off.
			 */
			if ((pastLeftWall && isMovingLeft()) || (pastRightWall && isMovingRight())) {
				bounceHorizontal();
			}
			
			if (pastTop && isMovingUp()) {
				bounceVertical();
			}

			/*
			 * If the Particle has struck the ground, bounce vertically. Deactivate the
			 * particle if it has lost so much energy it is no longer really bouncing.
			 */
			if (isUnderground(elapsedMillis) && isMovingDown()) {
				bounceVertical();
				if (!isMovingVertically()) {
					deactivate();
				}
			}

			/*
			 * Add the Particle's velocity times elapsed time to its current location.
			 */
			location.shift(velocity.mul(elapsedMillis));

			/*
			 * Apply the accleration due to gravity.
			 */
			velocity.y += GRAVITY;
		}
	}

}
