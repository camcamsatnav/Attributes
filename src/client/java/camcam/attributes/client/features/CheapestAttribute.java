package camcam.attributes.client.features;

import camcam.attributes.client.AttributesClient;
import camcam.attributes.client.util.Bazaar;
import camcam.attributes.client.util.Format;
import camcam.attributes.client.util.ShardData;
import camcam.attributes.client.util.ShardPriorityQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheapestAttribute {

    private static final Pattern SHARD_PATTERN = Pattern.compile("\\bSource: ([\\w\\s]+) \\([A-Z]\\d+\\)");

    private final Bazaar bazaar;

    private final ShardPriorityQueue shardQueue;

    public CheapestAttribute() {
        bazaar = new Bazaar();
        this.shardQueue = new ShardPriorityQueue();
    }

    public void onTick(MinecraftClient client) {
        if (client.player == null || !(client.currentScreen instanceof GenericContainerScreen) || !client.currentScreen.getTitle().getString().equals("Attribute Menu")) return;

        for (Slot slot : client.player.currentScreenHandler.slots) {
            if (slot.id < 10) continue;
            if (slot.id > 43) return;
            ItemStack itemStack = slot.getStack();
            LoreComponent loreComponent = itemStack.getComponents().get(DataComponentTypes.LORE);

            if (loreComponent == null || loreComponent.lines().size() < 10) continue;

            String shard = null;
            int count = -1;
            for (Text component : loreComponent.lines()) {
                if (shard == null) {
                    Matcher shardMatcher = SHARD_PATTERN.matcher(component.getString());
                    if (shardMatcher.find()) {
                        shard = shardMatcher.group(1);
                    }
                }

                if (count == -1) {
                    if (component.getString().startsWith("Syphon ")) {
                        count = Integer.parseInt(component.getString().split(" ")[1]);
                    }
                }
            }

            if (shard == null) {
                AttributesClient.LOGGER.warn("Shard name not found");
                return;
            }

            if (count == -1) {
                // case where u just upgrade to lvl 10, needs to be removed
                this.shardQueue.remove(new ShardData(Format.shardToBzID(shard), 0, 0));
                continue;
            }
            double price = this.bazaar.getPrice(Format.shardToBzID(shard));
            if (price == -1) continue;

            this.shardQueue.add(new ShardData(Format.shardToBzID(shard), count, price));
            /*
             * lore lines
             * 0 -> type (eg Foraging)
             * 1 -> empty
             * 2 -> description
             * 3 -> empty
             * 4 -> Source: Phanpyre Shard (C4)
             * 5 -> Rarity: COMMON
             * 6 -> Enabled: Yes
             * 7 -> empty
             * 8 -> Level: 5 / Left-Click to open!
             * 9 -> Syphon 4 more to level up! / Right-Click to toggle!
             * 10 -> empty -> only if yes more upgrades
             * 11 -> Left-Click to open!
             * 12 -> Right-Click to toggle!
             */
        }
    }

    public void render(DrawContext drawContext) {
        if (!AttributesClient.CONFIG.mainToggle) return;
        List<ShardData> lowest = this.shardQueue.getLowest();
        for (int i = 0; i < lowest.size(); i++) {
            ShardData shard = lowest.get(i);
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, String.format("%s x %d for %s", Format.bzIDToShard(shard.id()), shard.count(), Format.formatPrice(shard.unitPrice() * shard.count())), 2, 2 + 16 * i, AttributesClient.CONFIG.overlayColour, true);
        }
    }
}
