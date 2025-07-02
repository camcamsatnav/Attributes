package camcam.attributes.client.util;

import java.util.Map;

public record AttributeLevels(
        Map<Integer, Integer> COMMON,
        Map<Integer, Integer> UNCOMMON,
        Map<Integer, Integer> RARE,
        Map<Integer, Integer> EPIC,
        Map<Integer, Integer> LEGENDARY
) {
}
