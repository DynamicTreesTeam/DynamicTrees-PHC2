package maxhyper.dtphc2.init;

import com.dtteam.dynamictrees.block.CommonVoxelShapes;
import com.dtteam.dynamictrees.block.fruit.Fruit;
import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.block.pod.Pod;
import com.dtteam.dynamictrees.event.RegistryEvent;
import com.dtteam.dynamictrees.event.TypeRegistryEvent;
import com.dtteam.dynamictrees.registry.DTRegistries;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.blocks.DragonFruitLeavesProperties;
import maxhyper.dtphc2.fruits.*;
import maxhyper.dtphc2.genfeatures.DTPHC2GenFeatures;
import maxhyper.dtphc2.trees.FruitLogSpecies;
import maxhyper.dtphc2.trees.GenOnExtraSoilSpecies;
import maxhyper.dtphc2.trees.PaperbarkFamily;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD)
public class DTPHC2Registries {

    public static final TagKey<Block> CAN_BE_SPILED = BlockTags.create(DynamicTreesPHC2.location("can_be_spiled"));

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DynamicTreesPHC2.MOD_ID);

    public static final Supplier<SoundEvent> FRUIT_BONK = registerSound("falling_fruit.bonk");

    public static final VoxelShape DRAGON_FRUIT_CACTUS_SAPLING_SHAPE = Shapes.box(
                    0.375f, 0.0f, 0.375f,
                    0.625f, 0.5f, 0.625f);

    public static final VoxelShape BANANA_SAPLING_SHAPE = Shapes.box(
                    0.375f, 0.0f, 0.375f,
                    0.625f, 0.9375f, 0.625f);

    public static void setup() {
        CommonVoxelShapes.SHAPES.put(DynamicTreesPHC2.location("dragon_fruit_cactus").toString(), DRAGON_FRUIT_CACTUS_SAPLING_SHAPE);
        CommonVoxelShapes.SHAPES.put(DynamicTreesPHC2.location("banana_sapling").toString(), BANANA_SAPLING_SHAPE);
    }

    public static Supplier<SoundEvent> registerSound (String name){
        return SOUNDS.register(name, ()-> SoundEvent.createVariableRangeEvent(DynamicTreesPHC2.location(name)));
    }

    @SubscribeEvent
    public static void registerFruitType(final TypeRegistryEvent<Fruit> event) {
        if (event.isEntryOfType(Fruit.class)){
            event.registerType(DynamicTreesPHC2.location("offset_down"), OffsetFruit.TYPE);
            event.registerType(DynamicTreesPHC2.location("falling_fruit"), FallingFruit.TYPE);
            event.registerType(DynamicTreesPHC2.location("cobweb"), CobwebFruit.TYPE);
        }
    }

    @SubscribeEvent
    public static void registerPodType(final TypeRegistryEvent<Pod> event) {
        if (event.isEntryOfType(Pod.class)){
            event.registerType(DynamicTreesPHC2.location("palm"), PalmPod.TYPE);
            event.registerType(DynamicTreesPHC2.location("falling_palm"), FallingPalmPod.TYPE);
        }
    }

    @SubscribeEvent
    public static void registerLeavesPropertiesType(final TypeRegistryEvent<LeavesProperties> event) {
        if (event.isEntryOfType(LeavesProperties.class)){
            event.registerType(DynamicTreesPHC2.location("dragon_fruit"), DragonFruitLeavesProperties.TYPE);
        }
    }

    @SubscribeEvent
    public static void registerFamilyType(final TypeRegistryEvent<Family> event) {
        if (event.isEntryOfType(Family.class)){
            event.registerType(DynamicTreesPHC2.location("paperbark"), PaperbarkFamily.TYPE);
        }
    }

    @SubscribeEvent
    public static void registerSpeciesType(final TypeRegistryEvent<Species> event) {
        if (event.isEntryOfType(Species.class)){
            event.registerType(DynamicTreesPHC2.location("fruit_log"), FruitLogSpecies.TYPE);
            event.registerType(DynamicTreesPHC2.location("generates_on_extra_soil"), GenOnExtraSoilSpecies.TYPE);
        }
    }

    @SubscribeEvent
    public static void onGenFeatureRegistry (final RegistryEvent<GenFeature> event) {
        if (event.isEntryOfType(GenFeature.class)){
            DTPHC2GenFeatures.register(event.getRegistry());
        }
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Add to ingredients tab
        if (event.getTab() == DTRegistries.DT_CREATIVE_TAB.get()) {
            DTPHC2Items.acceptToDynamicTreesTab(event);
        }
    }

}
