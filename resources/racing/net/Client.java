package racing.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import racing.Player;
import racing.PlayerMP;

import racing.Racing;
import racing.net.packets.*;
import racing.net.packets.MainPacket.PacketTypes;

public class Client extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Racing game;
    public boolean confirmedLogin = false;
    private boolean loginFailed = false;

    public Client(Racing game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException|UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (!loginFailed) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
            } catch (IOException|InterruptedException e) {
                System.out.println(e.getMessage());
            }
            if(!confirmedLogin) {
                LoginPacket loginPacket = new LoginPacket(game.player.getUsername(),
                        game.player.getX(), game.player.getY());
                sendData(loginPacket.getData());
            }
        }
        System.err.println("Cannot log in. Probably there is already a player with that username or race has started.");
        System.exit(0);
    }

    private void parsePacket(byte[] data, InetAddress address, int port) throws InterruptedException {
        String message = new String(data).trim();
        PacketTypes type = MainPacket.lookupPacket(message.substring(0, 2));
        MainPacket packet;
        switch (type) {
        default:
        case INVALID:
            break;
        case LOGIN:
            packet = new LoginPacket(data);
            handleLogin((LoginPacket) packet, address, port);
            break;
        case DISCONNECT:
            packet = new DisconnectPacket(data);
            System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                    + ((DisconnectPacket) packet).getUsername() + " has left the world...");
            game.removePlayerMP(((DisconnectPacket) packet).getUsername());
            break;
        case MOVE:
            packet = new MovePacket(data);
            handleMove((MovePacket) packet);
            break;
        case SEND_MAP:
            packet = new SendMapPacket(data);
            setMap((SendMapPacket) packet);
            break;
        case START_RACE:
            packet = new StartRacePacket(data);
            startRace((StartRacePacket) packet);
            packet = new ConfirmStartPacket(game.player.getUsername());
            sendData(packet.getData());
            break;
        case END_OF_RACE:
            packet = new EndOfRacePacket(data);
            game.endOfRace(((EndOfRacePacket)packet).getTime());
            packet = new ConfirmEndPacket(game.player.getUsername());
            sendData(packet.getData());
            break;
        case RESULTS:
            packet = new ResultsPacket(data);
            game.showResults((ResultsPacket) packet);
            break;
        case CONFIRM_LOGIN:
            confirmedLogin = true;
            break;
        case CANNOT_LOGIN:
            loginFailed = true;
            break;
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, Server.PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleLogin(LoginPacket packet, InetAddress address, int port) {
        PlayerMP player = new PlayerMP(packet.getX(), packet.getY(), packet.getAngle(), packet.getUsername(), address, port);
        boolean alreadyLogged = false;
        for(Player tempPlayer : game.getPlayers()) {
            if(tempPlayer.getUsername().equals(player.getUsername()))
                alreadyLogged = true;
        }
        if(!alreadyLogged) {
            System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername()
                + " has joined the game...");
            game.addPlayer(player);
        }
    }

    private void handleMove(MovePacket packet) {
        this.game.movePlayer(packet.getUsername(), packet.getX(), packet.getY(),
                packet.getAngle(), packet.getSpeed(),
                 packet.getFreezeTime());
    }

    private void setMap(SendMapPacket packet) {
        this.game.setMap(packet.getFile());
    }
    
    private void startRace(StartRacePacket packet) {
        this.game.startRace(packet.getTime());
    }
}
