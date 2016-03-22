package client;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.NameNotBoundException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author George Kravas
 */
public class Location
    extends MLObject {

    /** The version of the serialized form of this class. */
    protected static final int SCARRED_LAND = 0;
    protected static final int FLAT_LANDS_TYPE = 1;

    private static final long serialVersionUID = 1L;
    protected ManagedReference<ScalableHashMap<String, ManagedReference<FactionlListener>>> players;
    private static final Logger logger = Logger.getLogger(FactionlListener.class.getName());
    protected static final String LOCATION_BIND_PREFIX = "Location.";
    protected final ManagedReference<Channel> locationChannel;

    protected int tileWidth;
    protected String locationMap;

    public Location(String name) {
        super(name, "Location of battle");
        Channel ch = AppContext.getChannelManager().createChannel(name, new LocalGameChannelListener(), Delivery.RELIABLE);
        locationChannel = AppContext.getDataManager().createReference(ch);
        players = AppContext.getDataManager().createReference(new ScalableHashMap<String, ManagedReference<FactionlListener>>());
        tileWidth = 8;
        generateMap();
    }

    public void generateMap() {
        Random r = new Random();
        locationMap = "";
        int len = tileWidth * tileWidth;
        for (int i = 0; i < len; i++)
            locationMap += r.nextInt(2) + ",";
        locationMap = locationMap.substring(0, len * 2 - 1);
        logger.log(Level.INFO, "Tilr number of location {0} is {1}", new Object[] {getName(), locationMap.split(",").length});
    }

    public String addPlayer(FactionlListener player) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        dataManager.markForUpdate(player);
        String coords = generateNextAvailableCoords();
        player.setCitadelCoords(coords);
        player.joinChannel(locationChannel);
        players.getForUpdate().put(coords, dataManager.createReference(player));
        logger.log(Level.INFO, "User {0} has enterred location {1}", new Object[] { player.getUserName(), getName()});
        logger.log(Level.INFO, "{0} users in location {1}", new Object[] { players.get().size(), getName()});
        return coords;
    }

    public void removePlayer(FactionlListener player) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        dataManager.markForUpdate(player);
        player.leaveChannel(locationChannel);
        players.getForUpdate().remove(player.getCitadelCoords());
        logger.log(Level.INFO, "User {0} has left location {1}", new Object[] { player.getUserName(), getName()});
        logger.log(Level.INFO, "{0} users in location {1}", new Object[] { players.get().size(), getName()});
    }

    /*public String getOtherPlayersCoords(FactionlListener player) {
        String arr = "";
        String key = "";
        ManagedReference<FactionlListener> faction;
        Object[] keys = players.get().keySet().toArray();
        for (int i = 0; i < keys.length - 1; i++) {
            key = (String)keys[i];
            faction = players.get().get(key);
            if (!key.matches(player.getCitadelCoords()))
                arr += key + ":" + faction.get().getUserName() + ",";
        }
        return (arr.matches("")) ? "" : arr.substring(0, arr.length() - 1);
    }*/

    public String getOtherPlayersCoords(FactionlListener player) {
        String arr = "";
        String key = "";
        ManagedReference<FactionlListener> faction;
        Iterator<Entry<String, ManagedReference<FactionlListener>>> it = players.get().entrySet().iterator();
        Entry<String, ManagedReference<FactionlListener>> entry = null;
        while(it.hasNext()){
            entry = it.next();
            faction = entry.getValue();
            key = entry.getKey();
            if (!key.matches(player.getCitadelCoords()))
                arr += key + ":" + faction.get().getUserName() + ",";
        }
        return (arr.matches("")) ? "" : arr.substring(0, arr.length() - 1);
    }

    @SuppressWarnings("empty-statement")
    protected String generateNextAvailableCoords() {
        Random r = new Random();
        String coords = (r.nextInt(tileWidth) + 1) + "_" + (r.nextInt(tileWidth) + 1);
        while (players.get().containsKey(coords))
            coords = (r.nextInt(tileWidth) + 1) + "_" + (r.nextInt(tileWidth) + 1);
       return coords;
    }

    //Static
    public static Location getLocation(String LocationName) {
        String locationBinding = LOCATION_BIND_PREFIX + LocationName;
        DataManager dataMgr = AppContext.getDataManager();
        Location location;
        try {
            location = (Location) dataMgr.getBinding(locationBinding);
        } catch (NameNotBoundException ex) {
            // this is a new location
            location = new Location(locationBinding);
            logger.log(Level.INFO, "New location created: {0}", location);
            dataMgr.setBinding(locationBinding, location);
        }
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getName();
    }

    //GETTERS
    public String getLocationMap() {return locationMap;}
}
