package camcam.attributes.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "attributes")
public class AttributeConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean mainToggle = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public int numberOfShards = 5;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean useBuyOrder = false;

    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public int overlayColour = 0xFFFFFF00;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean showCountToMax = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean sortByMax = false;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean showTotalCost = false;
}
