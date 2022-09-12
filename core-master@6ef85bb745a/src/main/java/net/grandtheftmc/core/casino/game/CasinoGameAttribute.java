package net.grandtheftmc.core.casino.game;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CasinoGameAttribute {
    /**
     * Identifier of the casino game.
     * @return
     */
    int id();

    /**
     * Name of the casino game.
     * @return
     */
    String name();

    /**
     * Version of the casino game.
     * @return
     */
    String version();
}
