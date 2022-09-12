package net.grandtheftmc.Bungee.help;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.help.data.HelpCategory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.config.Configuration;

import java.util.*;

/**
 * Created by Adam on 05/06/2017.
 */
public class HelpCore {

    /*
        Sample Data: (Tab to delimit keys for multiple checks)
        ------------------------------------------------------

        help:
          topic:
            matches: []
            help: []
            subtopic:
              matches: []
              help: [] - Can be empty or not, doesn't matter

       Example:
       --------

        help:
         Vehicles:
           matches: vehicle,vehicles
           help: []
           Jetpacks:
             matches:
             - 'jetpack'
             - 'jetpacks'
             help:
             - 'Jetpacks are cool.'
             - 'Go buy a Jetpack.'
           Wingsuits:
             matches:
             - 'fly'
             - 'wingsuit'
             - 'wingsuits'
             - 'wings'
             help:
             - 'Wingsuits are faster than JetPacks.'
             - 'Or, are they'


     */

    /**
     * A list of all the root categories.
     */
    private List<HelpCategory> rootCategories;

    private List<String> footer;
    private List<String> header;
    private String pathString, pathDelim, relatedString, title, cats, helpPath, helpMatch, sendHelp;

    /**
     * Map match strings to each help category.
     */
    private Map<String, HelpCategory> mappings = new HashMap<>();

    private List<BaseComponent[]> mainHelpMenu;

    /**
     * Create an instance of the help core, load in all the keywords from a config.
     *
     * @param io The config to load the help data from.
     */
    public HelpCore(Configuration io) {
        load(io);
    }

    public void reload() {
        rootCategories.clear();
        header.clear();
        footer.clear();
        mappings.clear();
        mainHelpMenu = null;

        Bungee.getSettings().setHelpConfig(Utils.loadConfig("help"));
        load(Bungee.getSettings().getHelpConfiguration());
    }

    private void load(Configuration io) {

        rootCategories = new ArrayList<>();

        header = io.getStringList("formatting.header");
        footer = io.getStringList("formatting.footer");
        pathString = Utils.f(io.getString("formatting.path"));
        pathDelim = Utils.f(io.getString("formatting.pathdelimiter"));
        relatedString = Utils.f(io.getString("formatting.related"));
        title = Utils.f(io.getString("formatting.title"));
        cats = Utils.f(io.getString("formatting.categories"));
        helpPath = Utils.f(io.getString("formatting.helppath"));
        helpMatch = Utils.f(io.getString("formatting.helpmatch"));
        sendHelp = Utils.f(io.getString("formatting.sendhelp"));

        for (String s : io.getSection("help").getKeys()) {
            //Each key here is a root category

            HelpCategory rootCategory = new HelpCategory(io.getSection("help." + s), null, s);
            rootCategories.add(rootCategory);
        }

        //Now traverse the tree to add all matches
        for (HelpCategory rootCat : rootCategories) {
            associateMappings(rootCat);
        }
    }

    private void associateMappings(HelpCategory category) {
        //Associate mappings with this category
        for (String s : category.getMatches()) {
            mappings.put(s, category);
        }

        //Now do the same for all children, recursively...
        category.getChildren().forEach(this::associateMappings);
    }

    /**
     * Return the help category associated with this search string.
     *
     * @param s The string to check for matches.
     * @return The associated help category.
     */
    public HelpCategory getAssociatedCategory(String s) {
        return mappings.get(s.toLowerCase());
    }

    public List<BaseComponent[]> getMainHelpMenu() {
        if (mainHelpMenu == null) {
            mainHelpMenu = new ArrayList<>();

            //add headers
            getHeader().forEach(h -> mainHelpMenu.add(new ComponentBuilder(Utils.f(h)).create()));
            mainHelpMenu.add(new ComponentBuilder(" " + title).create());

            //related Categories
            ComponentBuilder builder = new ComponentBuilder(" " + cats);

            for (int i = 0; i < rootCategories.size(); i++) {
                HelpCategory cat = rootCategories.get(i);
                builder.append(cat.getDisplayName()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + cat.getSectionName()));

                if ((i + 1) != rootCategories.size()) {
                    //comma delimiters
                    builder.append(", ").color(ChatColor.WHITE);
                }
            }
            //add categories
            mainHelpMenu.add(builder.create());

            //add footers
            getFooter().forEach(h -> mainHelpMenu.add(new ComponentBuilder(Utils.f(h)).create()));
        }

        return mainHelpMenu;
    }

    public List<String> getFooter() {
        return footer;
    }

    public List<String> getHeader() {
        return header;
    }

    public String getPathString() {
        return pathString;
    }

    public String getPathDelim() {
        return pathDelim;
    }

    public String getRelatedString() {
        return relatedString;
    }

    public String getHelpPath() {
        return helpPath;
    }

    public String getHelpMatch() {
        return helpMatch;
    }

    public String getSendHelp() {
        return sendHelp;
    }
}
