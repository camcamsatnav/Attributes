package camcam.attributes.client.features;

import camcam.attributes.client.AttributesClient;
import camcam.attributes.client.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheapestAttribute {

    private static final Pattern SHARD_PATTERN = Pattern.compile("\\bSource: ([\\w\\s]+) \\([A-Z]\\d+\\)");

    private final Bazaar bazaar;

    private Map<String, Map<Integer, Integer>> attributeLevels;

    private final Set<Shard> shardSet;

    public CheapestAttribute() {
        bazaar = new Bazaar();
        this.shardSet = new HashSet<>();
        this.getAttributeLevels();
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
            String rarity = null;
            int level = 0;
            int count = -1;

            for (Text component : loreComponent.lines()) {
                if (shard == null) {
                    Matcher shardMatcher = SHARD_PATTERN.matcher(component.getString());
                    if (shardMatcher.find()) {
                        shard = shardMatcher.group(1);
                    }
                }

                if (rarity == null) {
                    if (component.getString().startsWith("Rarity: ")) {
                        rarity = component.getString().split(" ")[1];
                    }
                }

                if (level == 0) {
                    if (component.getString().startsWith("Level: ")) {
                        level = Integer.parseInt(component.getString().split(" ")[1]);
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

            if (rarity == null) {
                AttributesClient.LOGGER.warn("Shard rarity not found");
                return;
            }

            if (count == -1) {
                // case where u just upgrade to lvl 10, needs to be removed
                this.shardSet.remove(new Shard(Format.shardToBzID(shard), 0, 0, 0));
                continue;
            }

            int countMax = this.attributeLevels.get(rarity).get(10) - (this.attributeLevels.get(rarity).get(level + 1) - count);

            double price = this.bazaar.getPrice(Format.shardToBzID(shard));

            if (price == -1) continue;

            // update the set to contain latest shard information
            this.shardSet.remove(new Shard(Format.shardToBzID(shard), 0, 0, 0));

            this.shardSet.add(new Shard(Format.shardToBzID(shard), count, price, countMax));
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

        List<Shard> shards = this.shardSet.stream().sorted(Comparator.comparingDouble(s -> (AttributesClient.CONFIG.sortByMax ? s.countMax() : s.count()) * s.unitPrice())).toList();

        for (int i = 0; i < Math.min(AttributesClient.CONFIG.numberOfShards, shards.size()); i++) {
            Shard shard = shards.get(i);
            drawContext.drawText(MinecraftClient.getInstance().textRenderer,
                    String.format("%s x %d for %s. %s", Format.bzIDToShard(shard.id()), shard.count(), Format.formatPrice(shard.unitPrice() * shard.count()),
                            AttributesClient.CONFIG.showCountToMax ? String.format("%s to max (%d)", Format.formatPrice(shard.unitPrice() * shard.countMax()), shard.countMax()) : ""),
                    2, 2 + 16 * i, AttributesClient.CONFIG.overlayColour, true);
        }
        if (AttributesClient.CONFIG.showTotalCost && !this.shardSet.isEmpty()) {
            double totalCost = this.shardSet.stream().mapToDouble(s -> s.countMax() * s.unitPrice()).sum();
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, String.format("Total cost: %s", Format.formatPrice(totalCost)), 2, 2 + 16 * (AttributesClient.CONFIG.numberOfShards + 1), AttributesClient.CONFIG.overlayColour, true);
        }
    }

    public void getAttributeLevels() {
        Gson gson = new GsonBuilder().create();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(CheapestAttribute.class.getClassLoader().getResourceAsStream("assets/attributes/data/attributeLevels.json")))) {
            AttributeLevels parsed = gson.fromJson(reader, AttributeLevels.class);

            this.attributeLevels = Map.of(
                    "COMMON", parsed.COMMON(),
                    "UNCOMMON", parsed.UNCOMMON(),
                    "RARE", parsed.RARE(),
                    "EPIC", parsed.EPIC(),
                    "LEGENDARY", parsed.LEGENDARY()
            );
        } catch (IOException ignored) {
            AttributesClient.LOGGER.error("Error loading attribute levels");
        }
    }
}
