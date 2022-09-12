package net.grandtheftmc.gtm.drugs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Remco on 25-3-2017.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DrugParam {

    FunctionalInterface value();

}
