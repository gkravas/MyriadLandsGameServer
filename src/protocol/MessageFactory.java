package protocol;

import client.FactionlListener;
import java.nio.ByteBuffer;

/**
 *
 * @author George Kravas
 */
public class MessageFactory {

    public static ByteBuffer createLoginSuccessMessage() {
        String[] values = {String.valueOf(MLProtocol.LOGIN_SUCCESS)};
        String[] names = {MLProtocol.TYPE};
        return MLProtocol.createXMLSGSPacket(values, names);
    }

    public static ByteBuffer createLoginFailureMessage() {
        String[] values = {String.valueOf(MLProtocol.LOGIN_FAILURE)};
        String[] names = {MLProtocol.TYPE};
        return MLProtocol.createXMLSGSPacket(values, names);
    }

    public static ByteBuffer createPlayerJoinedLocation(FactionlListener faction, String citadelCoords, String locationMap) {
        String[] values = {String.valueOf(MLProtocol.PLAYER_JOINED_LOCATION), faction.getUserName(),
                            faction.getLocation().getName(), citadelCoords, locationMap};
        String[] names = {MLProtocol.TYPE, MLProtocol.USER, MLProtocol.LOCATION, MLProtocol.CITADEL_COORDS,
                          MLProtocol.LOCATION_MAP};
        return MLProtocol.createXMLSGSPacket(values, names);
    }

    public static ByteBuffer createPlayerLeftLocation(FactionlListener faction) {
        String[] values = {String.valueOf(MLProtocol.PLAYER_LEFT_LOCATION), faction.getUserName()};
        String[] names = {MLProtocol.TYPE, MLProtocol.USER};
        return MLProtocol.createXMLSGSPacket(values, names);
    }

    public static ByteBuffer createOtherPlayersAlreadyJoined(String users) {
        String[] values = {String.valueOf(MLProtocol.OTHER_PLAYERS_JOINED_LOCATION), users};
        String[] names = {MLProtocol.TYPE, MLProtocol.USERS};
        return MLProtocol.createXMLSGSPacket(values, names);
    }
}
