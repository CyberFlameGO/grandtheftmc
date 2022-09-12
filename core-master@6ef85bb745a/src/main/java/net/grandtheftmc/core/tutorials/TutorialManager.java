package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.SoundEffect;
import net.grandtheftmc.core.util.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager implements Component<TutorialManager, Core> {

    private List<Tutorial> tutorials = new ArrayList<>();
    private Map<String, Tutorial> mappedTuts = new HashMap<>();

    public TutorialManager() {
        this.load();
    }

    @Override
    public TutorialManager onDisable(Core plugin) {
        this.tutorials.forEach(t -> t.getSlides().clear());
        this.tutorials.clear();

        mappedTuts.clear();
        return this;
    }

    public List<Tutorial> getTutorials() {
        return this.tutorials;
    }

    public Tutorial getTutorial(String name) {
        for (Tutorial tutorial : this.tutorials)
            if (tutorial.getName().equalsIgnoreCase(name))
                return tutorial;
        return null;
    }

    public List<Tutorial> getTutorials(int start, int end) {
        List<Tutorial> l = new ArrayList<>();
        for (int i = 0; i < this.tutorials.size(); i++)
            if (i >= start && i <= end)
                l.add(this.tutorials.get(i));
        return l;
    }

    public void addTutorial(Tutorial tutorial) {
        this.tutorials.add(tutorial);
    }

    public void removeTutorial(Tutorial tutorial) {
        this.tutorials.remove(tutorial);
    }

    public void load() {
        YamlConfiguration c = Core.getSettings().getTutorialsConfig();
        this.tutorials = new ArrayList<>();
        for (String name : c.getKeys(false)) {
            try {
                boolean invisible = c.get(name + ".invisible") != null && c.getBoolean(name + ".invisible");
                boolean playersInvisible = c.get(name + ".playersInvisible") != null && c.getBoolean(name + ".playersInvisible");
                List<Slide> slides = new ArrayList<>();
                if (c.get(name + ".slides") != null)
                    for (String s : c.getConfigurationSection(name + ".slides").getKeys(false)) {
                        boolean canConfirm = c.get(name + ".slides." + s + ".canConfirm") != null && c.getBoolean(name + ".slides." + s + ".canConfirm");
                        int delay = c.get(name + ".slides." + s + ".delay") == null ? -1 : c.getInt(name + ".slides." + s + ".delay");
                        Location location = Utils.teleportLocationFromString(c.getString(name + ".slides." + s + ".location"));
                        GameMode gameMode = c.get(name + ".slides." + s + ".gameMode") == null ? null : GameMode.valueOf(c.getString(name + ".slides." + s + ".gameMode"));
                        String actionBarMessage = c.getString(name + ".slides." + s + ".actionBarMessage");
                        List<String> msges = c.getStringList(name + ".slides." + s + ".messages");
                        String[] messages = c.get(name + ".slides." + s + ".messages") == null ? null : msges.toArray(new String[msges.size()]);
                        boolean headerAndFooter = c.getBoolean(name + ".slides." + s + ".headerAndFooter");
                        Title title = null;
                        if (c.get(name + ".slides." + s + ".title") != null) {
                            String string = c.getString(name + ".slides." + s + ".title.title");
                            String subtitle = c.getString(name + ".slides." + s + ".title.subtitle");
                            int fadeIn = c.get(name + ".slides." + s + ".title.fadeIn") == null ? 0 : c.getInt(name + ".slides." + s + ".title.fadeIn");
                            int stay = c.get(name + ".slides." + s + ".title.stay") == null ? 0 : c.getInt(name + ".slides." + s + ".title.stay");
                            int fadeOut = c.get(name + ".slides." + s + ".title.fadeOut") == null ? 0 : c.getInt(name + ".slides." + s + ".title.fadeOut");
                            title = new Title(string, subtitle, fadeIn, stay, fadeOut);
                        }
                        List<SoundEffect> sounds = new ArrayList<>();
                        if (c.get(name + ".slides." + s + ".sounds") != null) {
                            for (String s2 : c.getConfigurationSection(name + ".slides." + s + ".sounds").getKeys(false)) {
                                Sound sound = c.get(name + ".slides." + s + ".sounds." + s2 + ".sound") == null ? null
                                        : Sound.valueOf(c.getString(name + ".slides." + s + ".sounds." + s2 + ".sound"));
                                int volume = c.get(name + ".slides." + s + ".sounds." + s2 + ".volume") == null ? 1 : c.getInt(name + ".slides." + s + ".sounds." + s2 + ".volume");
                                int pitch = c.get(name + ".slides." + s + ".sounds." + s2 + ".pitch") == null ? 1 : c.getInt(name + ".slides." + s + ".sounds." + s2 + ".pitch");
                                int d = c.get(name + ".slides." + s + ".sounds." + s2 + ".delay") == null ? 1 : c.getInt(name + ".slides." + s + ".sounds." + s2 + ".delay");
                                sounds.add(new SoundEffect(sound, volume, pitch, d));
                            }
                        }
                        int slot = Integer.parseInt(c.getString(name + ".slides." + s + ".slot"));
                        String menu = c.getString(name + ".slides." + s + ".menu");
                        slides.add(new Slide(canConfirm, delay, location, gameMode, messages, headerAndFooter, actionBarMessage, title, sounds, slot, menu));
                    }
                Tutorial t = new Tutorial(name, slides, invisible, playersInvisible);
                this.tutorials.add(t);
                this.mappedTuts.put(name.toLowerCase(), t);
            } catch (Exception e) {
                Core.log("Error while loading tutorial " + name + ");");
                e.printStackTrace();
            }
        }
    }

    public void save(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getTutorialsConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (Tutorial tut : this.tutorials) {
            try {
                String name = tut.getName();
                c.set(name + ".invisible", tut.isInvisible());
                c.set(name + ".playersInvisible", tut.isPlayersInvisible());
                int i = 0;
                for (Slide slide : tut.getSlides()) {
                    String path = name + ".slides." + i;
                    c.set(path + ".canConfirm", slide.isCanConfirm());
                    c.set(path + ".delay", slide.getDelay());
                    if (slide.getLocation() != null)
                        c.set(path + ".location", Utils.teleportLocationToString(slide.getLocation()));
                    c.set(path + ".gameMode", slide.getGameMode() == null ? null : slide.getGameMode().toString());
                    c.set(path + ".actionBarMessage", slide.getActionBarMessage());
                    List<String> messages = Utils.toList(slide.getMessages());
                    c.set(path + ".messages", messages);
                    c.set(path + ".headerAndFooter", slide.getHeaderAndFooter());
                    Title title = slide.getTitle();
                    if (title != null) {
                        c.set(path + ".title.title", title.getTitle());
                        c.set(path + ".title.subtitle", title.getSubtitle());
                        c.set(path + ".title.fadeIn", title.getFadeIn());
                        c.set(path + ".title.stay", title.getStay());
                        c.set(path + ".title.fadeOut", title.getFadeOut());
                    }
                    int i2 = 0;
                    if (slide.getSounds() != null)
                        for (SoundEffect s : slide.getSounds()) {
                            c.set(path + ".sounds." + i2 + ".sound", s.getSound().toString());
                            c.set(path + ".sounds." + i2 + ".volume", s.getVolume());
                            c.set(path + ".sounds." + i2 + ".pitch", s.getPitch());
                            c.set(path + ".sounds." + i2 + ".delay", s.getDelay());
                            i2++;
                        }
                    c.set(path + ".slot", slide.getSlot());
                    c.set(path + ".menu", slide.getMenu());
                    i++;
                }
            } catch (Exception e) {
                Core.error("&7Eror while saving tutorial " + tut.getName());
                e.printStackTrace();
            }
        }
        Utils.saveConfig(c, "tutorials");
    }

}
