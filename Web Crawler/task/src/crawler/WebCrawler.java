package crawler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.showMessageDialog;

public class WebCrawler extends JFrame {

    private JTextField URLField;
    private JTextArea HTMLTextArea;
    private JLabel Title;


    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int windowHeight = 720;
        final int windowWidth = 720;
        setSize(windowWidth, windowHeight);
        setTitle("WebCrawler");

        URLField = new JTextField("https://");
        URLField.setBounds(10, 10, 635, 20);
        URLField.setName("UrlTextField");
        URLField.setEnabled(true);
        add(URLField);

        HTMLTextArea = new JTextArea();
        HTMLTextArea.setBounds(10, 65, 700, 620);
        HTMLTextArea.setName("HtmlTextArea");
        HTMLTextArea.setEnabled(false);
        add(HTMLTextArea);

        JLabel TitleLabel = new JLabel("Title:");
        TitleLabel.setBounds(10, 40, 40, 20);
        TitleLabel.setName("Title");
        add(TitleLabel);

        Title = new JLabel("");
        Title.setBounds(45, 40, 615, 20);
        Title.setName("TitleLabel");
        add(Title);

        JButton runButton = new JButton("View");
        runButton.setBounds(650, 10, 60, 20);
        runButton.setName("RunButton");
        ActionListener buttonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    getCode();
                } catch (IOException e) {
                    showMessageDialog(null, "Bad URL address!");
                }

            }
        };
        runButton.addActionListener(buttonActionListener);
        add(runButton);

        setLayout(null);
        setVisible(true);
    }

    private void getCode() throws IOException {
        final String url = URLField.getText();

        final InputStream inputStream = new URL(url).openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append('\n');
        }

        HTMLTextArea.setText(stringBuilder.toString());

        Pattern pattern = Pattern.compile("(?<=<title>).*(?=</title>)");
        Matcher matcher = pattern.matcher(HTMLTextArea.getText());

        if (matcher.find()) {
            Title.setText(matcher.group());
        } else {
            Title.setText("¯\\_(ツ)_/¯");
        }
    }

}