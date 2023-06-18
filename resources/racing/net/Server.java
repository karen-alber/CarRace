package racing.net;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


import racing.PlayerMP;
import racing.net.packets.*;
import racing.net.packets.MainPacket.PacketTypes;

public class Server extends Thread {

    public static final int PORT = 11331;
    private DatagramSocket socket;
    protected List<PlayerMP> connectedPlayers = new ArrayList<>();
    protected List<Double> raceTimes;
    private File map;
    private List<ParseUserPacket> userThreads;
    private List<Boolean> confirmedStart;
    protected int startX, startY;
    protected double startAngle;
    protected long time;
    protected boolean isRaceStarted;
    protected int numberOfLaps;
    private boolean allConfirmedStart;
    
    private final Object lockUserList = new Object();
            
    public Server() {
        numberOfLaps = Integer.parseInt(JOptionPane
                .showInputDialog(null, "Please enter number of laps"));
        raceTimes = new ArrayList<>();
        userThreads = new ArrayList<>();
        isRaceStarted = false;
        
        loadMap();
        
        time = System.currentTimeMillis();
        try {
            this.socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {  
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                if(isRaceStarted) {
                    if(connectedPlayers.isEmpty()) {
                        isRaceStarted = false;

                    }
                    else if(!allConfirmedStart) {
                        int i = 0;
                        allConfirmedStart = true;
                        for(Boolean confirmed : confirmedStart) {
                            if (confirmed.booleanValue() == Boolean.FALSE) {
                                StartRacePacket startPacket =
                                        new StartRacePacket(time);
                                this.sendData(startPacket.getData(),
                                        connectedPlayers.get(i).ipAddress, 
                                        connectedPlayers.get(i).port);
                                allConfirmedStart = false;
                            }
                            i++;
                        }
                    }
                }
                for(int i=0; i<raceTimes.size();i++) {
                    if(raceTimes.get(i) == 0.) break;
                    else if(i==raceTimes.size()-1) {
                        
                        ResultsPacket resultsPacket =
                                new ResultsPacket(raceTimes, connectedPlayers);
                        this.sendDataToAllClients(resultsPacket.getData());
                        isRaceStarted = false;
                    }
                }
                socket.receive(packet);
                this.parsePacket(packet, packet.getAddress(), packet.getPort());
            } catch (IOException|InterruptedException e) {
              System.out.println(e.getMessage());
            }
        }
    }

    private void parsePacket(DatagramPacket dpacket, InetAddress address, int port) throws InterruptedException {
        byte[] data = dpacket.getData();
        String message = new String(data).trim();
        PacketTypes type = MainPacket.lookupPacket(message.substring(0, 2));
        MainPacket packet;
        switch (type) {
        case INVALID:
            break;
        case LOGIN:
            if(isRaceStarted) {
                return;
            }
            packet = new LoginPacket(data);
            PlayerMP player = new PlayerMP(startX+50*connectedPlayers.size(), 
                    startY+50*connectedPlayers.size(), startAngle,
                    ((LoginPacket) packet).getUsername(), address, port);
            if(addConnection(player, (LoginPacket) packet)) {
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                    + ((LoginPacket) packet).getUsername() + " has connected...");
                packet = new MovePacket(player);
                sendDataToAllClients(packet.getData());
                ParseUserPacket pup = new ParseUserPacket(address, port, 
                        this, socket, player.getUsername());
                userThreads.add(pup);
                while(!userThreads.contains(pup))
                    userThreads.add(pup);
                pup.start();
            }
            break;
        case START_RACE:
            isRaceStarted = true;
            time = System.currentTimeMillis();
            confirmedStart = new ArrayList<>(Arrays
                    .asList(new Boolean[connectedPlayers.size()]));
            Collections.fill(confirmedStart, Boolean.FALSE);
            allConfirmedStart = false;
            packet = new StartRacePacket(time);
            packet.writeData(this);
            break;
        case DISCONNECT:
            packet = new DisconnectPacket(data);
            System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                    + ((DisconnectPacket) packet).getUsername() + " has left...");
            int index = getPlayerMPIndex(((DisconnectPacket)packet).getUsername());
            userThreads.get(index).close();
            removeConnection((DisconnectPacket) packet);
            break;
        default:
            boolean isUserThread = false;
            for (ParseUserPacket userThread : userThreads) {
                if (userThread.getAddress().equals(address) &&
                        userThread.getPort()==port) {
                    userThread.addPacket(dpacket);
                    isUserThread = true;
                    break;
                }
            }
            if(!isUserThread) {
                CannotLoginPacket alreadyConnectedPacket =
                        new CannotLoginPacket();
                sendData(alreadyConnectedPacket.getData(), dpacket.getAddress(),
                        dpacket.getPort());
            }
            break;
        }
    }

    private boolean addConnection(PlayerMP player, LoginPacket packet) {
        boolean alreadyConnected = false;
        synchronized(lockUserList) {
            for (PlayerMP p : this.connectedPlayers) {
                if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
                    alreadyConnected = true;
                    break;
                }
            }
            if (!alreadyConnected) {
                ConfirmLoginPacket confirmLoginPacket =
                        new ConfirmLoginPacket();
                sendData(confirmLoginPacket.getData(), player.ipAddress, player.port);
                for (PlayerMP p : this.connectedPlayers) {
                    
                    sendData(packet.getData(), p.ipAddress, p.port);

                    
                    LoginPacket tmpPacket = new LoginPacket(p.getUsername(), p.getX(), p.getY());
                    sendData(tmpPacket.getData(), player.ipAddress, player.port);
                }
                connectedPlayers.add(player);
                raceTimes.add(0.);
                return true;
            }
            else {
                ConfirmLoginPacket confirmLoginPacket =
                        new ConfirmLoginPacket();
                sendData(confirmLoginPacket.getData(), player.ipAddress, player.port);
                return false;
            }
        }
    }

    private void removeConnection(DisconnectPacket packet) {
        synchronized(lockUserList) {
            int index = getPlayerMPIndex(packet.getUsername());
            this.connectedPlayers.remove(index);
            this.raceTimes.remove(index);
            packet.writeData(this);
        }
    }

    public PlayerMP getPlayerMP(String username) {
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        synchronized(this) {
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
            try {
                this.socket.send(packet);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }
    private void loadMap() {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = 
               new File(System.getProperty("user.dir")+"\\resources\\maps");
        chooser.setCurrentDirectory(workingDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
           "PNG maps", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           map = chooser.getSelectedFile();
        }
        else System.exit(0);
        String settingsPath = "resources/maps/" + map.getName().
                                substring(0, map.getName().length()-4)+".txt";

        // set start coordinates and angle, and then checkpoints and bonusPoints
        try (Stream<String> stream = Files.lines(Paths.get(settingsPath))) {
            Iterator<String> iterator = stream.iterator();
            startX = Integer.parseInt(iterator.next());
            startY = Integer.parseInt(iterator.next());
            startAngle = Double.parseDouble(iterator.next());
            for(int i=0;i<5;i++) {
                iterator.next();
            }
        }
        catch (IOException e) {
            System.out.println("Watch out, cannot open checkpoints file!");
            System.exit(2);
        }
    }

    public File getMap() {
        return this.map;
    }
    
    public void setConfirmedStart(String username) {
        int index = getPlayerMPIndex(username);
        confirmedStart.set(index, true);
    } 
}
