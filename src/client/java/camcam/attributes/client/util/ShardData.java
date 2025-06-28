package camcam.attributes.client.util;

import java.util.Objects;


public record ShardData(String id, int count, double unitPrice) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        ShardData other = (ShardData) obj;
        return this.id().equals(other.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id());
    }
}
