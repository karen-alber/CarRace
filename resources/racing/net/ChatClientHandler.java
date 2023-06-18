package racing.net;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClientHandler implements Runnable{
    public static ArrayList<ChatClientHandler> handlers = new ArrayList<>();
    private Socket s;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ChatClientHandler(Socket s) {
        try {
            this.s = s;
            this.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.username = reader.readLine();
            handlers.add(this);
            broadcastMessage("SERVER: " + username + " has joined the game.");
        } catch (IOException e) {
            closeAll(s, reader, writer);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while (s.isConnected()) {
            try {
                messageFromClient = reader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(s, reader, writer);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ChatClientHandler clientHandler: handlers) {
            if (!clientHandler.username.equals(username)) {
                try {
                    clientHandler.writer.write(messageToSend);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                } catch (IOException e) {
                    closeAll(s, reader, writer);
                }
            }
        }
    }

    public void closeAll(Socket s, BufferedReader reader, BufferedWriter writer) {
        removeClientHandler();
        try {
            if (s != null)
                s.close();
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClientHandler(){
        handlers.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat.");
    }
}
