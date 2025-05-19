package com.example.chatapplication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {

    private JTextPane messagePane;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private StyledDocument doc;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatClientGUI() {
        setTitle("💬 Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doc = messagePane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(messagePane);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputField.setPreferredSize(new Dimension(400, 40));
        inputField.setMargin(new Insets(5, 10, 5, 10));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(100, 40));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        connectToServer();
        setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            appendStyledMessage("✅ Connected to chat server...", Color.DARK_GRAY, false);

            Thread thread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        final String msg = message;
                        SwingUtilities.invokeLater(() -> appendStyledMessage("👤 " + msg, new Color(60, 60, 60), false));
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> appendStyledMessage("❌ Disconnected from server", Color.RED, false));
                }
            });
            thread.start();
        } catch (IOException e) {
            appendStyledMessage("❌ Unable to connect to server: " + e.getMessage(), Color.RED, false);
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            appendStyledMessage("🟢 You: " + message, new Color(0, 102, 204), true);
            inputField.setText("");
        }
    }

    private void appendStyledMessage(String message, Color color, boolean rightAlign) {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        StyleConstants.setFontFamily(attr, "Segoe UI");
        StyleConstants.setFontSize(attr, 14);
        StyleConstants.setAlignment(attr, rightAlign ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
        StyleConstants.setSpaceAbove(attr, 4);
        StyleConstants.setSpaceBelow(attr, 4);

        try {
            doc.setParagraphAttributes(doc.getLength(), 1, attr, false);
            doc.insertString(doc.getLength(), getTimeStamp() + " " + message + "\n", attr);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getTimeStamp() {
        return "[" + timeFormat.format(new Date()) + "]";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientGUI::new);
    }
}