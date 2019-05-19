package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.OscillatingColor;

import processing.core.PApplet;

public class Tubular extends LiteShapeSystem {
	
   protected static final int SQUARE_SIDE = 30;
   protected static final int TUBULAR_SIDE = 1200;
   
   private static final int TUBE_LOW_VALUE = 1;
   private static final int TUBE_HIGH_VALUE = 10;
   private static final int STROKE_WEIGHT = 15;
   private static final int BOX_ALPHA = 50;
   private static final int ROTATION_SPEED = 5;
   private static final int TUBE_SPEED = 15;
   private static final int SPACE_BETWEEN_SQUARES = 50;
   
   private static final float MIN_TUBE_MOD = 0.5f;
   private static final float MAX_TUBE_MOD = 2.5f;
   private static final float TUBE_MOD_STEP = 0.5f;
   private ColorSystem oscColor;

   public Tubular(Main parent) {
       super(parent);
       parent.getSetupEvent().register(() -> {
           APV<ColorSystem> colSystem = parent.getColors();
           this.oscColor = (ColorSystem)colSystem.getFirstInstanceOf(OscillatingColor.class);
       });
   }

   public void draw() {
       this.setDrawMode();
       for (float mod = MIN_TUBE_MOD; mod < MAX_TUBE_MOD; mod += TUBE_MOD_STEP) {
           this.parent.pushMatrix();
           this.drawTube(mod);
           this.parent.popMatrix();
       }
       this.drawGrids();
   }

   protected void setDrawMode() {
       this.parent.ellipseMode(3);
   }

   protected void drawGrids() {
       Point2D pt = this.parent.getCurrentPoint();
       int mouseX = (int)pt.getX();
       int mouseY = (int)pt.getY();
       Color color = this.oscColor.getCurrentColor();
       this.parent.fill(color.getRGB(), BOX_ALPHA);
       this.parent.stroke(1);
       float theta = this.parent.oscillate(0.0f, 360, ROTATION_SPEED);
       for (int i = 0; i < this.parent.width; i += SPACE_BETWEEN_SQUARES) {
           for (int j = 0; j < this.parent.height; j += SPACE_BETWEEN_SQUARES) {
               float dist = PApplet.sqrt((PApplet.sq((mouseX - i)) + PApplet.sq((mouseY - j)))) / 10.0f;
               
               this.parent.pushMatrix();
               this.parent.translate(i, j);
               this.parent.rotate(PApplet.radians(theta));
               this.parent.translate(- i, - j);
               this.drawGridItem(i, j, dist);
               this.parent.popMatrix();
           }
       }
   }

   protected void drawGridItem(int i, int j, float dist) {
       this.parent.ellipse(
    		   i, 
    		   j, 
    		   PApplet.sq(PApplet.sqrt((SQUARE_SIDE - dist))), 
    		   PApplet.sq(PApplet.sqrt((SQUARE_SIDE - dist))));
   }

   protected void drawTube(float mod) {
       this.parent.strokeWeight(STROKE_WEIGHT * mod);
       this.parent.strokeCap(2);
       this.parent.stroke(Color.BLACK.getRGB());
       
       float oscMod = this.parent.oscillate(SPACE_BETWEEN_SQUARES, NO_ALPHA, TUBE_SPEED);
       this.parent.fill(this.parent.getColor().getCurrentColor().getRGB(), SPACE_BETWEEN_SQUARES * oscMod);
       float sizeModifier = this.parent.oscillate(TUBE_LOW_VALUE * mod, TUBE_HIGH_VALUE * mod, TUBE_SPEED);
       float tubeSide = TUBULAR_SIDE / sizeModifier;
       this.drawTubeShape(tubeSide);
   }

   protected void drawTubeShape(float tubeSide) {
       this.parent.ellipse((this.parent.width / 2), (this.parent.height / 2), tubeSide, tubeSide);
   }
}



