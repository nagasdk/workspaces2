/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.*;
import org.sikuli.ide.indentation.PythonIndentation;
import org.sikuli.script.Debug;

public class EditorKit extends StyledEditorKit {

  private ViewFactory _viewFactory;

  public EditorKit() {
    _viewFactory = new EditorViewFactory();
  }
  public static final String deIndentAction = "SKL.DeindentAction";
  private static final TextAction[] defaultActions = {
    new InsertTabAction(),
    new DeindentAction(),
    new InsertBreakAction(),
    new NextVisualPositionAction(forwardAction, false, SwingConstants.EAST),
    new NextVisualPositionAction(backwardAction, false, SwingConstants.WEST),
    new NextVisualPositionAction(selectionForwardAction, true, SwingConstants.EAST),
    new NextVisualPositionAction(selectionBackwardAction, true, SwingConstants.WEST),
    new NextVisualPositionAction(upAction, false, SwingConstants.NORTH),
    new NextVisualPositionAction(downAction, false, SwingConstants.SOUTH),
    new NextVisualPositionAction(selectionUpAction, true, SwingConstants.NORTH),
    new NextVisualPositionAction(selectionDownAction, true, SwingConstants.SOUTH),};

  public static class InsertTabAction extends TextAction {

    private  PythonIndentation indentationLogic;

    public InsertTabAction() {
      super(insertTabAction);
    }

    public InsertTabAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Debug.log(5, "InsertTabAction " + e);
      JTextComponent text = (JTextComponent) e.getSource();
      actionPerformed(text);
    }

    public void actionPerformed(JTextComponent text) {
      indentationLogic = ((EditorPane) text).getIndentationLogic();
      boolean indentError = false;
      Document doc = text.getDocument();
      Element map = doc.getDefaultRootElement();
      String tabWhitespace = PreferencesUser.getInstance().getTabWhitespace();
      Caret c = text.getCaret();
      int dot = c.getDot();
      int mark = c.getMark();
      int dotLine = map.getElementIndex(dot);
      int markLine = map.getElementIndex(mark);

      if (dotLine != markLine) {
        int first = Math.min(dotLine, markLine);
        int last = Math.max(dotLine, markLine);
        Element elem;
        int start;
        try {
          for (int i = first; i < last; i++) {
            elem = map.getElement(i);
            start = elem.getStartOffset();
            doc.insertString(start, tabWhitespace, null);
          }
          elem = map.getElement(last);
          start = elem.getStartOffset();
          if (Math.max(c.getDot(), c.getMark()) != start) {
            doc.insertString(start, tabWhitespace, null);
          }
        } catch (BadLocationException ble) {
          ble.printStackTrace();
          UIManager.getLookAndFeel().provideErrorFeedback(text);
        }
      } else {
        text.replaceSelection(tabWhitespace);
      }
    }
  }

  public static class DeindentAction extends TextAction {
//TODO dedent not working consistently on last line (no last empty line)

    private Segment segLine;
    private  PythonIndentation indentationLogic;

    public DeindentAction() {
      this(deIndentAction);
    }

    public DeindentAction(String name) {
      super(name);
      segLine = new Segment();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Debug.log(5, "DedentAction " + e);
      JTextComponent text = (JTextComponent) e.getSource();
      actionPerformed(text);
    }

    public void actionPerformed(JTextComponent text) {
      indentationLogic = ((EditorPane) text).getIndentationLogic();
      StyledDocument doc = (StyledDocument) text.getDocument();
      Element map = doc.getDefaultRootElement();
      Caret c = text.getCaret();
      int dot = c.getDot();
      int mark = c.getMark();
      int line1 = map.getElementIndex(dot);

      if (dot != mark) {
        int line2 = map.getElementIndex(mark);
        int begin = Math.min(line1, line2);
        int end = Math.max(line1, line2);
        Element elem;
        try {
          for (line1 = begin; line1 < end; line1++) {
            elem = map.getElement(line1);
            handleDecreaseIndent(line1, elem, doc);
          }
          elem = map.getElement(end);
          int start = elem.getStartOffset();
          if (Math.max(c.getDot(), c.getMark()) != start) {
            handleDecreaseIndent(end, elem, doc);
          }
        } catch (BadLocationException ble) {
          ble.printStackTrace();
          UIManager.getLookAndFeel().provideErrorFeedback(text);
        }
      } else {
        Element elem = map.getElement(line1);
        try {
          handleDecreaseIndent(line1, elem, doc);
        } catch (BadLocationException ble) {
          ble.printStackTrace();
          UIManager.getLookAndFeel().provideErrorFeedback(text);
        }
      }

    }

    private void handleDecreaseIndent(int line, Element elem, StyledDocument doc)
            throws BadLocationException {
      int start = elem.getStartOffset();
      int end = elem.getEndOffset() - 1;
      doc.getText(start, end - start, segLine);
      int i = segLine.offset;
      end = i + segLine.count;
      if (end > i) {
        String leadingWS = PythonIndentation.getLeadingWhitespace(doc, start, end - start);
        int toRemove = indentationLogic.checkDedent(leadingWS, line+1);
        doc.remove(start, toRemove);
      }
    }
  }

  public static class InsertBreakAction extends TextAction {

    private  PythonIndentation indentationLogic;

    public InsertBreakAction() {
      super(insertBreakAction);
    }

    public InsertBreakAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Debug.log(5, "InsertBreakAction " + e);
      JTextComponent text = (JTextComponent) e.getSource();
      insertBreak(text);
    }

    public void insertBreak(JTextComponent text) {
      indentationLogic = ((EditorPane) text).getIndentationLogic();
      boolean noSelection = text.getSelectionStart() == text.getSelectionEnd();
      if (noSelection) {
        insertNewlineWithAutoIndent(text);
      } else {
        text.replaceSelection("\n");
//TODO insertNewlineWithAutoIndent
      }
    }

    private void insertNewlineWithAutoIndent(JTextComponent text) {
      try {
        int caretPos = text.getCaretPosition();
        StyledDocument doc = (StyledDocument) text.getDocument();
        Element map = doc.getDefaultRootElement();
        int lineNum = map.getElementIndex(caretPos);
        Element line = map.getElement(lineNum);
        int start = line.getStartOffset();
        int end = line.getEndOffset() - 1;
        int len = end - start;
        String s = doc.getText(start, len);

        String leadingWS = PythonIndentation.getLeadingWhitespace(doc, start, caretPos - start);
        StringBuffer sb = new StringBuffer("\n");
        sb.append(leadingWS);
//TODO better control over automatic indentation
        indentationLogic.checkIndent(leadingWS, lineNum+1);

        // If there is only whitespace between the caret and
        // the EOL, pressing Enter auto-indents the new line to
        // the same place as the previous line.
        int nonWhitespacePos = PythonIndentation.atEndOfLine(doc, caretPos, start, s, len);
        if (nonWhitespacePos == -1) {
          if (leadingWS.length() == len) {
            // If the line was nothing but whitespace, select it
            // so its contents get removed.
            text.setSelectionStart(start);
          } else {
            // Select the whitespace between the caret and the EOL
            // to remove it
            text.setSelectionStart(caretPos);
          }
          text.setSelectionEnd(end);
          text.replaceSelection(sb.toString());
          // auto-indentation for python statements like if, while, for, try,
          // except, def, class and auto-deindentation for break, continue,
          // pass, return
          analyseDocument(doc, lineNum, indentationLogic);
          // auto-completion: add colon if it is obvious
          if (indentationLogic.shouldAddColon()) {
            doc.insertString(caretPos, ":", null);
            indentationLogic.setLastLineEndsWithColon();
          }
          int lastLineChange = indentationLogic.shouldChangeLastLineIndentation();
          int nextLineChange = indentationLogic.shouldChangeNextLineIndentation();
          if (lastLineChange != 0) {
            Debug.log(5, "change line %d indentation by %d columns", lineNum + 1,
                    lastLineChange);
            changeIndentation((DefaultStyledDocument) doc, lineNum, lastLineChange);
            // nextLineChange was determined based on indentation of last line before
            // the change
            nextLineChange += lastLineChange;
          }
          if (nextLineChange != 0) {
            Debug.log(5, "change line %d indentation by %d columns", lineNum + 2,
                    nextLineChange);
            changeIndentation((DefaultStyledDocument) doc, lineNum + 1, nextLineChange);
          }
        } // If there is non-whitespace between the caret and the
        // EOL, pressing Enter takes that text to the next line
        // and auto-indents it to the same place as the last
        // line. Additional auto-indentation or dedentation for
        // specific python statements is only done for the next line.
        else {
          text.setCaretPosition(nonWhitespacePos);
          doc.insertString(nonWhitespacePos, sb.toString(), null);
          analyseDocument(doc, lineNum, indentationLogic);
          int nextLineChange = indentationLogic.shouldChangeNextLineIndentation();
          if (nextLineChange != 0) {
            Debug.log(5, "change line %d indentation by %d columns", lineNum + 2,
                    nextLineChange);
            changeIndentation((DefaultStyledDocument) doc, lineNum + 1, nextLineChange);
          }
        }

      } catch (BadLocationException ble) {
        text.replaceSelection("\n");
        ble.printStackTrace();
      }

    }

    private void analyseDocument(Document document, int lineNum,
            PythonIndentation indentationLogic) throws BadLocationException {
      Element map = document.getDefaultRootElement();
      int endPos = map.getElement(lineNum).getEndOffset();
      indentationLogic.reset();
      indentationLogic.addText(document.getText(0, endPos));
    }

    /**
     * Change the indentation of a line. Any existing leading whitespace is replaced by the
     * appropriate number of tab characters (padded with blank characters if necessary) if tab
     * expansion in the user preferences is true, or the appropriate number of blank characters if
     * tab expansion is false.
     *
     * @param linenum the line number (0-based)
     * @param columns the number of columns by which to increase the indentation (if columns is
     * greater than 0) or decrease the indentation (if columns is less than 0)
     * @throws BadLocationException if the specified line does not exist
     */
    // TODO: make this a method of SikuliDocument, no need to pass document as argument
    private void changeIndentation(DefaultStyledDocument doc, int linenum,
            int columns) throws BadLocationException {
      PreferencesUser pref = PreferencesUser.getInstance();
      boolean expandTab = pref.getExpandTab();
      int tabWidth = pref.getTabWidth();

      if (linenum < 0) {
        throw new BadLocationException("Negative line", -1);
      }
      Element map = doc.getDefaultRootElement();
      if (linenum >= map.getElementCount()) {
        throw new BadLocationException("No such line", doc.getLength() + 1);
      }
      if (columns == 0) {
        return;
      }

      Element lineElem = map.getElement(linenum);
      int lineStart = lineElem.getStartOffset();
      int lineLength = lineElem.getEndOffset() - lineStart;
      String line = doc.getText(lineStart, lineLength);

      // determine current indentation and number of whitespace characters
      int wsChars;
      int indentation = 0;
      for (wsChars = 0; wsChars < line.length(); wsChars++) {
        char c = line.charAt(wsChars);
        if (c == ' ') {
          indentation++;
        } else if (c == '\t') {
          indentation += tabWidth;
        } else {
          break;
        }
      }

      int newIndentation = indentation + columns;
      if (newIndentation <= 0) {
        doc.remove(lineStart, wsChars);
        return;
      }

      // build whitespace string for new indentation
      StringBuilder newWs = new StringBuilder(newIndentation / tabWidth + tabWidth - 1);
      int ind = 0;
      if (!expandTab) {
        for (; ind + tabWidth <= newIndentation; ind += tabWidth) {
          newWs.append('\t');
        }
      }
      for (; ind < newIndentation; ind++) {
        newWs.append(' ');
      }
      doc.replace(lineStart, wsChars, newWs.toString(), null);
    }
  }

  private static class NextVisualPositionAction extends TextAction {

    private boolean select;
    private int direction;

    NextVisualPositionAction(String nm, boolean select, int dir) {
//TODO forward selection space+image - space not selected alone
//TODO up/down might step left or right
      super(nm);
      this.select = select;
      this.direction = dir;
    }

    private static int getNSVisualPosition(EditorPane txt, int pos, int direction) {
      Element root = txt.getDocument().getDefaultRootElement();
      int numLines = root.getElementIndex(txt.getDocument().getLength() - 1) + 1;
      int line = root.getElementIndex(pos) + 1;
      int tarLine = direction == SwingConstants.NORTH ? line - 1 : line + 1;
      try {
        if (tarLine <= 0) {
          return 0;
        }
        if (tarLine > numLines) {
          return txt.getDocument().getLength();
        }

        Rectangle curRect = txt.modelToView(pos);
        Rectangle tarEndRect;
        if (tarLine < numLines) {
          tarEndRect = txt.modelToView(txt.getLineStartOffset(tarLine) - 1);
        } else {
          tarEndRect = txt.modelToView(txt.getDocument().getLength() - 1);
        }
        Debug.log(9, "curRect: " + curRect + ", tarEnd: " + tarEndRect);

        if (curRect.x > tarEndRect.x) {
          pos = txt.viewToModel(new Point(tarEndRect.x, tarEndRect.y));
        } else {
          pos = txt.viewToModel(new Point(curRect.x, tarEndRect.y));
        }
      } catch (BadLocationException e) {
        e.printStackTrace();
      }

      return pos;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent textArea = (JTextComponent) e.getSource();

      Caret caret = textArea.getCaret();
      int dot = caret.getDot();

      /*
       * Move to the beginning/end of selection on a "non-shifted"
       * left- or right-keypress.  We shouldn't have to worry about
       * navigation filters as, if one is being used, it let us get
       * to that position before.
       */
      if (!select) {
        switch (direction) {
          case SwingConstants.EAST:
            int mark = caret.getMark();
            if (dot != mark) {
              caret.setDot(Math.max(dot, mark));
              return;
            }
            break;
          case SwingConstants.WEST:
            mark = caret.getMark();
            if (dot != mark) {
              caret.setDot(Math.min(dot, mark));
              return;
            }
            break;
          default:
        }
      }

      Position.Bias[] bias = new Position.Bias[1];
      Point magicPosition = caret.getMagicCaretPosition();

      try {

        if (magicPosition == null
                && (direction == SwingConstants.NORTH
                || direction == SwingConstants.SOUTH)) {
          Rectangle r = textArea.modelToView(dot);
          magicPosition = new Point(r.x, r.y);
        }

        NavigationFilter filter = textArea.getNavigationFilter();

        if (filter != null) {
          dot = filter.getNextVisualPositionFrom(textArea, dot,
                  Position.Bias.Forward, direction, bias);
        } else {
          if (direction == SwingConstants.NORTH
                  || direction == SwingConstants.SOUTH) {
            dot = getNSVisualPosition((EditorPane) textArea, dot, direction);
          } else {
            dot = textArea.getUI().getNextVisualPositionFrom(
                    textArea, dot,
                    Position.Bias.Forward, direction, bias);
          }
        }
        if (select) {
          caret.moveDot(dot);
        } else {
          caret.setDot(dot);
        }

        if (magicPosition != null
                && (direction == SwingConstants.NORTH
                || direction == SwingConstants.SOUTH)) {
          caret.setMagicCaretPosition(magicPosition);
        }

      } catch (BadLocationException ble) {
        ble.printStackTrace();
      }

    }
  }

//<editor-fold defaultstate="collapsed" desc="general support functions">
  @Override
  public Action[] getActions() {
    return TextAction.augmentList(super.getActions(), defaultActions);
  }

  @Override
  public ViewFactory getViewFactory() {
    return _viewFactory;
  }

  @Override
  public String getContentType() {
    //TODO other content type: ruby, DSL, ...
    return "text/python";
  }

  @Override
  public void read(Reader in, Document doc, int pos)
          throws IOException, BadLocationException {
    Debug.log(3, "SikuliEditorKit.read");
    super.read(in, doc, pos);
  }

  @Override
  public void write(Writer out, Document doc, int pos, int len)
          throws IOException, BadLocationException {
    write(out, doc, pos, len, null);
  }

  public void write(Writer out, Document doc, int pos, int len, Map<String, String> copiedImgs)
          throws IOException, BadLocationException {
    Debug.log(9, "SikuliEditorKit.write %d %d", pos, len);
    DefaultStyledDocument sdoc = (DefaultStyledDocument) doc;
    int i = pos;
    while (i < pos + len) {
      Element e = sdoc.getCharacterElement(i);
      int start = e.getStartOffset(), end = e.getEndOffset();
      if (e.getName().equals(StyleConstants.ComponentElementName)) {
        // A image argument to be filled
        AttributeSet attr = e.getAttributes();
        Component com = StyleConstants.getComponent(attr);
        out.write(com.toString());
        if (copiedImgs != null && com instanceof EditorPatternButton) {
          EditorPatternButton btn = (EditorPatternButton) com;
          String absPath = btn.getFilename();
          String fname = (new File(absPath)).getName();
          copiedImgs.put(fname, absPath);
          Debug.log(3, "save image for copy&paste: " + fname + " -> " + absPath);
        }
      } else {
        if (start < pos) {
          start = pos;
        }
        if (end > pos + len) {
          end = pos + len;
        }
        out.write(doc.getText(start, end - start));
      }
      i = end;
    }
    out.close();
  }
//</editor-fold>

}
