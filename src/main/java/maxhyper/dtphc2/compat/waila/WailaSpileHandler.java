package maxhyper.dtphc2.compat.waila;

import com.dtteam.dynamictrees.compat.WailaHelper;
import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.MapleSpileBlock;
import maxhyper.dtphc2.blocks.MapleSpileBucketBlock;
import maxhyper.dtphc2.blocks.MapleSpileCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ElementHelper;

import java.util.LinkedList;
import java.util.List;

public class WailaSpileHandler implements IBlockComponentProvider {
    public static final ResourceLocation ID = DynamicTreesPHC2.location("spile");
    public static WailaSpileHandler INSTANCE = new WailaSpileHandler();

    private BlockPos lastPos = BlockPos.ZERO;
    private Species lastSpecies = Species.NULL_SPECIES;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        if (accessor.getBlockState().hasProperty(MapleSpileBlock.FILLED)) {
            boolean filled = accessor.getBlockState().getValue(MapleSpileBlock.FILLED);
            Component filledText = filled
                    ? Component.translatable("tooltip.dtphc2.maple_spile_filled")
                    : Component.translatable("tooltip.dtphc2.maple_spile_not_filled");
            tooltip.add(filledText);
        }
        if (accessor.getBlockState().hasProperty(MapleSpileBucketBlock.FILLING)) {
            int filling = accessor.getBlockState().getValue(MapleSpileBucketBlock.FILLING);
            float percent = filling / 3.0F * 100;
            tooltip.add(Component.translatable("tooltip.dtphc2.maple_spile_bucket_filling", String.format("%.0f%%", percent)));
        }

        // ADD ICON
        List<IElement> elements = new LinkedList<>();

        if (WailaHelper.invalid) {
            lastPos = BlockPos.ZERO;
            lastSpecies = Species.NULL_SPECIES;

            WailaHelper.invalid = false;
        }

        BlockPos pos = accessor.getPosition();
        Species species = Species.NULL_SPECIES;

        //Attempt to get species by checking if we're still looking at the same block
        if (lastPos.equals(pos)) {
            species = lastSpecies;
        }

        //Attempt to get species from the world as a last resort as the operation can be rather expensive
        BlockState state = accessor.getLevel().getBlockState(accessor.getPosition());
        if (species == Species.NULL_SPECIES) {
            if (!state.hasProperty(MapleSpileCommon.FACING)) {
                elements.add(getElement(ItemStack.EMPTY));
            }
            Direction dir = state.getValue(MapleSpileCommon.FACING);
            species = TreeHelper.getExactSpecies(accessor.getLevel(), accessor.getPosition().offset(dir.getOpposite().getNormal()));
        }

        //If everything fails just show an iron ingot, womp womp
        if (species == Species.NULL_SPECIES) tooltip.add(getElement(new ItemStack(Items.IRON_INGOT)));

        //Update the cached species and position
        lastSpecies = species;
        lastPos = pos;

        int count = 0;
        if (state.hasProperty(MapleSpileBlock.FILLED)) {
            count = state.getValue(MapleSpileBlock.FILLED) ? 1 : 0;
        } else if (state.hasProperty(MapleSpileBucketBlock.FILLING)) {
            count = state.getValue(MapleSpileBucketBlock.FILLING);
            count += count == 3 ? 1 : 0;
        }
        elements.add(getElement(new ItemStack(MapleSpileCommon.getSyrupItem(species), count)));
        elements.forEach(tooltip::append);
    }

    private static IElement getElement(ItemStack stack) {
        return !stack.isEmpty() ? ElementHelper.INSTANCE.item(stack) : ElementHelper.INSTANCE.spacer(0, 0);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}