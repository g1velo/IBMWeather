package org.openhab.binding.IBMWeather.objects;

import java.util.List;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.TypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationParser {

    private static Logger logger = LoggerFactory.getLogger(NullDetect.class);

    public static ChannelStateObject setAll(Observation observation, Thing thing) {
        Observation o = observation;
        Thing t = thing;
        ChannelStateObject cso = new ChannelStateObject();

        List<Channel> lc = t.getChannels();
        logger.debug("lc Size  : {} ", lc.size());

        for (Channel ch : lc) {
            logger.warn("In channel : {} {} ", ch.getUID(), ch.getUID().toString().split(":")[3]);
            String state = "";
            boolean processIt = true;

            switch (ch.getUID().toString().split(":")[3]) {
                case "temp":
                    logger.debug("in temp : {}", o.getTemp());
                    state = o.getTemp();
                    break;
                case "rh":
                    logger.debug("in rh : {}", o.getRh());
                    state = o.getRh();
                    break;
                case "pressure":
                    logger.debug("in pressure: {}", o.getRh());
                    state = o.getPressure();
                    break;
                case "wx_phrase":
                    logger.debug("in wx_phrase : {} ", o.getWx_phrase());
                    state = o.getWx_phrase();
                    break;
                case "wdir":
                    logger.debug("in wx_dir : {} ", o.getWdir());
                    TypeParser.parseType(ch.getAcceptedItemType(), o.getWdir());
                    state = o.getWdir();
                    break;
                case "precip_total":
                    logger.debug("in precip_total {} ", o.getPrecip_total());
                    state = o.getPrecip_total();
                    break;
                case "wdir_cardinal":
                    logger.debug("in wdir_cardinal : ", o.getWdir_cardinal());
                    state = o.getWdir_cardinal();
                    break;
                case "wspd":
                    logger.debug("in wspd: {} ", o.getWspd());
                    state = o.getWspd();
                    break;
                case "precip_hrly":
                    logger.debug("in precip_hrly: {} ", o.getPrecip_hrly());
                    state = o.getPrecip_hrly();
                    break;
                case "uv_desc":
                    logger.debug("in uv_desc: {} ", o.getUv_desc());
                    state = o.getUv_desc();
                    break;
                case "uv_index":
                    logger.debug("in uv_index : {} ", o.getUv_index());
                    state = o.getUv_index();
                    break;
                default:
                    logger.warn("I don't know  : {} ", ch.getUID().toString().split(":")[3]);
                    processIt = false;
                    break;
            }

            logger.warn("channel : {} set to : {}  accepted : {}", ch.getUID(), state, ch.getAcceptedItemType());

            if (processIt && state != null) {
                String type = new String();
                State s = new DecimalType(0);

                switch (ch.getAcceptedItemType()) {
                    case "String":
                        logger.warn("Accepted Type by {} is String", ch.getUID());
                        type = "StringType";
                        s = new StringType(state);
                        break;
                    case "Number":
                        logger.warn("Accepted Type by {} is Number", ch.getUID());
                        type = "DecimalType";
                        s = new DecimalType(state);
                        break;
                }

                if (TypeParser.parseType(type, state) != null) {
                    logger.warn("Channel : {} Will get value : {} ", ch.getUID(), state);
                    cso.setChiud(ch.getUID());
                    cso.setS(s);
                    // updateState(ch.getUID(), s);

                } else {
                    logger.warn("Channel : {} had wrong type value : {} ", ch.getUID(), state);
                    logger.warn("Was expecting : {} ", ch.getAcceptedItemType());
                }
            }
        }
        return cso;
    }
}
