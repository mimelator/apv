package com.arranger.apv.test;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.arranger.apv.gradient.GradientHelper;
import com.arranger.apv.util.Configurator;

public class GradientTest extends APVPluginTest {

	private static final float EPSILON = 0.001f;
	private static final int WIDTH = 50;

	public GradientTest() {
	}

	@Override
	protected void setFrameIndexes() {

	}

	@Test
	public void testSerializeGradient() {
		LinearGradientPaint lgp = createLGP();
		GradientHelper gh = new GradientHelper(parent, lgp);
		String config = gh.getConfig();
		assert(config != null);
		
		Configurator configurator = new Configurator(parent);
		GradientHelper gh2 = (GradientHelper)configurator.loadObjectFromConfig(config);
		assert(gh2 != null);
		LinearGradientPaint result = gh2.getLinearGradientPaint();
		assert(result != null);
		
		assert(lgp.getStartPoint().equals(result.getStartPoint()));
		assert(lgp.getEndPoint().equals(result.getEndPoint()));
		assert(lgp.getColors().length == result.getColors().length);
		assert(lgp.getFractions().length == result.getFractions().length);
		
		IntStream.range(0, lgp.getColors().length).forEach(i -> {
			assert(lgp.getColors()[i].equals(result.getColors()[i]));
			assert(isEqual(lgp.getFractions()[i], result.getFractions()[i]));
		});
	}
	
	protected boolean isEqual(float v1, float v2) {
		return Math.abs(v1 - v2) < EPSILON;
	}

	@Test
	public void testGradient() {
		LinearGradientPaint p = createLGP();
		int width = (int) p.getEndPoint().getX();

		Rectangle bounds = new Rectangle(0, 0, width, 1);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		PaintContext context = p.createContext(null, bounds, bounds, new AffineTransform(), hints);
		assert (context != null);
		Raster raster = context.getRaster(0, 0, width, 1);
		assert (raster != null);

		IntStream.range(0, width).forEach(i -> {
			int[] pixel = raster.getPixel(i, 0, new int[3]);
			Color result = new Color(pixel[0], pixel[1], pixel[2]);
			System.out.printf("%d -> %s\n", i, parent.format(result));
		});

		IntStream.range(0, width).forEach(i -> {
			Color result = getColor(p, (float) i / (float) width);
			System.out.printf("%d -> %s\n", i, parent.format(result));
		});
	}

	protected LinearGradientPaint createLGP() {
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(WIDTH, WIDTH);
		float[] dist = { 0.0f, 0.2f, 1.0f };
		Color[] colors = { Color.RED, Color.WHITE, Color.BLUE };
		LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
		return p;
	}

	public Color getColor(LinearGradientPaint lgp, float pct) {
		int width = (int) lgp.getEndPoint().getX();

		Rectangle bounds = new Rectangle(0, 0, width, 1);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		PaintContext context = lgp.createContext(null, bounds, bounds, new AffineTransform(), hints);
		assert (context != null);
		Raster raster = context.getRaster(0, 0, width, 1);
		assert (raster != null);

		int index = (int) (pct * width);

		int[] pixel = raster.getPixel(index, 0, new int[3]);
		Color result = new Color(pixel[0], pixel[1], pixel[2]);
		return result;
	}
}
