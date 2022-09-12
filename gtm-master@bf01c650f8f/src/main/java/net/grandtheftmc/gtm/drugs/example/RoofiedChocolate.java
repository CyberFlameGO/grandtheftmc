package net.grandtheftmc.gtm.drugs.example;

import de.slikey.effectlib.effect.LoveEffect;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.categories.examples.Stimulants;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-09-28.
 */
public class RoofiedChocolate extends Stimulants {
    public RoofiedChocolate() {
        super("roofied_chocolate", 60*4);
    }


    private String[] hornyThingsPrezWouldSay = new String[]{"Holy fuck that tree is so hot right now", "I could probably go hump a cow and be okay with myself", "Is it just me, or is Bruce Jenner kinda hot?", "Best five seconds of my life"};
    @Override
    public boolean apply(Player player) {
        int ran = ThreadLocalRandom.current().nextInt(0, 100);
        if(ran<20) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*4, 0));
        }
        else if(ran<40) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*60*2, 1));
        }
        else if(ran<45) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0));

        }
        else if(ran<75) {
            LoveEffect effect = new LoveEffect(GTM.getEffectLib());
            effect.setEntity(player);
            effect.iterations = 15 * 20;
            effect.start();
            player.sendMessage(Utils.f("&7&o" + hornyThingsPrezWouldSay[ThreadLocalRandom.current().nextInt(0, this.hornyThingsPrezWouldSay.length)]));
        }
        else if(ran<90) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60*2, 0));
        }
        else if(ran<100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0));
        }
        return true;
    }
}
