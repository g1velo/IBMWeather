package org.openhab.binding.IBMWeather.internal;

import static org.openhab.binding.IBMWeather.IBMWeatherConstants.*;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.IBMWeather.handler.HourlyForecastHandler;
import org.openhab.binding.IBMWeather.handler.MeteoHandler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ThingHandlerFactory.class, immediate = true)
public class IBMWeatherHandlerFactory extends BaseThingHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        // TODO Auto-generated method stub
        return SUPPORTED_DEVICE_TYPES_UIDS.contains(thingTypeUID);

        // return false;
        // return true;
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        // TODO Auto-generated method stub

        logger.debug("Creating Meteo Handler {} ", SUPPORTED_DEVICE_TYPES_UIDS);
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        // if (SUPPORTED_DEVICE_TYPES_UIDS.contains(thingTypeUID)) {
        if (thingTypeUID.equals(THING_TYPE_FORECAST)) {
            logger.debug("working on thing type : {}", thingTypeUID);
            return new HourlyForecastHandler(thing);
        }
        if (thingTypeUID.equals(THING_TYPE_METEO)) {
            logger.debug("working on thing type : {}", thingTypeUID);
            return new MeteoHandler(thing);
        }
        return null;
    }

}
