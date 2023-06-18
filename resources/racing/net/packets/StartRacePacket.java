
package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class StartRacePacket extends MainPacket {
    
    private long time;
    
    public StartRacePacket(byte[] data) {
        super(14);
        String dataArray = readData(data);
        time = Long.parseLong(dataArray);
    }
    
    public StartRacePacket(Long time) {
        super(14);
        this.time = time;
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
        return ("14"+this.time).getBytes();
    }
    
    public long getTime() {
        return time;
    }
}
