package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class ConfirmLoginPacket extends MainPacket {
     
    
    public ConfirmLoginPacket(byte[] data) {
        super(21);
    }
    
    public ConfirmLoginPacket() {
        super(21);
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
        return ("21").getBytes();
    }
}
