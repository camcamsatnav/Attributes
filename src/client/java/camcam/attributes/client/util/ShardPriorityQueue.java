package camcam.attributes.client.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ShardPriorityQueue {

    private final PriorityQueue<ShardData> priceQueue;

    private static final int MAX_SIZE = 5;

    public ShardPriorityQueue() {
        this.priceQueue = new PriorityQueue<>(
                (a, b) -> Double.compare(b.unitPrice() * b.count(), a.unitPrice() * a.count())
        );
    }

    public void add(ShardData data) {
        this.priceQueue.remove(data);
        this.priceQueue.offer(data);
        if (this.priceQueue.size() > MAX_SIZE) {
            this.priceQueue.poll(); // remove the highest total price
        }
    }

    public List<ShardData> getLowest() {
        List<ShardData> result = new ArrayList<>(this.priceQueue);
        result.sort(Comparator.comparingDouble(d -> d.unitPrice() * d.count()));
        return result;
    }

    public void remove(ShardData data) {
        this.priceQueue.remove(data);
    }
}
