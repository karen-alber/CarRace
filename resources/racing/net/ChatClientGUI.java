package racing.net;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Socket;


public class ChatClientGUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private Socket socket;
    private String username;

    public ChatClientGUI(Socket socket, String username) {
        this.socket = socket;
        this.username = username;

        
        frame = new JFrame(username + " chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        
        messageField = new JTextField();

        
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                ChatClient chatClient = new ChatClient(socket, username);
                chatClient.sendMessageFromGUI(message);
                messageField.setText("");
            }
        });

        
        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = messageField.getText();
                    ChatClient chatClient = new ChatClient(socket, username);
                    chatClient.sendMessageFromGUI(message);
                    messageField.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(inputPanel, BorderLayout.SOUTH);
        frame.setContentPane(contentPane);

        
        try {
            File file = new File("ChatClientGUI.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    chatArea.append(line + "\n");
                }
                reader.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        
        try {
            RandomAccessFile raf = new RandomAccessFile("ChatClientGUI.txt", "r");
            final long[] filePointer = {raf.length()};

            PropertyChangeListener pcl = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        RandomAccessFile raf = new RandomAccessFile("ChatClientGUI.txt", "r");
                        raf.seek(filePointer[0]);
                        String line = null;
                        while ((line = raf.readLine()) != null) {
                            chatArea.append(line + "\n");
                        }
                        filePointer[0] = raf.getFilePointer();
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pcl.propertyChange(null);
                }
            };

            
            Timer timer = new Timer(100, al);

            
            timer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        frame.setVisible(true);
    }
}
