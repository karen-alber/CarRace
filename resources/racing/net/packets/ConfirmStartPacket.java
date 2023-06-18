
package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class ConfirmStartPacket extends MainPacket {
    
    private final String username;
    
    public ConfirmStartPacket(byte[] data) {
        super(24);
        username = readData(data);
    }
    
    public ConfirmStartPacket(String username) {
        super(24);
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
        return ("24"+this.username).getBytes();
    }
    
    public String getUsername() {
        return username;
    }
}
