package protocol;

import java.nio.ByteBuffer;

/**
 *
 * @author George Kravas
 */
public class MLProtocol {

    public static final int LOGIN_REQUEST = 0;
    public static final int LOGIN_SUCCESS = 1;
    public static final int LOGIN_FAILURE = 2;
    public static final int ACTION_PERFORMED = 3;
    public static final int PLAYER_JOINED_LOCATION = 4;
    public static final int PLAYER_LEFT_LOCATION = 5;
    public static final int REQUEST_JOIN_LOCATION = 6;
    public static final int OTHER_PLAYERS_JOINED_LOCATION = 7;
    public static final int REQUEST_OTHER_PLAYERS_JOINED_LOCATION = 8;

    public static final String TYPE = "type";
    public static final String PASSWORD = "password";
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String ACTION = "action";
    public static final String ENTITY = "entity";
    public static final String ARGS = "args";
    public static final String LOCATION = "location";
    public static final String CITADEL_COORDS = "citadel";
    public static final String LOCATION_MAP = "locationMap";

    public static ByteBuffer createXMLSGSPacket(String[] args, String[] properties) {
        String msg = "<msg>";
        for (int i = 0; i < properties.length; i++) {
            msg += createXMLChild(properties[i], args[i]);
        }
        msg += "</msg>";
        return ByteBuffer.wrap(msg.getBytes());
    }

    public static String createXMLChild(String name, String value) {
        return "<" + name + ">" + value + "</" + name + ">";
    }
}
