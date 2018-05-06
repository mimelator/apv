package com.arranger.apv.gui.creator;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextArea;

import com.arranger.apv.Main;

@SuppressWarnings("serial")
public class EmojisPanel extends SetPackPanel {
	
	private JTextArea textArea;
	private List<String> origMsgList; 
	
	public EmojisPanel(Main parent) {
		super(parent, PANELS.EMOJIS);
		textArea = new JTextArea(15, 30);
		
		//get the current messages
		origMsgList = parent.getRandomMessagePainter().getMsgList();
		origMsgList.forEach(msg -> {
			textArea.append(msg);
			textArea.append(System.lineSeparator());
		});
		add(textArea);
	}
	
	public void updateForDemo(boolean isDemoActive, Path parentDirectory) {
		if (isDemoActive) {
			parent.getRandomMessagePainter().setMsgList(getMsgList());
		} else {
			parent.getRandomMessagePainter().setMsgList(origMsgList);
		}
	}
	
	@Override
	public void createFilesForSetPack(Path parentDirectory) {
		//No files to create
	}
	
	private List<String> getMsgList() {
		String text = textArea.getText();
		String[] split = text.split("[\\r\\n]+"); //https://stackoverflow.com/questions/454908/split-java-string-by-new-line
		return Arrays.asList(split);
	}
}