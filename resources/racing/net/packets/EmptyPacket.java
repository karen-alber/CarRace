package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class EmptyPacket extends MainPacket {
    
    public EmptyPacket(byte[] data) {
        super(23);
    }
    
    public EmptyPacket() {
        super(23);
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
        return ("23").getBytes();
    }

}
