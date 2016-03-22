package client;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;
import util.XMLUtil;

/**
 *
 * @author George Kravas
 */
public class LocalGameChannelListener
        implements ChannelListener, Serializable{

    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    /** The {@link Logger} for this class. */
    private static final Logger logger = Logger.getLogger(LocalGameChannelListener.class.getName());
    private final ManagedReference<LocalGameChannelMessageHandler> messageHandler;

    public LocalGameChannelListener() {
        messageHandler = AppContext.getDataManager().createReference(new LocalGameChannelMessageHandler());
    }


    @Override
    public void receivedMessage(Channel channel, ClientSession sender, ByteBuffer message) {
        logger.log(Level.INFO, "Channel message from {0} on channel {1}", new Object[] { sender.getName(), channel.getName() });
        try {
            messageHandler.get().handleMessage(XMLUtil.createXMLDocument(message), channel, sender);
        } catch (CharacterCodingException ex) {
            Logger.getLogger(LocalGameChannelListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(LocalGameChannelListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocalGameChannelListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
