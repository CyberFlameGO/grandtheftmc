package net.grandtheftmc.core.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Luke Bingham on 01/08/2017.
 */
public class NumeralUtil {
    /**
     * If the given Number is, 29956
     * The returned state will be, 29,956.00
     */
    public static <T extends Number> String toCurrency(T number) {
        return String.format("%,.2f", number);
    }

    /**
     * If the given Number is, 29956
     * If the symbol depends on the Locale.
     *    en_US = $
     *    en_GB = £
     *
     * The returned state will be, $29,956.00
     */
    public static <T extends Number> String toCurrency(T number, Locale locale) {
        return NumberFormat.getCurrencyInstance(locale).format(number);
    }
}
