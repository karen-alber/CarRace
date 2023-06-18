package racing.net.packets;

import java.util.ArrayList;
import java.util.List;
import racing.PlayerMP;
import racing.net.Client;
import racing.net.Server;

public class ResultsPacket extends MainPacket {
    
    private final List<Double> raceTimes = new ArrayList<>();
    private final List<String> usernames = new ArrayList<>();
    
    public ResultsPacket(byte[] data) {
        super(20);
        String[] dataArray = readData(data).split(",");
        int size = Integer.parseInt(dataArray[0]);
        for (int i=0; i<size; i++) {
            raceTimes.add(Double.parseDouble(dataArray[i*2+1]));
            usernames.add(dataArray[i*2+2]);
        }
    }

    public ResultsPacket(List<Double> raceTimes, List<PlayerMP> players) {
        super(20);
        this.raceTimes.addAll(raceTimes);
        for(PlayerMP player : players) {
            usernames.add(player.getUsername());
        }
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
        String data;
        data = "20" + raceTimes.size();
        for(int i=0; i<raceTimes.size(); i++) {
            data += "," + raceTimes.get(i) + "," + usernames.get(i);
        }
        return data.getBytes();
    }
    
    public Double getRaceTime(int i) {
        return raceTimes.get(i);
    }
    
    public String getUsername(int i) {
        return usernames.get(i);
    }
    
    public int getSize() {
        return raceTimes.size();
    }
}
