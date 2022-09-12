package net.grandtheftmc.Bungee.help.data;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Adam on 05/06/2017.
 */
public class HelpCategory {

    /**
     * A name representing this section. Eg: Vehicles.
     */
    private String sectionName;

    /**
     * The display string to show in the menus.
     */
    private String displayName;

    /**
     * A list of help categories for this node, to support sub-categories.
     */
    private List<HelpCategory> subCategories = new ArrayList<>();

    /**
     * All lowercase, any strings in this set will be used to check matches for automated help.
     */
    private String[] matches;

    /**
     * A list of help messages for this topic.
     */
    private String[] help;

    /**
     * The parent help category of this child, null if a root.
     */
    private HelpCategory parent;

    /**
     * A Path representing the relative location of this category.
     * Like Vehicles > Cars > Lambourghini
     */
    private BaseComponent[] path;

    /**
     * A list of display components, pre-compiled on first request.
     */
    private List<BaseComponent[]> display;

    public HelpCategory(Configuration section, HelpCategory parent, String name) {
        //Always pass in the root of whatever config section we are processing.

        this.sectionName = name;
        this.parent = parent;
        this.displayName = section.contains("display") ? ChatColor.translateAlternateColorCodes('&', section.getString("display")) : name;

        //Load Help
        List<String> helpList = section.getStringList("help");
        help = helpList.toArray(new String[helpList.size()]);

        //Load Matches
        List<String> helpMatches = section.getStringList("matches");
        //Ensure all values are lowercase.
        helpMatches.forEach(m -> m = m.toLowerCase());
        matches = helpMatches.toArray(new String[helpMatches.size()]);

        //Bungee.getInstance().getLogger().info("Loading category: " + sectionName);
        //Bungee.getInstance().getLogger().info("Loading related subcategories...");

        //Load SubCategories Recursively
        for (String s : section.getKeys()) {
            //Bungee.getInstance().getLogger().info("Key = " + s);
            if (!s.equalsIgnoreCase("help") && !s.equalsIgnoreCase("matches") && !s.equalsIgnoreCase("display")) {
                //we have found a sub category
                //Bungee.getInstance().getLogger().info("Loading subcategory: " + sectionName + "->" + s);
                HelpCategory cat = new HelpCategory(section.getSection(s), this, s);
                subCategories.add(cat);
            }
        }

        //Bungee.getInstance().getLogger().info("Loading category: " + sectionName + " = COMPLETE");
    }

    public List<HelpCategory> getChildren() {
        return subCategories;
    }

    public String[] getMatches() {
        return matches;
    }

    public HelpCategory getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getSectionName() {
        return sectionName;
    }

    /**
     * Get a display list of text components to show to the player.
     *
     * @return A list of display components.
     */
    public List<BaseComponent[]> getDisplay() {

        if (display == null) {
            display = new ArrayList<>();

            //add headers
            Bungee.getInstance().getHelpCore().getHeader().forEach(h -> display.add(new ComponentBuilder(Utils.f(h)).create()));
            display.add(getPath());

            //related Categories
            ComponentBuilder builder = new ComponentBuilder(" " + Bungee.getInstance().getHelpCore().getRelatedString());

            for (int i = 0; i < getChildren().size(); i++) {
                HelpCategory cat = getChildren().get(i);
                builder.append(cat.getDisplayName()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + cat.getSectionName()));

                if ((i + 1) != getChildren().size()) {
                    //comma delimiters
                    builder.append(", ").color(ChatColor.WHITE);
                }
            }
            //add categories
            if (!getChildren().isEmpty()) {
                //only show related categories if there are children
                display.add(builder.create());
            }

            //display remaining help
            if (help != null && help.length > 0) {
                //Only show help if there's some.
                for (String s : help) {
                    display.add(new ComponentBuilder(" " + ChatColor.translateAlternateColorCodes('&', s)).create());
                }
            }

            //add footer
            Bungee.getInstance().getHelpCore().getFooter().forEach(h -> display.add(new ComponentBuilder(Utils.f(h)).create()));
        }

        return display;
    }

    /**
     * Get a path display string of the current route for this help category.
     *
     * @return
     */
    public BaseComponent[] getPath() {
        if (path == null) {
            //build path
            List<String> route = new ArrayList<>();
            List<String> displayRoute = new ArrayList<>();
            route.add(sectionName);
            displayRoute.add(displayName);

            HelpCategory currentCat = this;

            while (currentCat.hasParent()) {
                route.add(currentCat.getParent().getSectionName());
                displayRoute.add(currentCat.getParent().getDisplayName());
                currentCat = currentCat.getParent();
            }

            //Add core help.
            route.add("Help");
            displayRoute.add(Bungee.getInstance().getHelpCore().getHelpPath());

            Collections.reverse(route);
            Collections.reverse(displayRoute);

            StringBuilder b = new StringBuilder();
            route.stream().forEach(p -> b.append(p).append(" > "));
            //Trim trailing path markers
            b.setLength(b.length() - Bungee.getInstance().getHelpCore().getPathDelim().length());

            ComponentBuilder builder = new ComponentBuilder(" " + Bungee.getInstance().getHelpCore().getPathString());
            for (int i = 0; i < route.size(); i++) {
                String r = route.get(i);
                //add displayRoute.get(i)
                builder.append(displayRoute.get(i));

                if ((i + 1) != route.size()) {
                    //path flow object
                    builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + r));
                    builder.append(Bungee.getInstance().getHelpCore().getPathDelim());
                } else {
                    //We don't want them to be able to click the last thing, so use this tag to negate it
                    builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help view-null"));
                }
            }

            path = builder.create();
        }

        return path;
    }


}
