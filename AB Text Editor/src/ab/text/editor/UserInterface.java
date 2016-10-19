package ab.text.editor;

import com.inet.jortho.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class UserInterface extends javax.swing.JFrame {

    //Highlighter and painter for Find
    DefaultHighlighter highlighter;
    DefaultHighlightPainter painter;

    //Configuration from teacher configuration program
    Configuration config;

    // Option for find
    boolean findOptionSet;
    boolean textModified;

    //Constructor
    public UserInterface() {

        textModified = false;
        initComponents();
        initializeConfiguration();
        initializeActionListeners();
        initializeHighlighter();

    }

    // Begin methods for functionalities, to be called by action listeners
    private void doNew() {
        if (textModified) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you"
                    + " want to make a new file without saving?",
                    "Changes not saved", JOptionPane.INFORMATION_MESSAGE);
            if (result == 0) {
                stage.setText("");
                countWords();
            } else if (result == 1) {
                doSave();
            }
        }
    }

    private void doOpen() {
        if (textModified) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you"
                    + " want to open a new \nfile without saving"
                    + " your changes?",
                    "Changes not saved", JOptionPane.INFORMATION_MESSAGE);
            if (result == 0) {
                Open fileOpen = new Open();
                fileOpen.openFile(UserInterface.this);
                stage.setText(fileOpen.fileText);
                countWords();
            } else if (result == 1) {
                doSave();
            }
        } else {
            Open fileOpen = new Open();
            fileOpen.openFile(UserInterface.this);
            stage.setText(fileOpen.fileText);
            countWords();
        }
    }

    private void doSave() {
        Save fileSave = new Save();
        fileSave.saveFile(UserInterface.this, stage.getText(), config.saveMethodSelected);
        countWords();
        textModified = false;
    }

    private void doCut() {
        Clipboarder cutText = new Clipboarder();
        cutText.toClipboard(stage.getSelectedText());
        stage.replaceSelection("");
        countWords();
    }

    private void doCopy() {
        Clipboarder copyText = new Clipboarder();
        copyText.toClipboard(stage.getSelectedText());
        System.out.println("Copy");
    }

    private void doPaste() {
        Clipboarder pasteText = new Clipboarder();
        String text = pasteText.fromClipboard();
        stage.replaceSelection("");
        int position = stage.getCaretPosition();
        stage.insert(text, position);
        countWords();
    }

    private void doFind() {
        String findString = JOptionPane.showInputDialog("Enter the word you would like to find: ");
        try {
            String stageText = stage.getText();
            int findStringIndex = 0;
            int foundCount = 0;
            for (int i = 0; i < stageText.length(); i++) {
                if (stageText.charAt(i) == findString.charAt(findStringIndex)) {
                    findStringIndex++;
                    if (findStringIndex == findString.length()) {
                        foundCount++;
                        findStringIndex = 0;
                        highlighter.addHighlight(i - findString.length() + 1, i + 1, painter);
                    }
                } else {
                    findStringIndex = 0;
                }
            }
            JOptionPane.showMessageDialog(rootPane, "The word " + findString
                    + " was found " + foundCount + " times.");
            if (foundCount > 0) {
                findOptionSet = true;
                swapFindButton();
            }
        } catch (NullPointerException | BadLocationException | StringIndexOutOfBoundsException e) {
        }

    }

    private void swapFindButton() {
        if (findOptionSet) {
            findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/exitFind.png")));
            findButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/exitFindClicked.png")));
            findButton.setText("Exit Find");
        } else {
            findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/find.png")));
            findButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/findClicked.png")));
            findButton.setText("Find");
        }
    }

    private void doPrint() {
        Print print = new Print(stage);
        print.doPrinting();
    }

    private void doExit() {
        if (textModified) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you"
                    + " want to close without saving?",
                    "Changes not saved", JOptionPane.INFORMATION_MESSAGE);
            if (result == 0) {
                this.dispose();
            } else if (result == 1) {
                doSave();
                this.dispose();
            }
        } else {
            this.dispose();
        }
    }

    protected void countWords() {

        String s = stage.getText();

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {

            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;

            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;

            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }

        wordCountLabel.setText("Word Count:     " + wordCount);

    }

    //End methods for functionalities
    // Initilization methods
    private void initializeActionListeners() {

        newButton.addActionListener((ActionEvent e) -> {
            doNew();
        });
        openButton.addActionListener((ActionEvent e) -> {
            doOpen();
        });
        saveButton.addActionListener((ActionEvent e) -> {
            doSave();
        });
        cutButton.addActionListener((ActionEvent e) -> {
            doCut();
        });
        copyButton.addActionListener((ActionEvent e) -> {
            doCopy();
        });
        pasteButton.addActionListener((ActionEvent e) -> {
            doPaste();
        });
        findButton.addActionListener((ActionEvent e) -> {
            if (findOptionSet) {
                findOptionSet = false;
                highlighter.removeAllHighlights();
                swapFindButton();
            } else {
                doFind();
            }
        });
        printButton.addActionListener((ActionEvent e) -> {
            doPrint();
        });
        exitButton.addActionListener((ActionEvent e) -> {
            doExit();
        });

        StageListener keyListener = new StageListener(this);
        stage.addKeyListener(keyListener);

        if (config.spellCheckSelected) {
            initializeSpellCheck();
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                doExit();
            }
        });
    }

    private void initializeConfiguration() {
        config = new Configuration();
        removeButtons();
    }

    // Removes buttons according to teacher configuration
    private void removeButtons() {
        if (!config.newSelected) {
            jPanel1.remove(newButton);
        }
        if (!config.openSelected) {
            jPanel1.remove(openButton);
        }
        if (!config.saveSelected) {
            jPanel1.remove(saveButton);
        }
        if (!config.cutSelected) {
            jPanel1.remove(cutButton);
        }
        if (!config.copySelected) {
            jPanel1.remove(copyButton);
        }
        if (!config.pasteSelected) {
            jPanel1.remove(pasteButton);
        }
        if (!config.findSelected) {
            jPanel1.remove(findButton);
        }
        if (!config.printSelected) {
            jPanel1.remove(printButton);
        }
        if (!config.exitSelected) {
            jPanel1.remove(exitButton);
        }

        jPanel1.revalidate();
        validate();
        repaint();
    }

    private void initializeHighlighter() {
        highlighter = new DefaultHighlighter();
        painter = new DefaultHighlightPainter(Color.pink);

        findOptionSet = false;
        stage.setHighlighter(highlighter);
    }

    private void initializeSpellCheck() {
        //FILE LOCATION OF DICTIONARY
        String userDictionaryPath = "/dictionary/";

        //SET DICTIONARY PROVIDER FROM DICTIONARY PATH
        SpellChecker.setUserDictionaryProvider(new FileUserDictionary(userDictionaryPath));

        //REGISTER DICTIONARY
        SpellChecker.registerDictionaries(getClass().getResource(userDictionaryPath), "en");

        SpellChecker.register(stage);
    }

    /**
     * Initialize the frame and its components
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cutButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stage = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        wordCountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("AB Text Editor");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));

        newButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/new.png"))); // NOI18N
        newButton.setText("New");
        newButton.setBorder(null);
        newButton.setBorderPainted(false);
        newButton.setContentAreaFilled(false);
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setIconTextGap(0);
        newButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/newClicked.png"))); // NOI18N
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        openButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/open.png"))); // NOI18N
        openButton.setText("Open");
        openButton.setBorder(null);
        openButton.setBorderPainted(false);
        openButton.setContentAreaFilled(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setIconTextGap(0);
        openButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/openClicked.png"))); // NOI18N
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        saveButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/save.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.setBorder(null);
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setIconTextGap(0);
        saveButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/saveClicked.png"))); // NOI18N
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        cutButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/cut.png"))); // NOI18N
        cutButton.setText("Cut");
        cutButton.setBorder(null);
        cutButton.setBorderPainted(false);
        cutButton.setContentAreaFilled(false);
        cutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cutButton.setIconTextGap(0);
        cutButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/cutClicked.png"))); // NOI18N
        cutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        copyButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/copy.png"))); // NOI18N
        copyButton.setText("Copy");
        copyButton.setBorder(null);
        copyButton.setBorderPainted(false);
        copyButton.setContentAreaFilled(false);
        copyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyButton.setIconTextGap(0);
        copyButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/copyClicked.png"))); // NOI18N
        copyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        pasteButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/paste.png"))); // NOI18N
        pasteButton.setText("Paste");
        pasteButton.setBorder(null);
        pasteButton.setBorderPainted(false);
        pasteButton.setContentAreaFilled(false);
        pasteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pasteButton.setIconTextGap(0);
        pasteButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/pasteClicked.png"))); // NOI18N
        pasteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        findButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/find.png"))); // NOI18N
        findButton.setText("Find");
        findButton.setToolTipText("");
        findButton.setBorder(null);
        findButton.setBorderPainted(false);
        findButton.setContentAreaFilled(false);
        findButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        findButton.setIconTextGap(0);
        findButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/findClicked.png"))); // NOI18N
        findButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        printButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/print.png"))); // NOI18N
        printButton.setText("Print");
        printButton.setToolTipText("");
        printButton.setBorder(null);
        printButton.setBorderPainted(false);
        printButton.setContentAreaFilled(false);
        printButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printButton.setIconTextGap(0);
        printButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/printclicked.png"))); // NOI18N
        printButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        exitButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/exit.png"))); // NOI18N
        exitButton.setText("Exit");
        exitButton.setToolTipText("");
        exitButton.setBorder(null);
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setIconTextGap(0);
        exitButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/ab/text/editor/exitClicked.png"))); // NOI18N
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(newButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cutButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(copyButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pasteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(findButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton)
                        .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                .addComponent(pasteButton)
                                .addComponent(copyButton)
                                .addComponent(cutButton)
                                .addComponent(newButton)
                                .addComponent(openButton)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(findButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(printButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(exitButton, javax.swing.GroupLayout.Alignment.TRAILING)))))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);

        stage.setColumns(20);
        stage.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        stage.setLineWrap(true);
        stage.setRows(5);
        stage.setBorder(null);
        stage.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jScrollPane1.setViewportView(stage);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(238, 238, 238));
        jPanel4.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N

        wordCountLabel.setBackground(new java.awt.Color(238, 238, 238));
        wordCountLabel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        wordCountLabel.setText("Word Count:     0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wordCountLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(wordCountLabel)
                        .addGap(0, 10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    // Swing components                 
    private javax.swing.JButton copyButton;
    private javax.swing.JButton cutButton;
    private javax.swing.JButton findButton;
    private javax.swing.JButton printButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton openButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextArea stage;
    private javax.swing.JLabel wordCountLabel;

}
