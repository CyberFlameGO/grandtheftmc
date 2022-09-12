package net.grandtheftmc.vice.users;

import net.grandtheftmc.core.util.Utils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum ViceRank {

    JUNKIE(0, "&7", Material.CHEST), // 1 sethome
    FALCON(25000, "&e", Material.FEATHER), // 1 auction item
    THUG(100000, "&e", Material.STICK,"essentials.workbench"), // 2 auction items, /workbench
    DEALER(250000, "&e", Material.SUGAR),
    GROWER(500000, "&e", Material.SEEDS), //2 sethomes, create faction
    SMUGGLER(1000000, "&e", Material.CHEST),
    CHEMIST(2500000, "&e", Material.BREWING_STAND_ITEM), // 3 auction items
    DRUGLORD(5000000, "&e", Material.NETHER_STAR), // 4 auction items
    KINGPIN(10000000, "&e", Material.CHORUS_FRUIT_POPPED),// 3 sethomes,
    GODFATHER(25000000, "&e", Material.RED_ROSE, "essentials.enderchest"); // 5 auction items

    private final int price;
    private final String color;
    private final List<String> perms;
    private final Material material;

    ViceRank(int price, String color, Material material, String... perms) {
        this.price = price;
        this.color = color;
        this.perms = Arrays.asList(perms);
        this.material = material;
    }

    public List<String> getAllPerms() {
        List<String> permissions = new ArrayList<>();
        for (ViceRank c : ViceRank.values()) {
            permissions.addAll(c.perms);
            if (c == this)
                return permissions;
        }
        return permissions;
    }

    private List<String> getPerms() {
        return this.perms;
    }

    public int getPrice() {
        return this.price;
    }

    public String getName() {
        return this.toString();
    }

    public String getColor() {
        return this.color;
    }

    public String getColoredName() {
        return Utils.f(this.color + this.getName() + "&r");
    }

    public String getColoredNameBold() {
        return Utils.f(this.color + "&l" + this.getName() + "&r");
    }

    public ViceRank getNext() {
        String rankName = this.getName();
        if (this == GODFATHER) return null;
        int go = 0;

        ViceRank rank = null;
        for (ViceRank r : ViceRank.values())
            if (go == 0) {
                if (Objects.equals(r.getName(), rankName)) {
                    go = 1;
                }
            } else if (go == 1) {
                rank = r;
                break;
            }
        return rank;
    }

    public static ViceRank fromString(String string) {
        return Arrays.stream(ViceRank.values()).filter(uc -> uc.getName().equalsIgnoreCase(string)).findFirst().orElse(ViceRank.JUNKIE);
    }

    public Material getMaterial() {
        return this.material;
    }

    public static ViceRank getRankOrNull(String name) {
        if (name == null)
            return null;
        return Arrays.stream(ViceRank.values()).filter(r -> r.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isHigherThan(ViceRank rank) {
        for (ViceRank r : ViceRank.values())
            if (r == this)
                return false;
            else if (r == rank)
                return true;
        return false;
    }
}
