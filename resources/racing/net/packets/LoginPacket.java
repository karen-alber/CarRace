package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public class LoginPacket extends MainPacket {

    private final String username;
    private final double x, y;
    private double angle;

    public LoginPacket(byte[] data) {
        super(00);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Double.parseDouble(dataArray[1]);
        this.y = Double.parseDouble(dataArray[2]);
        this.angle = Double.parseDouble(dataArray[3]);
    }

    public LoginPacket(String username, double x, double y) {
        super(00);
        this.username = username;
        this.x = x;
        this.y = y;
    }
    
    public LoginPacket(String username, int x, int y) {
        super(00);
        this.username = username;
        this.x = x;
        this.y = y;
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
        return ("00" + this.username + "," + getX() + "," + getY() + 
                "," + getAngle()).getBytes();
    }

    public String getUsername() {
        return username;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public double getAngle() {
        return angle;
    }

}