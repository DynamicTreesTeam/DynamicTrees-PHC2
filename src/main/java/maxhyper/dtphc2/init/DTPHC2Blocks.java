package maxhyper.dtphc2.init;

import com.pam.pamhc2trees.init.ItemRegistration;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.BananaSuckerBlock;
import maxhyper.dtphc2.blocks.FruitVineBlock;
import maxhyper.dtphc2.blocks.MapleSpileBlock;
import maxhyper.dtphc2.blocks.MapleSpileBucketBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DTPHC2Blocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, DynamicTreesPHC2.MOD_ID);

    public static final Supplier<Block> BANANA_SUCKER_BLOCK = registerBlock("banana_sucker",
            ()->new BananaSuckerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)));
    public static final Supplier<FruitVineBlock> PASSION_FRUIT_VINE = registerBlock("passion_fruit_vine",
            () -> new FruitVineBlock(ItemRegistration.passionfruititem)
                    .setSeasonOffset(0f)); //summer
    public static final Supplier<FruitVineBlock> VANILLA_VINE = registerBlock("vanilla_vine",
            () -> new FruitVineBlock(ItemRegistration.vanillabeanitem)
                    .setSeasonOffset(2f)); //winter
    public static final Supplier<FruitVineBlock> PEPPERCORN_VINE = registerBlock("peppercorn_vine",
            () -> new FruitVineBlock(ItemRegistration.peppercornitem, DTPHC2Items.RIPE_PEPPERCORN_ITEM)
                    .setSeasonOffset(0f) //summer
                    .setMatureAge(3)
                    .setFruitOverripenChance(0.01f));
    public static final Supplier<Block> MAPLE_SPILE_BLOCK = registerBlock("maple_spile",
            () -> new MapleSpileBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.5f).randomTicks()));
    public static final Supplier<Block> MAPLE_SPILE_BUCKET_BLOCK = registerBlock("maple_spile_bucket",
            () -> new MapleSpileBucketBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.7f).randomTicks()));

    private static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
