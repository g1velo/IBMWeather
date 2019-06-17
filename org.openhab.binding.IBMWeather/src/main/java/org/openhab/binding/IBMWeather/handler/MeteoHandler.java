package org.openhab.binding.IBMWeather.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.TypeParser;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.openhab.binding.IBMWeather.objects.Combo;
import org.openhab.binding.IBMWeather.objects.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MeteoHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ScheduledFuture<?> refreshJob;
    private String user;
    private String password;
    private String url;
    private BigDecimal refresh;
    private ClientConfig clientConfig;
    private Client client;
    private HttpAuthenticationFeature feature;
    private Thing thing;

    public MeteoHandler(Thing thing) {
        super(thing);
        this.thing = thing;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initialize() {
        // TODO Auto-generated method stub
        // super.initialize();
        logger.debug("Initializing meteo Handler");

        Map<String, Object> m = thing.getConfiguration().getProperties();

        this.user = (String) m.get("user");
        this.password = (String) m.get("password");
        this.url = (String) m.get("url");
        this.refresh = (BigDecimal) m.get("refresh");

        logger.debug("config parameter user : {}", this.user);
        logger.debug("config parameter password: {}", this.password);
        logger.debug("config parameter url : {}", this.url);
        logger.debug("config parameter refresh : {}", this.refresh);

        // feature = HttpAuthenticationFeature.basic(this.user, this.password);
        feature = HttpAuthenticationFeature.basic("5be6a164-a186-4d8b-b4b2-0976395f97db", "pgEN3Cw9Qb");
        // logger.debug("feature : {} " , feature.);

        clientConfig = new ClientConfig();
        clientConfig.register(feature);
        client = ClientBuilder.newClient(clientConfig);

        startAutomaticRefresh();
        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        logger.debug("Removing meteo Handler");
        refreshJob.cancel(true);
        updateStatus(ThingStatus.OFFLINE);
        super.dispose();
    }

    private void startAutomaticRefresh() {

        logger.debug("startAutomaticRefresh()");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                logger.debug("Starting target");

                WebTarget webTarget = client.target("https://twcservice.eu-gb.mybluemix.net")
                        .queryParam("language", "fr-FR").queryParam("units", "m")
                        .path("/api/weather/v1/location/34000%3A4%3AFR/observations.json");

                Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
                // logger.debug("Starting target");

                Response response = invocationBuilder.get();
                logger.debug("response : {} ", response.getStatus());

                if (response.getStatus() == 200) {

                    // Jackson code to convert JSON String to Java object
                    ObjectMapper objectMapper = new ObjectMapper();
                    // logger.debug("response : {} ", response.getStatus());

                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                    try {
                        String JSONString = response.readEntity(String.class);
                        logger.debug(JSONString);
                        Combo cs = objectMapper.readValue(JSONString, Combo.class);
                        Observation o = cs.getObservation();

                        List<Channel> lc = thing.getChannels();
                        logger.debug("lc Size  : {} ", lc.size());
                        // NullDetect nd = new NullDetect();

                        for (Channel ch : lc) {
                            logger.debug("In channel : {} {} ", ch.getUID(), ch.getUID().toString().split(":")[3]);
                            // ChannelUID c = new ChannelUID(ch.getUID().toString().split(":")[3]);
                            State s = new DecimalType(0);
                            String state = "";
                            switch (ch.getUID().toString().split(":")[3]) {
                                case "temp":
                                    logger.debug("in temp : {}", o.getTemp());
                                    state = o.getTemp();
                                    break;
                                case "rh":
                                    logger.debug("in rh : {}", o.getRh());
                                    state = o.getRh();
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
                            }

                            logger.warn("TypeParser : {}  ", TypeParser.parseType("StringType", state));
                            logger.warn("TypeParser : {}  ", TypeParser.parseType("DecimalType", state));
                            logger.warn("TypeParser Args : {}  {} ", ch.getAcceptedItemType(), state);

                            if (TypeParser.parseType(ch.getAcceptedItemType(), state) == null) {
                                logger.trace("Channel : {} Will get value : {} ", ch.getUID(), state);
                                updateState(ch.getUID(), s);
                            } else {
                                logger.trace("Channel : {} had wrong type value : {} ", ch.getUID(), state);
                                logger.trace("Was expecting : {} ", ch.getAcceptedItemType());
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    logger.info(String.valueOf(response.getStatus()));
                    logger.info(response.getStatusInfo().toString());
                    logger.error("there was an error");
                }
                response.close();
                logger.debug("end of Runnable");

            }
        };

        logger.warn("refresh in Runnable {} ", refresh.intValue());
        refreshJob = scheduler.scheduleWithFixedDelay(runnable, 10, refresh.intValue(), TimeUnit.SECONDS);
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        // TODO Auto-generated method stub
        logger.warn("reconfiguring : {} ", this.thing.getUID());
        super.handleConfigurationUpdate(configurationParameters);

    }

}
