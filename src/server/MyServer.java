package server;

import client.FactionlListener;
import client.Location;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author George Kravas
 */
public class MyServer
        implements Serializable, AppListener {

    //public static final String LOCAL_GAMEPLAY_CHANNEL_NAME = "localGameplayChannel";
    //public  static final String CHAT_CHANNEL_NAME = "chatChannel";

    protected ManagedReference<ScalableHashMap<String, ManagedReference<FactionlListener>>> activePlayers;
    //protected ManagedReference<ScalableHashMap<String, Location>> locations;
    private static final Logger logger = Logger.getLogger(FactionlListener.class.getName());

    @Override
    public void initialize(Properties prop) {
        DataManager dmn = AppContext.getDataManager();
        activePlayers = dmn.createReference(new ScalableHashMap<String, ManagedReference<FactionlListener>>());
        //locations = dmn.createReference(new ScalableHashMap<String, Location>());
        initLocations(dmn);
    }

    protected void initLocations(DataManager dmn) {
        Location location = Location.getLocation("Location");
        //locations.getForUpdate().put("Location", location);
    }

    @Override
    public ClientSessionListener loggedIn(ClientSession session) {
        FactionlListener player = FactionlListener.loggedIn(session);
        activePlayers.getForUpdate().put(session.getName(), AppContext.getDataManager().createReference(player));
        logger.log(Level.INFO, "User {0} has loggin", new Object[] { session.getName() });
        return player;
    }


}
