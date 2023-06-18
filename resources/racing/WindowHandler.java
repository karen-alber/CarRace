package racing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

import racing.net.packets.DisconnectPacket;

public class WindowHandler implements WindowListener {

    private final Racing game;

    public WindowHandler(Racing game, JFrame frame) {
        this.game = game;
        frame.addWindowListener(this);
    }

    @Override
    public void windowActivated(WindowEvent event) {
    }

    @Override
    public void windowClosed(WindowEvent event) {
        DisconnectPacket packet = new DisconnectPacket(this.game.player.getUsername());
        packet.writeData(this.game.socketClient);
        packet.writeData(this.game.socketClient);
    }

    @Override
    public void windowClosing(WindowEvent event) {
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
    }

    @Override
    public void windowIconified(WindowEvent event) {
    }

    @Override
    public void windowOpened(WindowEvent event) {
    }

}