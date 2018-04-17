package com.arranger.apv.msg;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * @see https://www.openprocessing.org/sketch/166952
 */
public class CircularMessage extends MessageSystem {

	private static final int LARGEST_RADIUS = 100;
	
	private CircleText nvCirc;
	private PFont font;
	
	public CircularMessage(Main parent) {
		super(parent);
		//TODO Use Font Helper
		font = parent.createFont("Arial Bold", 20, true);
	}

	@Override
	protected void _draw(FadingMessage fadingMessage) {
		if (nvCirc == null) {
			nvCirc = new CircleText(parent.width / 2, parent.height / 2, joinMessage(fadingMessage, "  "));
		}
		
		//draw this thing
		doStandardFade(fadingMessage.frameFader.getFadePct());
		
		float fontSize = LARGEST_RADIUS; //parent.random(10, 40);
		parent.textFont(font, fontSize);
		parent.translate (parent.width / 2, parent.height / 2);
		parent.rotate(parent.oscillate(0, TWO_PI, 12));
		nvCirc.drawTextRing(0, 0, nvCirc.inText);
	}
	
	class CircleText {
		
		String inText;
		float xPos, yPos;
		float radius = LARGEST_RADIUS;
		private float startAngle = 0; // for future using
		private float crclStrWidth;

		CircleText(float in_posX, float in_posY, String in_txt) {
			setXY(in_posX, in_posY);
			setText(in_txt);
		}

		void setText(String in_txt) {
			this.inText = in_txt;
		}

		void setXY(float in_posX, float in_posY) {
			this.xPos = in_posX;
			this.yPos = in_posY;
		}

		void shiftXY(float in_posX, float in_posY) {
			this.xPos += in_posX;
			this.yPos += in_posY;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////

		// we set string and then method calculates size of the radius
		void drawTextRing(String in_txt) {
			setText(in_txt);
			drawTextOnRingBase();
		}

		void drawTextRing(float in_posX, float in_posY, String in_txt) {
			setXY(in_posX, in_posY);
			drawTextRing(in_txt);
		}

		void drawTextRing() {
			drawTextOnRingBase();
		}
		//////////////////////////////////////////////////////////////////////////////////////////////

		// we set string and radius and then method calculates size of the font
		void drawTextSizeForRadTxt(String in_txt, float in_diam) {
			this.inText = in_txt;
			parent.textSize(10);
			parent.textSize((TWO_PI * in_diam / 2) / (widthOfString(in_txt) / 10));
			drawTextOnRingBase();
		}

		void drawTextSizeForRadTxt(float in_posX, float in_posY, String in_txt, float in_diam) {
			setXY(in_posX, in_posY);
			drawTextSizeForRadTxt(in_txt, in_diam);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////

		// we set symbol and radius and then method counts amount of the symbols for
		// repetitions
		// if string contains more than 1 symbol then radius will be changed
		void drawSymbolsByRadius(String in_txt, float in_diam) {
			this.radius = in_diam / 2;
			resetStrBySymbNum(in_txt, 0);
			calcRadLenFromStirng();
			drawTextOnRingBase();
		}

		void drawSymbolsByRadius(float in_posX, float in_posY, String in_txt, float in_diam) {
			setXY(in_posX, in_posY);
			drawSymbolsByRadius(in_txt, in_diam);
		}

		//////////////////////////////////////////////////////////////////////////////////////////////

		// we set string and count of repetitions and then method calculates size of the
		// radius
		void drawStringRepetition(String in_txt, int in_countRepetition) {
			resetStrBySymbNum(in_txt, in_countRepetition);
			calcRadLenFromStirng();
			drawTextOnRingBase();
		}

		void drawStringRepetition(float in_posX, float in_posY, String in_txt, int in_countRepetition) {
			setXY(in_posX, in_posY);
			drawStringRepetition(in_txt, in_countRepetition);
			drawTextOnRingBase();
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		// helper methods

		// method create a new string that repeats itself as many times as necessary for
		// existing radius
		private void resetStrBySymbNum(String in_txt, int count) {
			if (count == 0) {
				count = (int)(TWO_PI*radius/ (widthOfString(in_txt)));
			}
			this.inText = "";
			while (count>0) {
				this.inText += in_txt;
				count--;
			}
		}

		// method of calculating the string width and the corresponding radius
		private void calcRadLenFromStirng() {
			this.crclStrWidth = widthOfString(this.inText);
			this.radius = crclStrWidth / TWO_PI;
		}

		// widthOfString("String width") != textWidth("String width")
		float widthOfString(String in_str) {
			int cnt = 0;
			float totalWidth = 0;
			while (cnt < in_str.length()) {
				totalWidth += parent.textWidth(in_str.charAt(cnt));
				++cnt;
			}
			return totalWidth;
		}

		//////////////////////////////////////////////////////////////////////////////////////////////

		void draw() {
			drawTextOnRingBase();
		}

		private void drawTextOnRingBase() {
			calcRadLenFromStirng();
			parent.textAlign(CENTER);

			int count = 0;
			float arcLength = 0;
			float totalAngle = crclStrWidth / radius;
			float angle;

			while (count < inText.length()) {
				float wdthTChar = parent.textWidth(inText.charAt(count));
				arcLength += wdthTChar / 2;
				angle = PI + arcLength / radius - totalAngle;
				float xx = PApplet.cos(angle + startAngle) * radius + xPos;
				float yy = PApplet.sin(angle + startAngle) * radius + yPos;

				parent.pushMatrix();
				parent.translate(xx, yy);
				parent.rotate(angle + PI / 2);
				parent.text(inText.charAt(count++), 0, 0);
				parent.popMatrix();
				arcLength += wdthTChar / 2;
			}
		}
	}
}
