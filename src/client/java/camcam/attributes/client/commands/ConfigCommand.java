package camcam.attributes.client.commands;

import camcam.attributes.client.config.AttributeConfig;
import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(literal("attr").executes(ctx -> run(ctx.getSource())));
    }
    public static int run(FabricClientCommandSource source) {
        source.getClient().send(() -> source.getClient().setScreen(AutoConfig.getConfigScreen(AttributeConfig.class, MinecraftClient.getInstance().currentScreen).get()));
        return 1;
    }
}
