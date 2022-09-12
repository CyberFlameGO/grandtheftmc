package com.j0ach1mmall3.wastedguns;
/*
 * Copyright (C) 2013-Current Carter Gale (Ktar5) <buildfresh@gmail.com>
 *
 * This file is part of wastedguns.
 *
 * wastedguns can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */

import org.bukkit.ChatColor;

import java.util.List;

public class StringUtil {
    /**
     * Quickly color a string
     */
    public static String colorString(String input) {
        return input != null ? ChatColor.translateAlternateColorCodes('&', input) : null;
    }

    /**
     * Quickly remove color from a string
     */
    public static String removeColor(String input) {
        return ChatColor.stripColor(input);
    }

    /**
     * Color a list..
     */
    public static List<String> colorList(List<String> inputs) {
        if (inputs == null) {
            return null;
        }

        for (int i = 0; i < inputs.size(); i++)
            inputs.set(i, colorString(inputs.get(i)));
        return inputs;
    }

    /**
     * Color us some strings matey!
     */
    public static String[] colorArray(String[] inputs) {
        for (int i = 0; i < inputs.length; i++)
            inputs[i] = colorString(inputs[i]);
        return inputs;
    }

    public static String[] colorArray(String[] inputs, ChatColor color) {
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = color + inputs[i];
        }

        return inputs;
    }

    /**
     * Limits a string to an upper bound for its length
     *
     * @param string The string to limit
     * @param limit  The upper bound value length (char count)
     * @return Returns the chopped up string if it goes over the limite, otherwise returns string
     */
    public static String limitString(String string, int limit) {
        return string.substring(0, Math.min(string.length(), limit));
    }

    public static String replace(String input, String toReplace, String replaceWith) {
        return input.replace(toReplace, replaceWith);
    }

    public static String replaceAndColor(String input, String toReplace, String replaceWith) {
        return colorString(replace(input, toReplace, replaceWith));
    }
}
