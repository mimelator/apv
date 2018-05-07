package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/504313
 */
public class Hactivity extends LiteShapeSystem {
	
	private static final String[] GARBAGE = { "bilinear momentum", "brute force", "fruit morse", "computing inverse", "quanta", "hex dump",
			"cpu", "triangulation", "recursive backtrace", "parallell complexification" };

	private static final Color DEFAULT_COLOR = new Color(100, 255, 50);
	
	Color fgColor;
	int bg;
	int fg;
	int textS = 6;
	int resetDelay = 400;
	int cursor = 10;
	float t = 0;
	Item i = new HorizSplit(1);
	
	public Hactivity(Main parent, Color color) {
		super(parent);
		
		fgColor = color;
		bg = parent.color(0);
		fg = color.getRGB();
		
		parent.getColorHelper().register(getName(), fgColor, col -> {fgColor = col; fg = col.getRGB();});
	}
	
	public Hactivity(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getColor(0, DEFAULT_COLOR));
	}
	
	@Override
	public String getConfig() {
		//{Hactivity : [GREEN]}
		return String.format("{%s : [%s]}", getName(), parent.format(fgColor, true));
	}

	@Override
	public void draw() {
		t += 0.01;
		i.make(parent.width, parent.height);

		Point2D cp = parent.getCurrentPoint();
		int mouseX = (int)cp.getX();
		int mouseY = (int)cp.getY();
		
		parent.stroke(fg);
		parent.line(mouseX, mouseY - cursor, mouseX, mouseY + cursor);
		parent.line(mouseX - cursor, mouseY, mouseX + cursor, mouseY);
	}

	String filler(int i) {
		return GARBAGE[i % GARBAGE.length];
	}

	void label(String t) {
		parent.textSize(textS);
		parent.fill(fg);
		parent.noStroke();
		parent.rect(0, 0, 80, textS + 8);

		parent.fill(bg);
		parent.text(t, 4, textS + 4);
	}

	abstract class Item {
		abstract void make(float w, float h);
	}

	Item generateItem(int level) {
		return generateItem(level, parent.random(1) < 0.5);
	}

	Item generateItem(int level, boolean horizontal) {
		if (parent.random(1) < 0.9 && level < 5) {
			if (parent.random(1) < 0.4) {
				return new StackItem(level);
			}
			if (horizontal) {
				return new HorizSplit(level);
			} else {
				return new VertSplit(level);
			}
		} else {
			switch ((int) parent.random(7)) {
			case 0:
				return new Code();
			case 1:
				return new Web();
			case 2:
				return new Bar();
			case 3:
				return new Line();
			case 4:
				return new Pie();
			case 5:
				return new Data();
			case 6:
				return new Progress();
			}
		}
		return null;
	}

	class StackItem extends Item {
		Item a;
		Item b;

		int l;

		public StackItem(int level) {
			l = level;
			reset();
		}

		int frame = (int) parent.random(resetDelay);

		void make(float w, float h) {
			frame++;
			if (frame % resetDelay == 0) {
				reset();
			}

			a.make(w, h);
			parent.fill(bg, 100);
			parent.noStroke();
			parent.rect(0, 0, w, h);
			b.make(w, h);
		}

		void reset() {
			if (parent.random(1) > 0.5 || a == null) {
				a = generateItem(l + 1);
			}
			if (parent.random(1) > 0.5 || b == null) {
				b = generateItem(l + 1);
			}
		}
	}

	class HorizSplit extends Item {
		float split = parent.random(0.25f, 0.75f);
		Item a;
		Item b;

		int l;

		public HorizSplit(int level) {
			l = level;
			reset();
		}

		int frame = (int) parent.random(resetDelay);

		void make(float w, float h) {
			frame++;
			if (frame % resetDelay == 0) {
				reset();
			}

			a.make(w * split, h);
			parent.pushMatrix();
			parent.translate(w * split, 0);
			b.make(w * (1 - split), h);
			parent.popMatrix();

			parent.stroke(fg);
			parent.strokeWeight(1);
			parent.line(w * split, 0, w * split, h);
		}

		void reset() {
			split = parent.random(0.25f, 0.75f);

			boolean horizontal = parent.height < PApplet.min(split, 1 - split) * parent.width;
			if (parent.random(1) > 0.5 || a == null) {
				a = generateItem(l + 1, horizontal);
			}
			if (parent.random(1) > 0.5 || b == null) {
				b = generateItem(l + 1, horizontal);
			}
		}
	}

	class VertSplit extends Item {
		float split = parent.random(0.25f, 0.75f);
		Item a;
		Item b;
		int frame = (int) parent.random(resetDelay);

		int l;

		public VertSplit(int level) {
			l = level;
			reset();
		}

		void make(float w, float h) {
			frame++;
			if (frame % resetDelay == 0) {
				reset();
			}

			a.make(w, h * split);
			parent.pushMatrix();
			parent.translate(0, h * split);
			b.make(w, h * (1 - split));
			parent.popMatrix();

			parent.stroke(fg);
			parent.strokeWeight(1);
			parent.line(0, h * split, w, h * split);
		}

		void reset() {
			split = parent.random(0.25f, 0.75f);
			boolean horizontal = PApplet.min(split, 1 - split) * parent.height < parent.width;
			if (parent.random(1) > 0.5 || a == null) {
				a = generateItem(l + 1, horizontal);
			}
			if (parent.random(1) > 0.5 || b == null) {
				b = generateItem(l + 1, horizontal);
			}
		}
	}

	abstract class Text extends Item {
		String code = "";
		int frame = 0;

		void make(float w, float h) {
			parent.textSize(textS);

			frame++;
			if (frame % 2 == 0) {
				addCode();
			}
			int lines = code.split("\\n").length;
			if (lines * textS * 2 > h) {
				code = code.replaceFirst("([^\\n]+\\n)", "");
				lines--;
			}

			parent.fill(fg);
			parent.text(code, 0, 0, w, h);
		}

		abstract void addCode();
	}

	class Progress extends Item {
		float time = 0;
		int seed = (int) parent.random(300);
		float[] progress = new float[5];

		void make(float w, float h) {
			time += 0.1;
			float rowHeight = h / (progress.length * 2 + 1);
			for (int i = 0; i < progress.length; i++) {
				progress[i] += parent.noise(i, time) / 500;

				parent.pushMatrix();
				parent.translate(10, rowHeight * (i * 2 + 1));

				parent.fill(
						parent.lerpColor(bg, fg, progress[i] < 1 ? 0.5f : (PApplet.sin((float) time * 3) / 8 + 0.5f)));
				parent.noStroke();
				parent.rect(0, 0, (w - 20) * PApplet.min(progress[i], 1), rowHeight);

				parent.noFill();
				parent.stroke(fg);
				parent.strokeWeight(1);
				parent.rect(0, 0, w - 20, rowHeight);

				label(filler(i + seed));
				parent.popMatrix();
			}
		}
	}

	class Code extends Text {
		float seed = parent.random(200);

		int indentation = 0;
		String[] control = { "for(Complex root: crack.generate())", "if(confirmPassword(input))",
				"while(forger.hasNext())", "private class Identifier" };

		void addCode() {
			switch ((int) parent.random(4)) {
			case 0:
				code += spaces() + filler((int) (seed + parent.random(100))).replaceAll(" ", ".") + "();\n";
				break;
			case 1:
				code += spaces() + control[(int) (parent.random(control.length))] + "  {\n";
				indentation++;
				break;
			case 2:
				if (indentation > 0) {
					indentation--;
					code += spaces() + "}\n";
				}
				break;
			case 3: // do nothing
				break;
			}
		}

		String spaces() {
			String answer = "";
			for (int i = 0; i < indentation; i++) {
				answer += "   ";
			}
			return answer;
		}
	}

	class Data extends Text {
		float seed = parent.random(200);

		void addCode() {
			for (int i = 0; i < 8; i++) {
				code += PApplet.sq(parent.random(42)) + "    ";
			}
			code += "\n";
		}
	}

	class Web extends Item {
		float seed = parent.random(200);

		void make(float w, float h) {
			float angle = parent.noise(seed, t / 10) * 2;
			float[][] points = new float[4][2];
			parent.pushMatrix();
			parent.translate(w / 2, h / 2);

			for (int i = 0; i < 16; i++) {
				angle += 1;
				parent.pushMatrix();
				float x = PApplet.cos(angle * i) * w / 3;
				float y = PApplet.sin(angle * (i + 1)) * h / 3;

				parent.stroke(fg, 140);
				for (int j = 0; j < 4; j++) {
					float[] point = points[(int) (parent.noise(seed + j, i, t) * points.length)];
					parent.line(x, y, point[0], point[1]);
				}
				points[(int) (parent.noise(seed + 1, i, t) * points.length)] = new float[] { x, y };

				parent.translate(x, y);
				label(filler(i + (int) seed));
				parent.popMatrix();
			}
			parent.popMatrix();
		}
	}

	class Bar extends Item {
		float seed = parent.random(200);

		void make(float w, float h) {
			int num = (int) (w / 25);
			float con = w / (num + 1);
			for (int i = 0; i < num; i++) {
				// fill(fg);
				// noStroke();
				parent.stroke(fg);
				parent.noFill();
				float value = h * (1 - parent.noise(seed, t + 0.23f * i, i) * 0.8f);
				float x = con * (2.0f / 3 + i);
				parent.rect(x, h, con / 3 * 2, -value);

				if (i % 3 == 0) {
					parent.pushMatrix();
					parent.translate(x, h - value - 20);
					label(filler(i + (int) seed));
					parent.popMatrix();
				}
			}
		}
	}

	class Line extends Item {
		int num = 42;
		float seed = parent.random(200);

		void make(float w, float h) {

			// draw ticks
			int step = 7;
			parent.stroke(parent.lerpColor(bg, fg, 0.5f));
			for (int x = 0; x < w; x += step) {
				parent.line(x, h, x, h - 10);
			}
			for (int y = 0; y < h; y += step) {
				parent.line(0, y, 10, y);
			}

			// draw the graph
			float con = w / (num + 1);
			for (int i = 0; i < num - 1; i++) {
				for (int j = 0; j < 3; j++) {
					parent.stroke(fg);
					parent.strokeWeight(2);
					parent.line(con * (0.5f + i), h * parent.noise(seed, t + 0.23f * i, j), con * (1.5f + i),
							h * parent.noise(seed, t + 0.23f * (i + 1), j));
					if (i == 5) {
						parent.pushMatrix();
						parent.translate(con * (0.5f + i), h * parent.noise(seed, t + 0.23f * i, j) - 20);
						label(filler(j + (int) seed));
						parent.popMatrix();
					}
				}
			}
		}
	}

	class Pie extends Item {
		float seed = parent.random(200);

		void make(float w, float h) {
			parent.pushMatrix();
			parent.translate(w / 2, h / 2);
			parent.stroke(fg);
			parent.strokeWeight(2);
			float diameter = PApplet.min(w, h) * 0.6f;

			float angle = parent.noise(seed, t) * 2;
			for (int i = 0; i < 16; i++) {
				parent.fill(parent.lerpColor(bg, fg, parent.noise(seed, i + 100)));
				float da = PApplet.sq(parent.noise(seed, i, t)) * 3;
				parent.arc(0, 0, diameter, diameter, angle, angle + da);
				angle += da;
			}

			angle = parent.noise(seed, t) * 2;
			for (int i = 0; i < 16; i++) {
				angle += PApplet.sq(parent.noise(seed, i, t)) * 3;
				parent.pushMatrix();
				parent.translate(PApplet.cos(angle) * diameter / 1.6f, PApplet.sin(angle) * diameter / 1.6f);
				label(filler(i + (int) seed));
				parent.popMatrix();
			}
			parent.popMatrix();
		}
	}

}
