package maxhyper.dtphc2.items;

import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;

public class FruitVineItem extends BlockItem {

    FruitVineBlock vineBlock;

    public FruitVineItem(FruitVineBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        vineBlock = pBlock;
    }

    public int getSeasonalTooltipFlags(LevelContext levelContext, Player player) {
        BlockPos playerPos = BlockPos.containing(player.position());

        Float seasonOffset = vineBlock.getSeasonOffset();
        if (seasonOffset == null) return 15;//All seasons

        Float fruitPeak = SeasonHelper.getPeakFruitProductionSeason(levelContext, playerPos, seasonOffset);
        if (fruitPeak == null) return 15;//All seasons

        int seasonFlags = 0;
        for (int i = 0; i < 4; i++) {
            float season = i + 0.5f;
            if (SeasonHelper.isSeasonBetween(fruitPeak, season-0.6f, season+0.6f)){
                seasonFlags |= 1 << i;
            }
        }
        return seasonFlags;
    }

}
