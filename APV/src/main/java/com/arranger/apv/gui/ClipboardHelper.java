package com.arranger.apv.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class ClipboardHelper {

	public ClipboardHelper(String text) {
		Toolkit.getDefaultToolkit().getSystemClipboard().
			setContents(new StringSelection(text), null);
	}

}
