package racing.net;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private Socket s;
    private BufferedReader reader;
    private BufferedWriter writer;
    public String username;
    private BufferedWriter guiWriter;

    public ChatClient(Socket s, String username) {
        try {
            this.s = s;
            this.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.username = username;
            File guiFile = new File("ChatClientGUI.txt");
            if (guiFile.exists()) {
                guiFile.delete();
            }

            // Open a new file writer for the ChatClientGUI
            this.guiWriter = new BufferedWriter(new FileWriter(guiFile, true));
            // Start listening for messages in a new thread
            new Thread(this::listenForMessage).start();
        } catch (IOException e) {
            closeAll(s, reader, writer, guiWriter);
        }
    }

    public void sendMessage() {
        try {
            writer.write(username);
            writer.newLine();
            writer.flush();

            System.out.println("Start Chatting!");
            Scanner s = new Scanner(System.in);
            while (this.s.isConnected()) {
                String message = s.nextLine();
                // Write the message to both the server and the ChatClientGUI
                sendMessageToServer(username + ": " + message);
                guiWriter.write(username + ": " + message);
                guiWriter.newLine();
                guiWriter.flush();
            }
        } catch (IOException e) {
            closeAll(s, reader, writer, guiWriter);
        }
    }
    public void sendMessageFromGUI(String message) {
        try {
            // Write the message to both the server and the ChatClientGUI
            sendMessageToServer(username + ": " + message);
            guiWriter.write(username + ": " + message);
            guiWriter.newLine();
            guiWriter.flush();
        } catch (IOException e) {
            closeAll(s, reader, writer, guiWriter);
        }
    }

    private void sendMessageToServer(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;
                while(s.isConnected()){
                    try {
                        messageFromChat = reader.readLine();
                        System.out.println(messageFromChat);
                        // Write the message to the ChatClientGUI
                        guiWriter.write(messageFromChat);
                        guiWriter.newLine();
                        guiWriter.flush();
                    } catch(IOException e) {
                        closeAll(s, reader, writer, guiWriter);
                    }
                }
            }
        }).start();
    }

    public void closeAll(Socket s, BufferedReader reader, BufferedWriter writer, BufferedWriter guiWriter) {
        try {
            if (s != null)
                s.close();
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
            if (guiWriter != null)
                guiWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}