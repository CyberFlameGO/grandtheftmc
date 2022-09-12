package net.grandtheftmc.core.anticheat.check;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.anticheat.check.movement.SpeedCheck;
import net.grandtheftmc.core.anticheat.check.movement.VelocityCheck;
import net.grandtheftmc.core.anticheat.trigger.Trigger;

import java.util.List;

public class CheckManager {

    private final List<Check> checks = Lists.newArrayList();

    public CheckManager() {

        //Movement
        this.checks.add(new SpeedCheck());
        this.checks.add(new VelocityCheck());

        //Combat
//        this.checks.add(new KillAuraCheck());
//        this.checks.add(new FrequencyCheck());
//        this.checks.add(new ImprobableCheck());
//        this.checks.add(new NoSwingCheck());
//        this.checks.add(new ReachCheck());
//        this.checks.add(new SwingCheck());
    }

    public List<Check> getChecks() {
        return checks;
    }

    public double check(Trigger trigger) {
        double lvl = 0D;
        for(Check check : this.checks) {
            double violation = check.analyse(trigger);
            if(violation > 0) {
                lvl += violation;
            }
        }
        return lvl;
    }
}
