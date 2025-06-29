package camcam.attributes.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "attributes")
public class AttributeConfig implements ConfigData {

    @ConfigEntry.Category("General")
    @Comment("Main toggle to display the cheapest attributes overlay")
    public boolean mainToggle = false;
}
