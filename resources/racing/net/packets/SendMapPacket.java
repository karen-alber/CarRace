package racing.net.packets;

import java.io.File;
import racing.net.Client;
import racing.net.Server;

public class SendMapPacket extends MainPacket {
    
    private final File file;
    
    public SendMapPacket(byte[] data) {
        super(13);
        String dataArray = readData(data);
        this.file = new File(dataArray);
    }
    
    public SendMapPacket(File file) {
        super(13);
        this.file = file;
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
        return ("13" + this.file.getName()).getBytes();
    }
    
    public File getFile() {
        return file;
    }
}
