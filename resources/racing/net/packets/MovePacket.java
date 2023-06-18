package racing.net.packets;

import racing.Player;
import racing.net.Client;
import racing.net.Server;



public class MovePacket extends MainPacket {

    private final String username;
    private final double x, y;

    private final double angle;
    private double speed;
    private final long freezeTime;
    private final int lap;
    
    public MovePacket(byte[] data) {
        super(02);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Double.parseDouble(dataArray[1]);
        this.y = Double.parseDouble(dataArray[2]);
        this.angle = Double.parseDouble(dataArray[3]);
        this.speed = Double.parseDouble(dataArray[4]);
        this.freezeTime = Long.parseLong(dataArray[7]);
        this.lap = Integer.parseInt(dataArray[8]);
        
    }

    public MovePacket(String username, double x, double y, double angle,
                      double speed, boolean hasOil, boolean hasBomb, long freezeTime, int lap) {
        super(02);
        this.username = username;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.freezeTime = freezeTime;
        this.lap = lap;
    }
    
    public MovePacket(String username, int x, int y, double angle,
                      double speed, boolean hasOil, boolean hasBomb, long freezeTime, int lap) {
        super(02);
        this.username = username;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.freezeTime = freezeTime;
        this.lap = lap;
    }
     
    public MovePacket(Player player) {
        super(02);
        this.username = player.getUsername();
        this.x = player.getX();
        this.y = player.getY();
        this.angle = player.getAngle();
        this.speed = player.getSpeed();
        this.freezeTime = player.getFreezeTime();
        this.lap = player.getLap();
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
        return ("02" + this.username + "," + this.x + "," + this.y + "," + 
                this.angle + "," + this.speed + "," +  "," + "," + this.freezeTime + "," +
                this.lap).getBytes();

    }

    public String getUsername() {
        return username;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getAngle() {
        return angle;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public long getFreezeTime() {
        return freezeTime;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public int getLap() {
        return this.lap;
    }
}
