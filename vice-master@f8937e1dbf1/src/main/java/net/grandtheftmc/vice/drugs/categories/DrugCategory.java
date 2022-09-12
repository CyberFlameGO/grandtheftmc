package net.grandtheftmc.vice.drugs.categories;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.examples.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Remco on 25-3-2017.
 */
public enum DrugCategory {

    STIMULANTS(Stimulants.class),
    INHALANTS(Inhalants.class),
    CANNABINOIDS(Cannabinoids.class),
    DEPRESSANTS(Depressants.class),
    OPIOIDS(Opioids.class),
    ANABOLIC_STEROIDS(AnabolicSteroids.class),
    HALLUCINOGENS(Hallucinogens.class),
    ALCOHOL(AAlcohol.class),
    PRESCRIPTION_DRUGS(PrescriptionDrugs.class);

    private final Class<?>[] categories;

    DrugCategory(Class<?>... categories) {
        this.categories = categories;
    }

    public Class<?>[] getCategories() {
        return categories;
    }

    public static Optional<DrugCategory> byDrug(Drug drug){
        return Arrays.stream(values()).filter((categorie) -> Arrays.stream(categorie.categories).anyMatch((categoryClass) -> categoryClass.equals(drug.getClass().getSuperclass()))).findFirst();
    }
}
