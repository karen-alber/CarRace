package racing.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private ServerSocket ss;
    public ChatServer (ServerSocket ss) {
        this.ss = ss;
    }

    public void startChatServer() {
        System.out.println("SERVER: Chat server is online!");
        try {
            while (!ss.isClosed()) {
                Socket s = ss.accept();
                System.out.println("SERVER: A new player has connected.");
                ChatClientHandler cch = new ChatClientHandler(s);

                Thread thread = new Thread(cch);
                thread.start();
            }
        } catch(IOException e) {
            e.printStackTrace();
            closeChatServer();
        }
    }
    public void closeChatServer(){
        if (ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

