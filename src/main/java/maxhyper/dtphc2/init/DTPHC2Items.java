package maxhyper.dtphc2.init;

import maxhyper.dtphc2.DynamicTreesPHC2;
import maxhyper.dtphc2.items.FruitVineItem;
import maxhyper.dtphc2.items.RipePeppercornItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static maxhyper.dtphc2.init.DTPHC2Blocks.*;

public class DTPHC2Items {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, DynamicTreesPHC2.MOD_ID);

    public static final Supplier<Item> PASSION_FRUIT_VINE_ITEM = ITEMS.register("passion_fruit_vine",
            () -> new FruitVineItem(PASSION_FRUIT_VINE.get(), new Item.Properties()));

    public static final Supplier<Item> VANILLA_VINE_ITEM = ITEMS.register("vanilla_vine",
            () -> new FruitVineItem(VANILLA_VINE.get(), new Item.Properties()));

    public static final Supplier<Item> PEPPERCORN_VINE_ITEM = ITEMS.register("peppercorn_vine",
            () -> new FruitVineItem(PEPPERCORN_VINE.get(), new Item.Properties()));

    public static final Supplier<Item> RIPE_PEPPERCORN_ITEM = ITEMS.register("ripe_peppercorn_item",
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
