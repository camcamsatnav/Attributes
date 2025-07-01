package camcam.attributes.client.util;

import camcam.attributes.client.AttributesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Bazaar {
    private static final String url = "https://api.hypixel.net/skyblock/bazaar";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();

    private volatile BazaarData data;

    public Bazaar() {
        try {
            this.fetchData();
        } catch (IOException e) {
            AttributesClient.LOGGER.error("Failed to fetch Bazaar data", e);
        }
    }

    private void fetchData() throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(s -> GSON.fromJson(s, BazaarData.class))
                .whenComplete((data, t) -> {
                    if (t != null) AttributesClient.LOGGER.error("Failed to fetch Bazaar data", t);
                    else {
                        this.data = data;
                    }
                });
    }

    public double getPrice(String name) {
        if (data == null) return -1;

        // thanks hypixel
        if (name.equals("SHARD_ABYSSAL_LANTERNFISH")) {
            name = "SHARD_ABYSSAL_LANTERN";
        } else if (name.equals("SHARD_CINDERBAT")) {
            name = "SHARD_CINDER_BAT";
        }

        BazaarData.Product product = data.products().get(name);
        if (product == null) {
            AttributesClient.LOGGER.warn("Item doesn't exist in bazaar data: {}", name);
            return -1;
        }

        return product.quick_status().buyPrice();
    }
}

