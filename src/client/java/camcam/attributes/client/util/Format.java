package camcam.attributes.client.util;

public class Format {
    public static String shardToBzID(String shard) {
        return "SHARD_" + shard.substring(0, shard.length() - 6).toUpperCase().replace(" ", "_");
    }

    public static String bzIDToShard(String id) {
        return id.replace("SHARD_", "").replace("_", " ");
    }

    public static String formatPrice(double price) {
        if (price > 1e6) {
            return Math.round(price / 1e7) + "m";
        } else if (price > 1e3) {
            return Math.round(price / 1e3) + "k";
        } else {
            return Long.toString(Math.round(price));
        }
    }
}
