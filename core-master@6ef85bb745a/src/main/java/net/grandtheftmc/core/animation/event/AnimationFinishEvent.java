package net.grandtheftmc.core.animation.event;

import net.grandtheftmc.core.animation.Animation;
import net.grandtheftmc.core.events.CoreEvent;

public class AnimationFinishEvent extends CoreEvent {

    private final Animation animation;

    public AnimationFinishEvent(Animation animation) {
        super(false);
        this.animation = animation;
    }

    public Animation getAnimation() {
        return this.animation;
    }
}
