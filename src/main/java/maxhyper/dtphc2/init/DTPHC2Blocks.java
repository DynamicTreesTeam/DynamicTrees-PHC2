package maxhyper.dtphc2.init;

import com.ferreusveritas.dynamictrees.init.DTRegistries;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.BananaSuckerBlock;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import maxhyper.dtphc2.blocks.MapleSpileBlock;
import maxhyper.dtphc2.blocks.MapleSpileBucketBlock;
import maxhyper.dtphc2.init.DTPHC2Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DTPHC2Blocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DynamicTreesPHC2.MOD_ID);

    public static final RegistryObject<Block> BANANA_SUCKER_BLOCK = registerBlock("banana_sucker", BananaSuckerBlock::new);
    public static final RegistryObject<FruitVineBlock> PASSION_FRUIT_VINE = registerBlock("passion_fruit_vine",
            () -> new FruitVineBlock().setSeasonOffset(0f)); //summer
    public static final RegistryObject<FruitVineBlock> VANILLA_VINE = registerBlock("vanilla_vine",
            () -> new FruitVineBlock().setSeasonOffset(2f)); //winter
    public static final RegistryObject<FruitVineBlock> PEPPERCORN_VINE = registerBlock("peppercorn_vine",
            () -> new FruitVineBlock().setSeasonOffset(0f)
                    .setMatureAge(3)
                    .setFruitOverripenChance(0.01f)); //summer
    public static final RegistryObject<Block> MAPLE_SPILE_BLOCK = registerBlock("maple_spile", MapleSpileBlock::new);
    public static final RegistryObject<Block> MAPLE_SPILE_BUCKET_BLOCK = registerBlock("maple_spile_bucket", MapleSpileBucketBlock::new);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
