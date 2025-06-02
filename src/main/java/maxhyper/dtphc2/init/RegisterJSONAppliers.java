package maxhyper.dtphc2.init;

import com.dtteam.dynamictrees.deserialization.PropertyAppliers;
import com.dtteam.dynamictrees.event.ApplierRegistryEvent;
import com.dtteam.dynamictrees.tree.species.Species;
import com.google.gson.JsonElement;
import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.trees.FruitLogSpecies;
import maxhyper.dtphc2.trees.GenOnExtraSoilSpecies;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DynamicTreesPHC2.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class RegisterJSONAppliers {

    @SubscribeEvent
    public static void registerAppliersSpecies(final ApplierRegistryEvent.Reload<Species, JsonElement> event) {
        registerSpeciesAppliers(event.getAppliers());
    }

//    @SubscribeEvent
//    public static void registerAppliersFruit(final ApplierRegistryEvent.Reload<Fruit, JsonElement> event) {
//        registerFruitAppliers(event.getAppliers());
//    }
//
//    @SubscribeEvent
//    public static void registerAppliersPod(final ApplierRegistryEvent.Reload<Pod, JsonElement> event) {
//        registerPodAppliers(event.getAppliers());
//    }

    public static void registerSpeciesAppliers(PropertyAppliers<Species, JsonElement> appliers) {
        appliers.register("extra_soil_for_worldgen", GenOnExtraSoilSpecies.class, Block.class,
                        GenOnExtraSoilSpecies::setExtraSoil)
                .register("log_drop_item", FruitLogSpecies.class, Item.class, FruitLogSpecies::setDropItem)
                //.register("log_drop_item", FruitLogSpecies.class, ResourceLocation.class, FruitLogSpecies::setDropItem)
                .register("log_drop_item_multiplier", FruitLogSpecies.class, Float.class, FruitLogSpecies::setItemMultiplier)
                .register("log_drop_fake_log", FruitLogSpecies.class, Item.class, FruitLogSpecies::setFakeLog)
                .register("log_drop_fake_log_multiplier", FruitLogSpecies.class, Float.class, FruitLogSpecies::setFakeLogMultiplier);
    }

//    public static void registerFruitAppliers(PropertyAppliers<Fruit, JsonElement> appliers) {
//        appliers.register("item_stack", DTPHC2Fruit.class, ResourceLocation.class, DTPHC2Fruit::setItemStackLoc)
//                .register("item_stack", FallingFruit.class, ResourceLocation.class, DTPHC2Fruit::setItemStackLoc)
//                .register("item_stack", OffsetFruit.class, ResourceLocation.class, DTPHC2Fruit::setItemStackLoc);
//    }
//    public static void registerPodAppliers(PropertyAppliers<Pod, JsonElement> appliers) {
//        appliers.register("item_stack", DTPHC2Pod.class, ResourceLocation.class, DTPHC2Pod::setItemStackLoc);
//    }

    @SubscribeEvent public static void registerAppliersSpecies(final ApplierRegistryEvent.GatherData<Species, JsonElement> event) { registerSpeciesAppliers(event.getAppliers()); }
//    @SubscribeEvent public static void registerAppliersFruit(final ApplierRegistryEvent.GatherData<Fruit, JsonElement> event) {
//        registerFruitAppliers(event.getAppliers());
//    }
//    @SubscribeEvent public static void registerAppliersPod(final ApplierRegistryEvent.GatherData<Pod, JsonElement> event) { registerPodAppliers(event.getAppliers()); }

}