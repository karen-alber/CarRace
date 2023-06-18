package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class CannotLoginPacket extends MainPacket {
    
    
    public CannotLoginPacket(byte[] data) {
        super(22);
    }
    
    public CannotLoginPacket() {
        super(22);
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
        return ("22").getBytes();
    }
}
