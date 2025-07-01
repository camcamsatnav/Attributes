package camcam.attributes.client;

import camcam.attributes.client.commands.ConfigCommand;
import camcam.attributes.client.config.AttributeConfig;
import camcam.attributes.client.features.CheapestAttribute;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributesClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("attributes");

    private static final Identifier SHARD_LAYER = Identifier.of("attributes", "shard_layer");

    public static AttributeConfig CONFIG;

    public CheapestAttribute cheapestAttribute;

    public AttributesClient() {
        cheapestAttribute = new CheapestAttribute();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Attributes Initialized");

        AutoConfig.register(AttributeConfig.class, GsonConfigSerializer::new);
        AttributesClient.CONFIG = AutoConfig.getConfigHolder(AttributeConfig.class).getConfig();

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, SHARD_LAYER, (drawContext, counter) -> cheapestAttribute.render(drawContext)));

        ClientCommandRegistrationCallback.EVENT.register(ConfigCommand::register);

        ClientTickEvents.END_CLIENT_TICK.register(client -> cheapestAttribute.onTick(client));
    }
}
