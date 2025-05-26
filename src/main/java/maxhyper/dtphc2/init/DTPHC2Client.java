package maxhyper.dtphc2.init;

import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.MapleSpileCommon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = DynamicTreesPHC2.MOD_ID, value = {Dist.CLIENT}, bus = EventBusSubscriber.Bus.MOD)
public class DTPHC2Client {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerItemColorHandlersEvent(RegisterColorHandlersEvent.Item event) {

        Item[] vineItems = new Item[]{DTPHC2Items.PASSION_FRUIT_VINE_ITEM.get(), DTPHC2Items.VANILLA_VINE_ITEM.get(), DTPHC2Items.PEPPERCORN_VINE_ITEM.get()};
        for (Item vineItem : vineItems){
            event.register((itemStack, tintIndex) ->
                    event.getItemColors().getColor(new ItemStack(Items.VINE), tintIndex), vineItem
            );
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerBlockColorHandlersEvent(RegisterColorHandlersEvent.Block event) {

        Block[] vines = new Block[]{DTPHC2Blocks.PASSION_FRUIT_VINE.get(), DTPHC2Blocks.VANILLA_VINE.get(), DTPHC2Blocks.PEPPERCORN_VINE.get()};
        for (Block vine : vines){
            event.register((state, worldIn, pos, tintIndex) ->
                    event.getBlockColors().getColor(Blocks.VINE.defaultBlockState(), worldIn, pos, tintIndex), vine
            );
        }

        Block[] spiles = new Block[]{DTPHC2Blocks.MAPLE_SPILE_BLOCK.get(), DTPHC2Blocks.MAPLE_SPILE_BUCKET_BLOCK.get()};
        for (Block spile : spiles){
            event.register(((MapleSpileCommon)spile)::colorMultiplier, spile);
        }
    }

}
