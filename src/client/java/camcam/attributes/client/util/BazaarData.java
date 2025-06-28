package camcam.attributes.client.util;

import java.util.Map;

public record BazaarData(boolean success, long lastUpdated, Map<String, Product> products) {
    public record Product(QuickStatus quick_status) {}

    public record QuickStatus(double buyPrice) {}
}
