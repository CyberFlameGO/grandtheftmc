package net.grandtheftmc.vice.users;

import net.grandtheftmc.core.util.Utils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum CopRank {

    COP("&b", 10000, Material.CHEST,"essentials.sethome.multiple","essentials.sethome.multiple.2"),
    NARC("&b", 25000, Material.STICK,"essentials.sethome.multiple.3"),
    LIEUTENANT("&b", 50000, Material.IRON_BARDING,"essentials.sethome.multiple.5"),
    CAPTAIN("&b", 100000, Material.SHIELD,"essentials.sethome.multiple.10"),
    WARDEN("&3", 250000, Material.BLAZE_ROD,"essentials.sethome.multiple.unlimited");

    private final String color;
    private final int salary;
    private final List<String> perms;
    private final Material material;


    CopRank(String color, int salary, Material material, String... perms) {
        this.color = color;
        this.salary = salary;
        this.perms = Arrays.asList(perms);
        this.material = material;
    }

    public List<String> getAllPerms() {
        List<String> permissions = new ArrayList<>();
        for (CopRank c : CopRank.values()) {
            permissions.addAll(c.perms);
            if (c == this)
                return permissions;
        }
        return permissions;
    }

    private List<String> getPerms() {
        return this.perms;
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

    public CopRank getNext() {
        String rankName = this.getName();
        if ("GODFATHER".equalsIgnoreCase(rankName))
            return null;
        int go = 0;

        CopRank rank = null;
        for (CopRank r : CopRank.values())
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

    public static CopRank fromString(String string) {
        return Arrays.stream(CopRank.values()).filter(uc -> uc.getName().equalsIgnoreCase(string)).findFirst().orElse(CopRank.COP);
    }

    public Material getMaterial() {
        return this.material;
    }

    public static CopRank getRankOrNull(String name) {
        if (name == null)
            return null;
        return Arrays.stream(CopRank.values()).filter(r -> r.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isHigherThan(CopRank rank) {
        for (CopRank r : CopRank.values())
            if (r == this)
                return false;
            else if (r == rank)
                return true;
        return false;
    }

    public double getSalary() {
        return this.salary;
    }
}
