package gjarscan

import javax.swing.*
import javax.swing.text.DefaultHighlighter
import javax.swing.text.Highlighter
import java.awt.*

/**
 * Created by tk on 21.6.2014.
 */
class JarResult extends JTextArea {

    class JarResultHighlighterPainter extends DefaultHighlighter.DefaultHighlightPainter
    {

        /**
         * Constructs a new highlight painter. If <code>c</code> is null,
         * the JTextComponent will be queried for its selection color.
         *
         * @param c the color for the highlight
         */
        JarResultHighlighterPainter(Color c) {
            super(c)
        }
    }

    Highlighter.HighlightPainter highlightPainter = new JarResultHighlighterPainter(Color.GREEN)
    def highlightpositions = []
    def int currentposIndex = -1
    def int findLength = 0

    public void highligth(String pattern)
    {
        if (!pattern)
            return

        Highlighter hl = this.getHighlighter()
        String text = getText()
        String strText = text.toUpperCase()
        int pos = 0, patterLen = pattern.length()
        pattern = pattern.toUpperCase()
        highlightpositions.clear()
        currentposIndex = -1
        findLength = 0

        while ((pos = strText.indexOf(pattern, pos)) >= 0)
        {
            hl.addHighlight(pos, pos +patterLen, highlightPainter)
            highlightpositions.add pos
            pos += patterLen
            findLength = patterLen
        }
    }

    def void searchNextHighLightPosition()
    {
       def int next = nextPosition()
       setPosition(next)
    }

    def void searchPreviousHighLightPosition()
    {
        def int prev = prevPosition()
        setPosition(prev)
    }

    def void setPosition(int newpos)
    {
        if (newpos < 0)
            return
        requestFocusInWindow();

        Rectangle viewRect = modelToView(newpos);
        // Scroll to make the rectangle visible
        scrollRectToVisible(viewRect);
        // Highlight the text
        setCaretPosition(newpos + findLength);
        moveCaretPosition(newpos);
        // Move the search position beyond the current match
        // currentpos = newpos +findLength;
    }

    def public int nextPosition()
    {
        if (highlightpositions.size() == 0)
            return -1
        def int ret = currentposIndex +1
        def int max = highlightpositions.size()
        if (ret < max)
        {
            currentposIndex = ret
        }
        else
        {
            currentposIndex = 0
            ret = currentposIndex
        }

        return highlightpositions.toArray()[ret]
    }

    def public int prevPosition()
    {
        if (highlightpositions.size() == 0)
            return -1
        def int ret = currentposIndex -1
        def int max = highlightpositions.size()
        if (ret > -1)
        {
            currentposIndex = ret
        }
        else
        {
            currentposIndex = max -1
            ret = currentposIndex
        }

        return highlightpositions.toArray()[ret]
    }
}
