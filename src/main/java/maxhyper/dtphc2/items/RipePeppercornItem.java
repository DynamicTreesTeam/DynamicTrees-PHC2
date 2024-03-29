package maxhyper.dtphc2.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class RipePeppercornItem extends Item {

    public RipePeppercornItem(Item.Properties properties) {
        super(properties);
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, @Nonnull TooltipFlag pFlag) {
        pTooltip.add(new TranslatableComponent("tooltip.dtphc2.ripe_peppercorn_item"));
    }

}
