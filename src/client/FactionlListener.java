package client;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import util.XMLUtil;

/**
 *
 * @author George Kravas
 */
public class FactionlListener
    extends MLObject
    implements ClientSessionListener {

    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;
    /** The {@link Logger} for this class. */
    private static final Logger logger = Logger.getLogger(FactionlListener.class.getName());
    /** The session this {@code ClientSessionListener} is listening to. */
    private ManagedReference<ClientSession> sessionRef;
    /** The name of the {@code ClientSession} for this listener. */
    protected static final String PLAYER_BIND_PREFIX = "Player.";

    private String sessionName;
    private String password;
    private ManagedReference<SessionMessageHandler> sessionMessageHandler;
    private ManagedReference<Channel> localGameplayChannel;
    private ManagedReference<Location> locationRef;
    private boolean _isLoggedIn;
    private String citaldelCoords;

    public FactionlListener(String name) {
        super(name, "FactionPlayer");
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.markForUpdate(this);
        sessionMessageHandler = dataMgr.createReference(new SessionMessageHandler());
        password = "123456";
    }


    public void joinChannel(ManagedReference<Channel> ch) {
        AppContext.getDataManager().markForUpdate(this);
        ch.getForUpdate().join(getSession());
        localGameplayChannel = ch;
    }

    public void leaveChannel(ManagedReference<Channel> ch) {
        AppContext.getDataManager().markForUpdate(this);
        ch.getForUpdate().leave(getSession());
        localGameplayChannel = null;
    }

    @Override
    public void receivedMessage(ByteBuffer message) {
        //ClientSession session = getSession();
        Document doc = null;
        try {
            doc = XMLUtil.createXMLDocument(message);
        } catch (CharacterCodingException ex) {
            Logger.getLogger(FactionlListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(FactionlListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FactionlListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sessionMessageHandler.get().handleMessage(doc, this);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FactionlListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        //logger.log(Level.INFO, "Message {0}", doc.toString());
    }

    @Override
    public void disconnected(boolean graceful) {
        AppContext.getDataManager().markForUpdate(this);
        sessionMessageHandler.get().sendLocationLeft(this);
        locationRef.getForUpdate().removePlayer(this);
        locationRef = null;
        localGameplayChannel = null;
        setIsLoggedIn(false);
        String grace = graceful ? "graceful" : "forced";
        logger.log(Level.INFO, "User {0} has logged out {1}", new Object[] { sessionName, grace });
    }

    public void sendSessionMessage(ByteBuffer message) {
        getSession().send(message);
    }

    public void sendLocalGameMessage(ByteBuffer message) {
        if (localGameplayChannel == null) return;
        localGameplayChannel.get().send(getSession(), message);
    }

    //GETTERS
    private ClientSession getSession() {
        if (sessionRef == null) return null;
        return sessionRef.get();
    }

    public String getPassword() {return password;}
    public String getUserName() {return sessionName;}
    public boolean getIsLoggedIn() {return _isLoggedIn;}
    public Location getLocation() {
        if (locationRef == null)
            return null;

        return locationRef.get();
    }
    public String getCitadelCoords() {return citaldelCoords;}

    //SETTERS
    public void setIsLoggedIn(boolean value) {
        AppContext.getDataManager().markForUpdate(this);
        _isLoggedIn = value;
    }

    public void setLocation(Location value) {
        AppContext.getDataManager().markForUpdate(this);
        if (value == null) {
            locationRef = null;
            return;
        }
        locationRef = AppContext.getDataManager().createReference(value);
    }

    protected void setSession(ClientSession session) {
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.markForUpdate(this);
        if (session == null) {
            sessionRef = null;
            return;
        }
        sessionRef = dataMgr.createReference(session);
        sessionName = session.getName();
        logger.log(Level.INFO, "Set session for {0} to {1}", new Object[] { this, session });
    }

    public void setCitadelCoords(String value) {
        citaldelCoords = value;
    }

    //Static
    public static FactionlListener loggedIn(ClientSession session) {
        String playerBinding = PLAYER_BIND_PREFIX + session.getName();
        DataManager dataMgr = AppContext.getDataManager();
        FactionlListener player;
        try {
            player = (FactionlListener) dataMgr.getBinding(playerBinding);
        } catch (NameNotBoundException ex) {
            // this is a new player
            player = new FactionlListener(playerBinding);
            logger.log(Level.INFO, "New player created: {0}", player);
            dataMgr.setBinding(playerBinding, player);
        }
        player.setSession(session);
        return player;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getName());
        buf.append('@');
        if (getSession() == null) {
            buf.append("null");
        } else {
            buf.append(sessionRef.getId());
        }
        return buf.toString();
    }
}
