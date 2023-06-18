package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class DisconnectPacket extends MainPacket {

    private String username;

    public DisconnectPacket(byte[] data) {
        super(01);
        this.username = readData(data);
    }

    public DisconnectPacket(String username) {
        super(01);
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
        return ("01" + this.username).getBytes();
    }

    public String getUsername() {
        return username;
    }

}