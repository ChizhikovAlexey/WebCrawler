package crawler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintWriter;

import static crawler.LinksAndTitles.linksAndTitles;
import static javax.swing.JOptionPane.showMessageDialog;


public class WebCrawler extends JFrame {

    private Crawler[] crawlers = null;
    private Thread[] threads = null;
    private static int linksProcessed = 0;
    private static JLabel linksCounter = null;
    private JToggleButton runButton = null;

    public WebCrawler() {
        final int WIN_WIDTH = 720;
        final int WIN_HEIGHT = 360;
        final int PADDING = 10;
        final int BOTTOM_PADDING = 40;
        final int ELEM_HEIGHT = 35;
        final int BUTTON_WIDTH = 100;
        final int LABEL_WIDTH = 60;
        final int COUNTER_WIDTH = 120;
        final int COUNTER_PADDING = 2 * PADDING + COUNTER_WIDTH;
        final int FIELD_WIDTH = WIN_WIDTH - 4 * PADDING - BUTTON_WIDTH - LABEL_WIDTH;
        final int FIELD_PADDING = 2 * PADDING + LABEL_WIDTH;
        final int BUTTON_PADDING = FIELD_PADDING + FIELD_WIDTH + PADDING;


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setTitle("Web Crawler");


        //DEPTH
        JLabel depthLabel = new JLabel("Depth:");
        depthLabel.setBounds(PADDING, 2 * PADDING + ELEM_HEIGHT,
                LABEL_WIDTH, ELEM_HEIGHT);
        add(depthLabel);

        JTextField depthTextField = new JTextField("5");
        depthTextField.setBounds(FIELD_PADDING, 2 * PADDING + ELEM_HEIGHT,
                FIELD_WIDTH, ELEM_HEIGHT);
        depthTextField.setName("DepthTextField");
        depthTextField.setEnabled(true);
        add(depthTextField);

        JCheckBox depthCheckbox = new JCheckBox("Enabled");
        depthCheckbox.setBounds(BUTTON_PADDING, 2 * PADDING + ELEM_HEIGHT,
                BUTTON_WIDTH, ELEM_HEIGHT);
        depthCheckbox.setSelected(true);
        depthCheckbox.setName("DepthCheckBox");
        add(depthCheckbox);


        //Threads
        JLabel threadsLabel = new JLabel("Threads:");
        threadsLabel.setBounds(PADDING, 3 * PADDING + 2 * ELEM_HEIGHT,
                LABEL_WIDTH, ELEM_HEIGHT);
        add(threadsLabel);

        JTextField threadsTextField = new JTextField("1");
        threadsTextField.setBounds(FIELD_PADDING, 3 * PADDING + 2 * ELEM_HEIGHT,
                FIELD_WIDTH, ELEM_HEIGHT);
        threadsTextField.setName("ThreadsTextField");
        threadsTextField.setEnabled(true);
        add(threadsTextField);

        //COUNTERS
        JLabel linksCounterLabel = new JLabel("Links processed:");
        linksCounterLabel.setBounds(PADDING, 4 * PADDING + 3 * ELEM_HEIGHT,
                COUNTER_WIDTH, ELEM_HEIGHT);
        add(linksCounterLabel);

        linksCounter = new JLabel("0");
        linksCounter.setBounds(COUNTER_PADDING, 4 * PADDING + 3 * ELEM_HEIGHT,
                COUNTER_WIDTH, ELEM_HEIGHT);
        linksCounter.setName("ParsedLabel");
        add(linksCounter);


        //CRAWL
        Crawler[] crawlers;
        Thread[] threads;

        JLabel URLLabel = new JLabel("URL:");
        URLLabel.setBounds(PADDING, PADDING,
                LABEL_WIDTH, ELEM_HEIGHT);
        add(URLLabel);

        JTextField URLField = new JTextField("https://");
        URLField.setBounds(FIELD_PADDING, PADDING,
                FIELD_WIDTH, ELEM_HEIGHT);
        URLField.setName("UrlTextField");
        URLField.setEnabled(true);
        add(URLField);

        runButton = new JToggleButton("Start");
        runButton.setBounds(BUTTON_PADDING, PADDING,
                BUTTON_WIDTH, ELEM_HEIGHT);
        runButton.setName("RunButton");
        ItemListener itemListener = itemEvent -> {
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
                runButton.setText("Stop");
                try {
                    startCrawlers(URLField.getText(), Integer.parseInt(threadsTextField.getText()),
                            depthCheckbox.isSelected()? Integer.parseInt(depthTextField.getText()) : Integer.MAX_VALUE);
                } catch (IOException e) {
                    showMessageDialog(null, "Bad URL address!");
                }
            } else {
                runButton.setText("Start");
                stopCrawlers();
            }
        };
        // Attach Listeners
        runButton.addItemListener(itemListener);
        add(runButton);


        //EXPORT TO FILE
        ///Users/aleksejcizikov/Desktop/test.txt
        JLabel ExportLabel = new JLabel("Export:");
        ExportLabel.setBounds(PADDING, WIN_HEIGHT - BOTTOM_PADDING - ELEM_HEIGHT,
                LABEL_WIDTH, ELEM_HEIGHT);
        add(ExportLabel);

        JTextField fileField = new JTextField("./output.txt");
        fileField.setBounds(FIELD_PADDING, WIN_HEIGHT - BOTTOM_PADDING - ELEM_HEIGHT,
                FIELD_WIDTH, ELEM_HEIGHT);
        fileField.setName("ExportUrlTextField");
        fileField.setEnabled(true);
        add(fileField);

        JButton exportButton = new JButton("Export");
        exportButton.setBounds(BUTTON_PADDING, WIN_HEIGHT - BOTTOM_PADDING - ELEM_HEIGHT,
                BUTTON_WIDTH, ELEM_HEIGHT);
        exportButton.setName("ExportButton");
        ActionListener exportButtonActionListener = actionEvent -> {
            try (PrintWriter writer = new PrintWriter(fileField.getText())) {
                for (String key : linksAndTitles.keySet()) {
                    writer.println(key);
                    writer.println(linksAndTitles.get(key));
                }
            } catch (IOException e) {
                showMessageDialog(null, "Bad file path!");
            }
        };
        exportButton.addActionListener(exportButtonActionListener);
        add(exportButton);

        setLayout(null);
        setVisible(true);
    }

    private void startCrawlers(String firstURL, int threadNumbers, int maxDepth) throws IOException {

        linksAndTitles.clear();
        LinksQueue.linksQueue.clear();
        LinksQueue.work = true;
        resetCounter();
        crawlers = new Crawler[threadNumbers];
        threads = new Thread[threadNumbers];
        crawlers[0] = new Crawler(firstURL, maxDepth);
        threads[0] = new Thread(crawlers[0]);
        threads[0].setPriority(10);
        threads[0].start();
        for (int i = 1; i < threadNumbers; i++) {
            crawlers[i] = new Crawler(maxDepth);
            threads[i] = new Thread(crawlers[i]);
            threads[i].setPriority(1);
            threads[i].start();
        }
        runButton.setSelected(false);
    }

    private void stopCrawlers() {
        LinksQueue.work = false;
        threads = null;
        crawlers = null;
    }

    public static synchronized void updateCounter() {
        linksProcessed++;
        linksCounter.setText(String.valueOf(linksProcessed));
    }
    private void resetCounter() {
        linksProcessed = 0;
        linksCounter.setText("0");
    }
}