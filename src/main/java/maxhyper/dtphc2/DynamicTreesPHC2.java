package maxhyper.dtphc2;

import com.dtteam.dynamictrees.block.fruit.Fruit;
import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.block.pod.Pod;
import com.dtteam.dynamictrees.data.GatherDataHelper;
import com.dtteam.dynamictrees.registry.NeoForgeRegistryHandler;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dtphc2.compat.DTPConfig;
import maxhyper.dtphc2.compat.DTPConfigProxy;
import maxhyper.dtphc2.compat.DefaultConfig;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import maxhyper.dtphc2.init.DTPHC2Items;
import maxhyper.dtphc2.init.DTPHC2Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(DynamicTreesPHC2.MOD_ID)
public class DynamicTreesPHC2 {
    public static final String MOD_ID = "dtphc2";

    public static DTPConfigProxy DTPlusConfig;

    public DynamicTreesPHC2(IEventBus bus, ModContainer modContainer) {
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::gatherData);

        NeoForgeRegistryHandler.setup(MOD_ID, bus);

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

    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

    private void gatherData(final GatherDataEvent event) {
        GatherDataHelper.gatherAllData(MOD_ID, event,
                //SoilProperties.REGISTRY,
                Family.REGISTRY,
                Species.REGISTRY,
                LeavesProperties.REGISTRY,
                Fruit.REGISTRY,
                Pod.REGISTRY
        );
    }

    public static ResourceLocation location(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}
