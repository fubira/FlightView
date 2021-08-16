package net.ironingot.flightview.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "flightview")
public class FlightViewConfig implements ConfigData {
    public int mode = 0;

	public static FlightViewConfig register(){
		var configHolder = AutoConfig.register(FlightViewConfig.class, GsonConfigSerializer::new);

        return configHolder.getConfig();
	}
}
