package client;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import org.jdom.Document;
import protocol.MLProtocol;
import protocol.MessageFactory;

/**
 *
 * @author George Kravas
 */
public class SessionMessageHandler
        implements ManagedObject, Serializable{

    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    public void handleMessage(Document xml, FactionlListener faction) throws UnsupportedEncodingException {
        int type = Integer.parseInt(xml.getRootElement().getChild(MLProtocol.TYPE).getText());
        System.out.println("Session type :" + type + ", " + faction.getUserName());
        switch (type) {
            case MLProtocol.LOGIN_REQUEST:
                manageLoginRequest(xml, faction);
            break;
            case MLProtocol.REQUEST_JOIN_LOCATION:
                String coords = manageLocationRequest(faction);
                sendLocationJoined(faction, coords);
            break;
            case MLProtocol.REQUEST_OTHER_PLAYERS_JOINED_LOCATION:
                sendOtherPlayersAlreadyJoined(faction, xml.getRootElement().getChild(MLProtocol.LOCATION).getText());
            break;
        }
    }

    protected void manageLoginRequest(Document xml, FactionlListener faction) throws UnsupportedEncodingException {
        String password = xml.getRootElement().getChildText(MLProtocol.PASSWORD);
        if (password.matches(faction.getPassword()) && !faction.getIsLoggedIn()) {
            faction.sendSessionMessage(MessageFactory.createLoginSuccessMessage());
            faction.setIsLoggedIn(true);
        } else {
            faction.sendSessionMessage(MessageFactory.createLoginFailureMessage());
        }
    }

    protected String manageLocationRequest(FactionlListener faction) {
        String locationName= Location.LOCATION_BIND_PREFIX + "Location";
        Location location = (Location) AppContext.getDataManager().getBinding(locationName);
        faction.setLocation(location);
        return location.addPlayer(faction);
    }

    public void sendLocationJoined(FactionlListener faction, String citadelCoords) {
           faction.sendLocalGameMessage(
            MessageFactory.createPlayerJoinedLocation(faction, citadelCoords, faction.getLocation().getLocationMap()));
    }

    public void sendLocationLeft(FactionlListener faction) {
            faction.sendLocalGameMessage(MessageFactory.createPlayerLeftLocation(faction));
    }

    public void sendOtherPlayersAlreadyJoined(FactionlListener faction, String locationName) {
        //Location location = (Location) AppContext.getDataManager().getBinding(locationName);
        String users = "";
        try {
            users = faction.getLocation().getOtherPlayersCoords(faction);
        } catch(Exception ex) {
            System.out.println(ex.toString());
        }
        System.out.println("USERS IN LOCATION '" + locationName + "' are  '" + users + "' for player '" + faction.getUserName() + "'");
        faction.sendSessionMessage(MessageFactory.createOtherPlayersAlreadyJoined(users));
    }
}
