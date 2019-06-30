package org.openhab.binding.IBMWeather.objects;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.State;

public class ChannelStateObject {

    private ChannelUID chiud;
    private State s;

    public ChannelStateObject() {
    }

    public ChannelStateObject(ChannelUID chuid, State s) {
        this.chiud = chiud;
        this.s = s;
    }

    public State getS() {
        return s;
    }

    public void setS(State s) {
        this.s = s;
    }

    public ChannelUID getChiud() {
        return chiud;
    }

    public void setChiud(ChannelUID chiud) {
        this.chiud = chiud;
    }

}
