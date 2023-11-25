//package com.inetfeedback.jarscan

import javax.swing.*;

import groovy.swing.*;
import javax.swing.text.*;
import java.awt.*;

class JarscanGui {
	  
	private JFrame frame;

	public JarscanGui()
	{
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//GraphicsDevice gd = ge.defaultScreenDevice();
		frame = new JFrame(""); 
		Dimension d = new Dimension(1000, 500);
		frame.setSize(d);
		frame.visible = true;
		frame.show();
	}
	
	public Document getDocument() { return doc; }
	
	public void insertString(String msg)
	{
		text
	}	
}