package com.inetfeedback.jarscan;


/* TextDemo.java requires no other files. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*; 
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element; 

import java.io.StringReader;
import java.io.IOException;
;

public class TextDemo extends JPanel implements ActionListener {
    protected JTextField textField;
    //protected JTextArea textArea;
    protected JEditorPane textArea;
    protected HTMLDocument document;
    private final static String newline = "\n";
    protected JLabel label;
    private boolean bEnterPressedInTextField = false;
	private boolean bBackGroundColor = false;
	private JPanel answerPanel;
	private int htmlLocation = 0;
	private HTMLEditorKit kit;
	private Element htmlBody, root;
	private final static String COPY_HTML =
		"<p>&copy; 1999, O'Reilly &amp; Associates</p>";
	
    public TextDemo() {
        super(new GridBagLayout()); 

        //JTextPane textPane = new JTextPane();
		//textPane.setEditable(false);

        //textArea = new JTextArea(5, 20);
        String firstHtml = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'><html xmlns='http://www.w3.org/1999/xhtml' xml:lang='fi' lang='fi'><head><title>Jarscan 2.1</title> </head><body>";
        htmlLocation = firstHtml.length()+1; 
        //textArea = new JEditorPane("text/html", firstHtml +"<div id='bodytext'>kissa</div> </body> </html>");
        textArea = new JEditorPane("text/html", null);
		JScrollPane scrollPane = new JScrollPane(textArea);
        
    	document = (HTMLDocument)textArea.getDocument();
    	initDocument();
        //textArea.setEditable(false);
        //JScrollPane scrollPane = new JScrollPane(textArea);

    	//htmlBody = document.getIterator(HTML.Tag.BODY);

    	Action a = new HTMLEditorKit.InsertHTMLTextAction("InsertCopyright",
    			COPY_HTML, HTML.Tag.BODY, HTML.Tag.P);
    	//a.putValue(Action.SMALL_ICON, new ImageIcon("icons/cpyrght.gif"));
    	a.putValue(Action.NAME, "Teletype Text");
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        //c.gridheight = GridBagConstraints.REMAINDER;

        textField = getTextField();
        //c.fill = GridBagConstraints.HORIZONTAL;
        //scrollPane.add(textField);
        //scrollPane.add(textArea);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        answerPanel = new JPanel();        
        //panel.add(scrollPane, c);
        //panel.add(textArea, c);
        //c.fill = GridBagConstraints.NORTH;
        //add(scrollPane, c);
		add(scrollPane, c);
        //add(textPane, c);
        label = new JLabel ("label");
        textField = newTextField();
        answerPanel.add(label);
        answerPanel.add(textField);
        //c.fill = GridBagConstraints.SOUTH;
        add(answerPanel);        
    }

    private void initDocument()
    {
     	kit = (HTMLEditorKit) textArea.getEditorKit();
    	try {
    		kit.insertHTML(document,0,"<html><body><b>This is bold</b><br/><i>this is italics</i></html></body>",0,0, HTML.Tag.BODY);
    		root = document.getDefaultRootElement();
    	} catch (BadLocationException be) {
    		be.printStackTrace();
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
     	//
    	   
    }
    
    public boolean isEnterPressedInTextField()
    {
    	return bEnterPressedInTextField;
    }
    
    public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();        
    }

    private JEditorPane getTextArea() { return textArea; }
    
    public void insertText(String msg)
    {
    	/*
    	if (bBackGroundColor)
    		textArea.setBackground(Color.BLUE);
    	else
    		textArea.setBackground(Color.GRAY);
    		*/
    	//textArea.append(msg +"\n");
    	//document.insertString(arg0, arg1, arg2)
    	try {
    		String html = msg; 
    		document.insertBeforeEnd(root, msg);
    		htmlLocation = htmlBody.getStartOffset();
    		//insertHTML(html, htmlLocation);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void insertHTML
    (String html, int location)
    throws IOException, BadLocationException 
    {
    	//assumes editor is already set to "text/html" type
    	//HTMLEditorKit kit = (HTMLEditorKit) textArea.getEditorKit();
    	//HTMLDocument doc = (HTMLDocument)editor.getDocument();
    	StringReader reader = new StringReader(html);
    	kit.read(reader, document, location);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add contents to the window.
        frame.add(new TextDemo());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void changeBackGroundColor(boolean p_bBackGroundColor)
	{
		bBackGroundColor = p_bBackGroundColor;
	}

    public void clearAnswer()
    {   
    	bEnterPressedInTextField = false;
    	//answerPanel.remove(textField);
		//textField = newTextField();
		//answerPanel.add(textField);
    	textField.setText("");
		answerPanel.paint(answerPanel.getGraphics());
		//paint(this.getGraphics());
    }
    
    public JTextField getTextField()
    {
    	return textField;
    }
    
    private JTextField newTextField()
    {
    	JTextField tmp = new JTextField(20);
    	tmp.addKeyListener(new KeyAdapter()
    	{
	    	public void keyPressed(KeyEvent evt)
	    	{
	    	int iKey = evt.getKeyCode();
	    	bEnterPressedInTextField = false;
	    	if (iKey == KeyEvent.VK_ENTER)
	    		bEnterPressedInTextField = true;
	    	}
	    });
    	return tmp;
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

        s = doc.addStyle("icon", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon pigIcon = createImageIcon("images/Pig.gif",
                                            "a cute pig");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }

        s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon soundIcon = createImageIcon("images/sound.gif",
                                              "sound icon");
        JButton button = new JButton();
        if (soundIcon != null) {
            button.setIcon(soundIcon);
        } else {
            button.setText("BEEP");
        }
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.setActionCommand("jbutton");
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = TextSamplerDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private JTextPane createTextPane() {
        String[] initString =
                { "This is an editable JTextPane, ",            //regular
                  "another ",                                   //italic
                  "styled ",                                    //bold
                  "text ",                                      //small
                  "component, ",                                //large
                  "which supports embedded components..." + newline,//regular
                  " " + newline,                                //button
                  "...and embedded icons..." + newline,         //regular
                  " ",                                          //icon
                  newline + "JTextPane is a subclass of JEditorPane that " +
                    "uses a StyledEditorKit and StyledDocument, and provides " +
                    "cover methods for interacting with those objects."
                 };

        String[] initStyles =
                { "regular", "italic", "bold", "small", "large",
                  "regular", "button", "regular", "icon",
                  "regular"
                };

        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);

        try {
            for (int i=0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i],
                                 doc.getStyle(initStyles[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }

        return textPane;
    }

}
