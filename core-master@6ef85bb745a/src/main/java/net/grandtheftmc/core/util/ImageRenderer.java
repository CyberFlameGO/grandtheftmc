package net.grandtheftmc.core.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Created by Luke Bingham on 12/09/2017.
 */
public class ImageRenderer extends MapRenderer {

    public static final HashMap<UUID, ImageRenderer> RENDERED_USERS = Maps.newHashMap();

    // So fancy.
    private BufferedImage cacheImage;

    public ImageRenderer(String url) throws IOException {
        this.cacheImage = this.getImage(url);
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
//        if(RENDERED_USERS.containsKey(player.getUniqueId())) return;
        RENDERED_USERS.put(player.getUniqueId(), this);

        if (this.cacheImage != null) {
            canvas.drawImage(0, 0, this.cacheImage);
        } else {
            player.sendMessage(ChatColor.RED + "Attempted to render the image, but the cached image was null!");
        }
    }

    private BufferedImage getImage(String url) throws IOException {
        boolean useCache = ImageIO.getUseCache();

        // Temporarily disable cache, if it isn't already,
        // so we can get the latest image.
        ImageIO.setUseCache(false);

        BufferedImage image = resize(new URL(url), new Dimension(128, 128));
        // TODO find import for RenderUtils
        //RenderUtils.resizeImage(image);

        // Renable it with the old value.
        ImageIO.setUseCache(useCache);

        return image;
    }

    private BufferedImage resize(final URL url, final Dimension size) throws IOException {
        final BufferedImage image = ImageIO.read(url);
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }

}
