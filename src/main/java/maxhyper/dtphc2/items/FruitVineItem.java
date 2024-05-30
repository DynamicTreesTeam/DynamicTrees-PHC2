package maxhyper.dtphc2.items;

import com.ferreusveritas.dynamictrees.compat.season.SeasonHelper;
import com.ferreusveritas.dynamictrees.util.LevelContext;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FruitVineItem extends BlockItem {

    FruitVineBlock vineBlock;

    public FruitVineItem(FruitVineBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        vineBlock = pBlock;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        if (world == null) return;
        if (SeasonHelper.getSeasonValue(LevelContext.create(world), BlockPos.ZERO) == null) return;
        int flags = getSeasonalTooltipFlags(world);

        if (flags != 0) {
            tooltip.add(Component.literal("desc.dynamictrees.seasonal.fertile_seasons").append(":"));

            if ((flags & 15) == 15) {
                tooltip.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.year_round").withStyle(ChatFormatting.LIGHT_PURPLE)));
            } else {
                if ((flags & 1) != 0) {
                    tooltip.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.spring").withStyle(ChatFormatting.GREEN)));
                }
                if ((flags & 2) != 0) {
                    tooltip.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.summer").withStyle(ChatFormatting.YELLOW)));
                }
                if ((flags & 4) != 0) {
                    tooltip.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.autumn").withStyle(ChatFormatting.GOLD)));
                }
                if ((flags & 8) != 0) {
                    tooltip.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.winter").withStyle(ChatFormatting.AQUA)));
                }
            }
        }
    }

    public int getSeasonalTooltipFlags(final Level world) {
        final float seasonStart = 1f / 6;
        final float seasonEnd = 1 - 1f / 6;
        final float threshold = 0.75f;

        Float seasonOffset = vineBlock.getSeasonOffset();

        int seasonFlags = 0;
        for (int i = 0; i < 4; i++) {
            boolean isValidSeason = false;
            if (seasonOffset != null) {
                final LevelContext levelContext = LevelContext.create(world);
                final float prod1 = SeasonHelper.globalSeasonalFruitProductionFactor(levelContext,
                        new BlockPos(0, (int) ((i + seasonStart - seasonOffset) * 64.0f), 0), true);
                final float prod2 = SeasonHelper.globalSeasonalFruitProductionFactor(levelContext,
                        new BlockPos(0, (int) ((i + seasonEnd - seasonOffset) * 64.0f), 0), true);
                if (Math.min(prod1, prod2) > threshold) {
                    isValidSeason = true;
                }

            } else {
                isValidSeason = true;
            }

            if (isValidSeason) {
                seasonFlags |= 1 << i;
            }

        }
        return seasonFlags;
    }

}
