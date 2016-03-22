package client;

import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import org.jdom.Document;
import protocol.MLProtocol;
import util.XMLUtil;

/**
 *
 * @author George Kravas
 */
public class LocalGameChannelMessageHandler
        implements ManagedObject, Serializable{

    /** The version of the serialized form of this class. */
    private static final long serialVersionUID = 1L;

    public void handleMessage(Document xml, Channel ch, ClientSession sender) throws UnsupportedEncodingException {
        int type = Integer.parseInt(xml.getRootElement().getChild(MLProtocol.TYPE).getText());
        System.out.println("LocalGame type :" + type);
        switch (type) {
            case MLProtocol.ACTION_PERFORMED:
                manageActionPerformed(xml, ch, sender);
            break;
        }
    }

    protected void manageActionPerformed(Document xml, Channel ch, ClientSession sender) {
        //xml.getRootElement().getChild(MessageProperties.TYPE).setText(String.valueOf(MessageType.ACTION_PERFORMED_BY_OTHER));
        ch.send(sender, XMLUtil.encodeXML(xml));
    }
}
