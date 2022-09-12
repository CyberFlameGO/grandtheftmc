package net.grandtheftmc.vice.drug.attribute;

import org.bukkit.event.Event;

public interface DrugAttribute<T extends Event> {
    void onEvent(T event);
}
