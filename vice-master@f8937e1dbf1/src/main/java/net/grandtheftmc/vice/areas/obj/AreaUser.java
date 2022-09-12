package net.grandtheftmc.vice.areas.obj;

import java.util.Set;
import java.util.UUID;

public class AreaUser {

    private UUID uuid;
    private int current;
    private Set<Integer> visited;

    public AreaUser(UUID uuid, Set<Integer> visited) {
        this.uuid = uuid;
        this.current = -1; // -1 is considered none
        this.visited = visited;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getCurrent() {
        return this.current;
    }

    public Set<Integer> getVisited() {
        return this.visited;
    }

    public boolean hasVisited(int areaId) {
        return visited.contains(areaId);
    }

    public void setCurrent(int id) {
        this.current = id;
    }
}
