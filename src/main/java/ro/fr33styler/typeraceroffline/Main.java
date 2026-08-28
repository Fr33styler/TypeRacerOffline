package ro.fr33styler.typeraceroffline;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {

        URL url = Main.class.getClassLoader().getResource("Text");
        if (url == null) return;

        Session session = new Session();

        InputStream inputStream = url.openConnection().getInputStream();

        InputStreamReader reader = new InputStreamReader(inputStream);

        StringBuilder result = new StringBuilder();
        char[] buffer = new char[8192];

        int charsRead;
        while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
            result.append(buffer, 0, charsRead);
        }
        String[] lines = result.toString().split("(\\n|\\r|\\r\\n)");

        System.setProperty("sun.java2d.noddraw", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("TypeRacerOffline");

        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();

        JButton button = new JButton("Generate Text");

        topPanel.add(button);

        JTextField wpmField = new JTextField("WPM: 0", 10);
        wpmField.setEditable(false);

        topPanel.add(new JLabel("Type Here:"));

        JTextField typeField = new JTextField("", 20);
        typeField.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextField typedWordsField = new JTextField("Typed: 0", 10);
        typedWordsField.setEditable(false);

        topPanel.add(typeField);

        JPanel centerPanel = new JPanel();

        JScrollPane bodyScrollablePane = new JScrollPane();
        bodyScrollablePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bodyScrollablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JTextArea bodyLogArea = new JTextArea(20, 80);
        bodyLogArea.setEditable(false);
        bodyLogArea.setLineWrap(true);
        bodyLogArea.setWrapStyleWord(true);
        bodyLogArea.setFont(new Font("Arial", Font.PLAIN, 16));
        Highlighter highlighter = new DefaultHighlighter();

        bodyLogArea.setHighlighter(highlighter);

        bodyScrollablePane.getViewport().setView(bodyLogArea);
        centerPanel.add(bodyScrollablePane);

        DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY);

        button.addActionListener(action -> {
            Random random = ThreadLocalRandom.current();

            String text = lines[random.nextInt(lines.length)];

            bodyLogArea.setText(text);
            session.getWords().clear();
            session.getWords().addAll(Arrays.asList(text.split(" ")));

            session.setIndex(0);

            wpmField.setText("WPM: 0");
            session.setTypedWords(0);
            session.setLastTypedWords(0);
            session.setSumOfDeltaTypedWords(0);
            session.setLengthOfDeltaTypedWords(0);
            typedWordsField.setText("Typed: 0");
            session.setTypedLetters(0);
            try {
                session.setHighlight(highlighter.addHighlight(0, session.getWords().get(0).length(), highlightPainter));
            } catch (BadLocationException exception) {
                System.out.println("Out of bounds!");
            }
        });

        typeField.addActionListener(action -> {
            int index = session.getIndex();
            List<String> words = session.getWords();

            if (session.getIndex() < words.size() && words.get(index).equals(action.getActionCommand())) {
                session.setIndex(index + 1);
                typeField.setText("");
            }
        });

        typeField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent event) {
                if (event.getKeyChar() == ' ') {
                    event.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent event) {}

            @Override
            public void keyReleased(KeyEvent event) {
                if (typeField.getText().equals(session.getWordAt(session.getIndex()))) {

                    session.setIndex(session.getIndex() + 1);
                    session.setTypedWords(session.getTypedWords() + 1);
                    typedWordsField.setText("Typed: " + session.getTypedWords());
                    session.setTypedLetters(session.getTypedLetters() + typeField.getText().length() + 1);

                    try {
                        highlighter.removeHighlight(session.getHighlight());
                        session.setHighlight(highlighter.addHighlight(session.getTypedLetters(),
                                session.getTypedLetters() + session.getWordAt(session.getIndex()).length(), highlightPainter));
                    } catch (BadLocationException exception) {
                        System.out.println("Out of bounds!");
                    }
                    typeField.setText("");
                }
            }
        });

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        executorService.scheduleAtFixedRate(() -> {
            if (session.getWords().size() < session.getTypedWords() && session.getWords().size() <= session.getIndex()) return;

            int typedWords = session.getTypedWords();

            session.setSumOfDeltaTypedWords(session.getSumOfDeltaTypedWords() + typedWords - session.getLastTypedWords());
            session.setLengthOfDeltaTypedWords(session.getLengthOfDeltaTypedWords() + 1);

            if (session.getLastTypedWords() > 0) {
                wpmField.setText("WPM: " + Math.round(session.getSumOfDeltaTypedWords() / session.getLengthOfDeltaTypedWords() * 60));
            }

            session.setLastTypedWords(typedWords);
        }, 0, 1, TimeUnit.SECONDS);

        topPanel.add(wpmField);
        topPanel.add(typedWordsField);

        frame.add(topPanel, BorderLayout.PAGE_START);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

}
