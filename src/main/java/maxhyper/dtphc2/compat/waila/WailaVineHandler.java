package maxhyper.dtphc2.compat.waila;

import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class WailaVineHandler implements IBlockComponentProvider {
    public static final ResourceLocation ID = DynamicTreesPHC2.location("vines");
    public static WailaVineHandler INSTANCE = new WailaVineHandler();
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        if (accessor.getBlock() instanceof FruitVineBlock fruitBlock) {
            float ageAsPercentage = fruitBlock.getAge(accessor.getBlockState()) * 100F / fruitBlock.getMatureAge();
            tooltip.add(Component.translatable(
                    "tooltip.waila.crop_growth",
                    ageAsPercentage < 100F ? String.format("%.0f%%", ageAsPercentage) :
                            Component.translatable("tooltip.waila.crop_mature").withStyle(ChatFormatting.GREEN)
            ));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
