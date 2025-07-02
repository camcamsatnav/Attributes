package camcam.attributes.client.util;

import java.util.Objects;


public record Shard(String id, int count, double unitPrice, int countMax) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        Shard other = (Shard) obj;
        return this.id().equals(other.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id());
    }
}
