package maxhyper.dtphc2.event;

import com.dtteam.dynamictrees.api.season.ClimateZoneType;
import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.client.Tooltips;
import com.dtteam.dynamictrees.item.Seed;
import com.dtteam.dynamictrees.systems.season.ClimateHelper;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.items.FruitVineItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = DynamicTreesPHC2.MOD_ID, value = Dist.CLIENT)
public class ItemTooltipEventHandler {
    @SubscribeEvent
    public static void onItemTooltipAdded(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (!(item instanceof FruitVineItem vine)) return;

        Player player = event.getEntity();
        if (player == null) return;

        LevelContext levelContext = LevelContext.create(player.level());

        if (SeasonHelper.getSeasonValue(levelContext, BlockPos.ZERO) == null) {
            return;
        }

        BlockPos playerPos = BlockPos.containing(player.position());
        ClimateZoneType climate = ClimateHelper.getClimate(player.level(), playerPos);
        int flags = vine.getSeasonalTooltipFlags(levelContext, player);
        Tooltips.applySeasonalTooltips(event.getToolTip(), flags, climate);
    }
}


