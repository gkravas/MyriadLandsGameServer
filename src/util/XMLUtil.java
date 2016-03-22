package util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author George Kravas
 */
public class XMLUtil {

    public static final String MESSAGE_CHARSET = "UTF-8";

    public static Document createXMLDocument(ByteBuffer message) throws CharacterCodingException, JDOMException, IOException {
        SAXBuilder xmlBuilder = new SAXBuilder();
        String XMLMessage = decodeString(message);
        Document doc = xmlBuilder.build(new StringReader(XMLMessage));
        return doc;
    }

    public static ByteBuffer encodeXML(Document xml) {
        try {
            return ByteBuffer.wrap(new XMLOutputter().outputString(xml).getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }

    /**
    * Encodes a {@code String} into a {@link ByteBuffer}.
    *
    * @param s the string to encode
    * @return the {@code ByteBuffer} which encodes the given string
    */
    public static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }
    /**
    * Decodes a message into a {@code String}.
    *
    * @param message the message to decode
    * @return the decoded string
    */
    public static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, MESSAGE_CHARSET).substring(2);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }
}
