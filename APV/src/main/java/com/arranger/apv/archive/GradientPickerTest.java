package com.arranger.apv.archive;

import java.awt.LinearGradientPaint;

import com.arranger.apv.gui.creator.GradientPicker;

/**
 * https://stackoverflow.com/questions/21423670/4-color-gradient-using-java-awt
 */
public class GradientPickerTest {

	public static void main(String[] args) {
		LinearGradientPaint lgp = new GradientPicker(null).showDialog();
		System.out.println(lgp);
	}

}
