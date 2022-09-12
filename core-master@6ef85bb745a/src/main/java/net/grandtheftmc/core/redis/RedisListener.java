package net.grandtheftmc.core.redis;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.redis.data.DataType;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.Reward;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by Adam on 14/06/2017.
 */
public class RedisListener {

    public RedisListener() {
        try {
            //Jedis e = Core.getInstance().getJedis();
            Jedis e = RedisFactory.getPool().getResource();

            e.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    super.onMessage(channel, message);

                    if (!channel.equalsIgnoreCase(RedisManager.channel)) {
                        //Only listen for messages on our channel.
                        return;
                    }

                    JSONObject serialized = new JSONObject(message);
                    String dt = serialized.getString("datatype");
                    DataType type = DataType.valueOf(dt);

                    if (type == null) {
                        Core.log("Unknown datatype (" + dt + ") received on channel=" + RedisManager.channel + ", ignoring.");
                        return;
                    }

                    if (type.equals(DataType.REWARD_NOTIFY)) {

                        String targetPlayer = serialized.getString("target");

                        Player target = null;

                        //Check for player from their UUID
                        if ((target = Bukkit.getPlayer(UUID.fromString(targetPlayer))) == null) {
                            //target is not on this server
                            return;
                        }

                        //Notify them of the rewards they have waiting
                        target.sendMessage(Utils.f("&aYou have rewards waiting to be claimed! /rewards"));
                    }
                }
            }, RedisManager.channel);
        } catch (JedisConnectionException e) {
            Core.log("Unable to connect to Jedis!!");
            e.printStackTrace();
        }
    }

    private String getDisplayName(Reward.RewardType type, String val) {
        switch (type) {
            case ITEMS:
                break;
            case BUCKS:
                return "&a&l" + val + " Bucks";
            case MONEY:
                return "&a$&l" + val;
            case TOKENS:
                int i = Integer.parseInt(val);
                return "&a&l" + i + "&e&l Token" + (i > 1 ? "s" : "");
            case NAMETAG:
                return "&e&l" + val;
            case RANK:
                UserRank rank = UserRank.valueOf(val);
                return rank.getColor() + "&lPermanent " + rank.getColoredNameBold();
            case TRIAL_RANK:
                String[] args = val.split(",");
                rank = UserRank.valueOf(args[0]);
                i = Integer.parseInt(args[1]);
                return "&a&l" + i + " day" + (i > 1 ? "s " : " ") + rank.getColoredNameBold() + "&a&l Trial";
            case CUSTOM:
                break;
//            case COSMETIC:
//                if (val.equalsIgnoreCase("random-any")) {
//                    args = val.split(",");
//                    int minTokens = Integer.parseInt(args[0]), maxTokens = Integer.parseInt(args[1]);
//                    return "&e&lRandom Cosmetic " +
//                            "&7(&e" + (maxTokens > 0 ? minTokens + '-' + maxTokens : "min " + minTokens) + " tokens&7)";
//                } else if (val.equalsIgnoreCase("random-specific")) {
//
//                    args = val.split(",");
//                    int minTokens = Integer.parseInt(args[0]), maxTokens = Integer.parseInt(args[1]);
//                    CosmeticType ct = CosmeticType.valueOf(args[2]);
//                    return '&' + ct.getColor() + "&lRandom " + ct.getColoredDisplayNameSingle()
//                            + " &7(&e" + (maxTokens > 0 ? minTokens + '-' + maxTokens : "min " + minTokens) + " tokens&7)";
//                } else {
//
//                    CosmeticType ct = CosmeticType.valueOf(val);
//                    return ct.getColoredDisplayName();
//                }
               /* return this.cosmetic == null ? this.cosmeticType == null ? "&e&lRandom Cosmetic " +
                        "&7(&e" + (this.maxTokens > 0 ? this.minTokens + '-' + this.maxTokens : "min " + this.minTokens) + " tokens&7)" :
                        '&' + this.cosmeticType.getColor() + "&lRandom " + this.cosmeticType.getColoredDisplayNameSingle()
                                + " &7(&e" + (this.maxTokens > 0 ? this.minTokens + '-' + this.maxTokens : "min " + this.minTokens) + " tokens&7)" :
                        this.cosmetic.getColoredDisplayName();*/
            case PERMISSION:
                return val;
            case CROWBARS:
                i = Integer.parseInt(val);
                return "&9&l" + i + " Crowbar" + (i > 1 ? "s" : "");
            case COMMAND:
                break;
            case ACHIEVEMENT:
                return val;

        }
        return val;
    }
}
