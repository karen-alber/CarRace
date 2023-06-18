package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class EndOfRacePacket extends MainPacket {
    
    private long time;
    
    public EndOfRacePacket(byte[] data) {
        super(15);
        time = System.currentTimeMillis();
    }

    public EndOfRacePacket() {
        super(15);
        time = System.currentTimeMillis();
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
        return ("15"+time).getBytes();
    }
    
    public long getTime() {
        return time;
    }
}
