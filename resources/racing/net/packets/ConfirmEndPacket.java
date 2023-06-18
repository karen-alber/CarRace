package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class ConfirmEndPacket extends MainPacket {
    
    private final String username;
    
    public ConfirmEndPacket(byte[] data) {
        super(25);
        username = readData(data);
    }
    
    public ConfirmEndPacket(String username) {
        super(25);
        this.username = username;
    }
    
    @Override
    public void writeData(Client client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(Server server) {
        server.sendDataToAllClients(getData());
    }
    
    @Override
    public byte[] getData() {
        return ("25"+this.username).getBytes();
    }
    
    public String getUsername() {
        return username;
    }
}
