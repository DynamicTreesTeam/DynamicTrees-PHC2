package maxhyper.dtphc2.init;

import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.items.FruitVineItem;
import maxhyper.dtphc2.items.RipePeppercornItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static maxhyper.dtphc2.init.DTPHC2Blocks.*;

public class DTPHC2Items {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DynamicTreesPHC2.MOD_ID);

    public static final RegistryObject<Item> PASSION_FRUIT_VINE_ITEM = ITEMS.register("passion_fruit_vine",
            () -> new FruitVineItem(PASSION_FRUIT_VINE.get(), new Item.Properties()));

    public static final RegistryObject<Item> VANILLA_VINE_ITEM = ITEMS.register("vanilla_vine",
            () -> new FruitVineItem(VANILLA_VINE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PEPPERCORN_VINE_ITEM = ITEMS.register("peppercorn_vine",
            () -> new FruitVineItem(PEPPERCORN_VINE.get(), new Item.Properties()));

    public static final RegistryObject<Item> RIPE_PEPPERCORN_ITEM = ITEMS.register("ripe_peppercorn_item",
            () -> new RipePeppercornItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void acceptToDynamicTreesTab (BuildCreativeModeTabContentsEvent event){
        event.accept(PASSION_FRUIT_VINE_ITEM.get());
        event.accept(VANILLA_VINE_ITEM.get());
        event.accept(PEPPERCORN_VINE_ITEM.get());
        event.accept(RIPE_PEPPERCORN_ITEM.get());
    }

}
