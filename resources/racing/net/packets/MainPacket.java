package racing.net.packets;

import racing.net.Client;
import racing.net.Server;

public abstract class MainPacket {

    public static enum PacketTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), OIL(03),
        GET_OIL(05),  REMOVE_OIL(07),
         SEND_MAP(13), START_RACE(14),
        END_OF_RACE(15), RESULTS(20), CONFIRM_LOGIN(21), CANNOT_LOGIN(22),
        EMPTY(23), CONFIRM_START(24), CONFIRM_END(25), CHATTING(26);

        private final int packetId;

        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }
    }

    public byte packetId;

    public MainPacket(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(Client client);

    public abstract void writeData(Server server);

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch (NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return PacketTypes.INVALID;
    }
}
