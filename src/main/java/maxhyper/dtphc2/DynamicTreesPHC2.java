package maxhyper.dtphc2;

import com.ferreusveritas.dynamictrees.api.GatherDataHelper;
import com.ferreusveritas.dynamictrees.api.registry.RegistryHandler;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.init.DTConfigs;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import com.ferreusveritas.dynamictrees.systems.pod.Pod;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.pam.pamhc2trees.init.ItemRegistration;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import maxhyper.dtphc2.compat.DTPConfig;
import maxhyper.dtphc2.compat.DTPConfigProxy;
import maxhyper.dtphc2.compat.DefaultConfig;
import maxhyper.dtphc2.event.SpilePlacementEvent;
import maxhyper.dtphc2.init.DTPHC2Client;
import maxhyper.dtphc2.init.DTPHC2Registries;
import maxhyper.dtphc2.init.DTPHC2Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod(DynamicTreesPHC2.MOD_ID)
@Mod.EventBusSubscriber(modid = DynamicTreesPHC2.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DynamicTreesPHC2 {
    public static final String MOD_ID = "dtphc2";

    public static DTPConfigProxy DTPlusConfig;

    public DynamicTreesPHC2() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SpilePlacementEvent.class);

        RegistryHandler.setup(MOD_ID);

        DTPHC2Blocks.register(bus);
        DTPHC2Items.register(bus);
        DTPHC2Registries.setup();
        DTPHC2Registries.SOUNDS.register(bus);

        if (ModList.get().isLoaded("dynamictreesplus"))
            DTPlusConfig = new DTPConfig();
        else
            DTPlusConfig = new DefaultConfig();
    }

    private void commonSetup(final FMLConstructModEvent event) {
//        if (DTConfigs.WORLD_GEN.get()) {
//            WorldGenRegistry.apple_worldgen = null;
//            WorldGenRegistry.avocado_worldgen = null;
//            WorldGenRegistry.candlenut_worldgen = null;
//            WorldGenRegistry.cherry_worldgen = null;
//            WorldGenRegistry.chestnut_worldgen = null;
//            WorldGenRegistry.gooseberry_worldgen = null;
//            WorldGenRegistry.lemon_worldgen = null;
//            WorldGenRegistry.nutmeg_worldgen = null;
//            WorldGenRegistry.orange_worldgen = null;
//            WorldGenRegistry.peach_worldgen = null;
//            WorldGenRegistry.pear_worldgen = null;
//            WorldGenRegistry.plum_worldgen = null;
//            WorldGenRegistry.walnut_worldgen = null;
//            WorldGenRegistry.spiderweb_worldgen = null;
//            WorldGenRegistry.hazelnut_worldgen = null;
//            WorldGenRegistry.pawpaw_worldgen = null;
//            WorldGenRegistry.soursop_worldgen = null;
//            WorldGenRegistry.almond_worldgen = null;
//            WorldGenRegistry.apricot_worldgen = null;
//            WorldGenRegistry.banana_worldgen = null;
//            WorldGenRegistry.cashew_worldgen = null;
//            WorldGenRegistry.cinnamon_worldgen = null;
//            WorldGenRegistry.coconut_worldgen = null;
//            WorldGenRegistry.date_worldgen = null;
//            WorldGenRegistry.dragonfruit_worldgen = null;
//            WorldGenRegistry.durian_worldgen = null;
//            WorldGenRegistry.fig_worldgen = null;
//            WorldGenRegistry.grapefruit_worldgen = null;
//            WorldGenRegistry.lime_worldgen = null;
//            WorldGenRegistry.mango_worldgen = null;
//            WorldGenRegistry.olive_worldgen = null;
//            WorldGenRegistry.papaya_worldgen = null;
//            WorldGenRegistry.paperbark_worldgen = null;
//            WorldGenRegistry.pecan_worldgen = null;
//            WorldGenRegistry.peppercorn_worldgen = null;
//            WorldGenRegistry.persimmon_worldgen = null;
//            WorldGenRegistry.pistachio_worldgen = null;
//            WorldGenRegistry.pomegranate_worldgen = null;
//            WorldGenRegistry.starfruit_worldgen = null;
//            WorldGenRegistry.vanillabean_worldgen = null;
//            WorldGenRegistry.breadfruit_worldgen = null;
//            WorldGenRegistry.guava_worldgen = null;
//            WorldGenRegistry.jackfruit_worldgen = null;
//            WorldGenRegistry.lychee_worldgen = null;
//            WorldGenRegistry.passionfruit_worldgen = null;
//            WorldGenRegistry.rambutan_worldgen = null;
//            WorldGenRegistry.tamarind_worldgen = null;
//            WorldGenRegistry.maple_worldgen = null;
//            WorldGenRegistry.pinenut_worldgen = null;
//        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DTPHC2Client.setup();
        DTPHC2Blocks.PASSION_FRUIT_VINE.get().setFruitStack(new ItemStack(ItemRegistration.passionfruititem.get()));
        DTPHC2Blocks.VANILLA_VINE.get().setFruitStack(new ItemStack(ItemRegistration.vanillabeanitem.get()));
        DTPHC2Blocks.PEPPERCORN_VINE.get().setFruitStack(new ItemStack(ItemRegistration.peppercornitem.get())).setOverripeFruitStack(new ItemStack(DTPHC2Items.RIPE_PEPPERCORN_ITEM.get()));
    }

    private void gatherData(final GatherDataEvent event) {
        GatherDataHelper.gatherAllData(MOD_ID, event,
                //SoilProperties.REGISTRY,
                //Family.REGISTRY,
                //Species.REGISTRY,
                //LeavesProperties.REGISTRY,
                Fruit.REGISTRY,
                Pod.REGISTRY
        );
    }

    public static ResourceLocation location(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
