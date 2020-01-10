/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide;

//
// A simple Java Console for your application (Swing version)
// Requires Java 1.1.5 or higher
//
// Disclaimer the use of this source is at your own risk.
//
// Permision to use and distribute into your own applications
//
// RJHM van den Bergh , rvdb@comweb.nl
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import org.sikuli.script.Debug;
import org.sikuli.script.SikuliScript;

public class EditorConsolePane extends JPanel implements Runnable {

  static boolean ENABLE_IO_REDIRECT = true;

  static {
    String flag = System.getProperty("sikuli.console");
    if (flag != null && flag.equals("false")) {
      ENABLE_IO_REDIRECT = false;
    }
  }
  final static int NUM_PIPES = 2;
  private JTextPane textArea;
  private Thread[] reader = new Thread[NUM_PIPES];
  private boolean quit;
  private final PipedInputStream[] pin = new PipedInputStream[NUM_PIPES];
  Thread errorThrower; // just for testing (Throws an Exception at this Console

  public EditorConsolePane() {
    super();
    textArea = new JTextPane();
    textArea.setEditorKit(new HTMLEditorKit());
    textArea.setTransferHandler(new JTextPaneHTMLTransferHandler());
    String css = PreferencesUser.getInstance().getConsoleCSS();
    ((HTMLEditorKit) textArea.getEditorKit()).getStyleSheet().addRule(css);
    textArea.setEditable(false);

    setLayout(new BorderLayout());
    add(new JScrollPane(textArea), BorderLayout.CENTER);

    if (ENABLE_IO_REDIRECT) {
      for (int i = 0; i < NUM_PIPES; i++) {
        pin[i] = new PipedInputStream();
      }

      if (SikuliScript.getScriptRunner("jython", null, null).doSomethingSpecial("redirect", pin)) {
        Debug.log(2, "EditorConsolePane: init: stdout/stderr redirected to console");
        quit = false; // signals the Threads that they should exit

        // Starting two seperate threads to read from the PipedInputStreams
        for (int i = 0; i < NUM_PIPES; i++) {
          reader[i] = new Thread(this);
          reader[i].setDaemon(true);
          reader[i].start();
        }
      } else {
        Debug.error("EditorConsolePane: init: Redirect to console not posssible");
      }

    }

  }

  private void appendMsg(String msg) {
    HTMLDocument doc = (HTMLDocument) textArea.getDocument();
    HTMLEditorKit kit = (HTMLEditorKit) textArea.getEditorKit();
    try {
      kit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /*
   public synchronized void windowClosed(WindowEvent evt)
   {
   quit=true;
   this.notifyAll(); // stop all threads
   try { reader.join(1000);pin.close();   } catch (Exception e){}
   try { reader2.join(1000);pin2.close(); } catch (Exception e){}
   System.exit(0);
   }

   public synchronized void windowClosing(WindowEvent evt)
   {
   frame.setVisible(false); // default behaviour of JFrame
   frame.dispose();
   }
   */
  static final String lineSep = System.getProperty("line.separator");

  private String htmlize(String msg) {
    StringBuilder sb = new StringBuilder();
    Pattern patMsgCat = Pattern.compile("\\[(.+?)\\].*");
    for (String line : msg.split(lineSep)) {
      Matcher m = patMsgCat.matcher(line);
      String cls = "normal";
      if (m.matches()) {
        cls = m.group(1);
      }
      line = "<span class='" + cls + "'>" + line + "</span>";
      sb.append(line).append("<br>");
    }
    return sb.toString();
  }

  @Override
  public synchronized void run() {
    try {
      for (int i = 0; i < NUM_PIPES; i++) {
        while (Thread.currentThread() == reader[i]) {
          try {
            this.wait(100);
          } catch (InterruptedException ie) {
          }
          if (pin[i].available() != 0) {
            String input = this.readLine(pin[i]);
            appendMsg(htmlize(input));
            textArea.setCaretPosition(textArea.getDocument().getLength() - 1);
          }
          if (quit) {
            return;
          }
        }
      }

    } catch (Exception e) {
      appendMsg("\nConsole reports an Internal error.");
      appendMsg("The error is: " + e);
    }

  }

  public synchronized String readLine(PipedInputStream in) throws IOException {
    String input = "";
    do {
      int available = in.available();
      if (available == 0) {
        break;
      }
      byte b[] = new byte[available];
      in.read(b);
      input = input + new String(b, 0, b.length);
    } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
    return input;
  }

  public void clear() {
    textArea.setText("");
  }
}

class JTextPaneHTMLTransferHandler extends TransferHandler {

  public JTextPaneHTMLTransferHandler() {
  }

  @Override
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    super.exportToClipboard(comp, clip, action);
  }

  @Override
  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    JTextPane aTextPane = (JTextPane) c;

    HTMLEditorKit kit = ((HTMLEditorKit) aTextPane.getEditorKit());
    StyledDocument sdoc = aTextPane.getStyledDocument();
    int sel_start = aTextPane.getSelectionStart();
    int sel_end = aTextPane.getSelectionEnd();

    int i = sel_start;
    StringBuilder output = new StringBuilder();
    while (i < sel_end) {
      Element e = sdoc.getCharacterElement(i);
      Object nameAttr = e.getAttributes().getAttribute(StyleConstants.NameAttribute);
      int start = e.getStartOffset(), end = e.getEndOffset();
      if (nameAttr == HTML.Tag.BR) {
        output.append("\n");
      } else if (nameAttr == HTML.Tag.CONTENT) {
        if (start < sel_start) {
          start = sel_start;
        }
        if (end > sel_end) {
          end = sel_end;
        }
        try {
          String str = sdoc.getText(start, end - start);
          output.append(str);
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        }
      }
      i = end;
    }

    return new StringSelection(output.toString());
  }
}
