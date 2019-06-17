package org.openhab.binding.IBMWeather.objects;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.TypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullDetect {

    private static Logger logger = LoggerFactory.getLogger(NullDetect.class);

    // public static byte[] readBytes(int numBytes, ByteArrayInputStream inputStream) {
    public static State nullCheck(Channel ch, String value) {
        // String c = ch.getAcceptedItemType();
        NullDetect.logger.debug("Accepted type is : {} ", ch.getAcceptedItemType());
        TypeParser.parseType(ch.getAcceptedItemType(), value);
        return null;
    }

}
