package net.grandtheftmc.vice.drug;

public abstract class BaseDrug implements Drug {

    private final String name;

    /**
     * Construct a drug
     */
    public BaseDrug(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
