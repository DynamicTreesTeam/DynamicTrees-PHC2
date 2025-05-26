package maxhyper.dtphc2.compat;

import com.dtteam.dynamictreesplus.init.DTPConfigs;

public class DTPConfig implements DTPConfigProxy {

    @Override
    public boolean cactusPrickleOnMoveOnlyConfig() {
        return DTPConfigs.CACTUS_PRICKLE_ON_MOVE_ONLY.get();
    }

    @Override
    public boolean cactusKillItemsConfig() {
        return DTPConfigs.CACTUS_KILL_ITEMS.get();
    }

    @Override
    public boolean canBoneMealCactusConfig() {
        return DTPConfigs.CAN_BONE_MEAL_CACTUS.get();
    }

}
