package maxhyper.dtphc2.fruits;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.FruitBlock;
import com.ferreusveritas.dynamictrees.data.provider.DTLootTableProvider;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;

public class DTPHC2Fruit extends Fruit {

    public static final TypedRegistry.EntryType<Fruit> TYPE = TypedRegistry.newType(DTPHC2Fruit::new);

    public DTPHC2Fruit(ResourceLocation registryName) {
        super(registryName);
    }

    private ResourceLocation itemStackLoc;
    private ItemStack itemStack;

    @Override
    public void setItemStack(ItemStack itemStack) {}

    public void setItemStackLoc(ResourceLocation itemStack) {
        itemStackLoc = itemStack;
    }

    public ItemStack computeItemStack() {
        if (itemStack == null){
            if (itemStackLoc == null) {
                throw new IllegalStateException("Invoked too early or item was not set on \"" + getRegistryName() + "\".");
            }
            Item item = ForgeRegistries.ITEMS.getValue(itemStackLoc);
            if (item == null || item == Items.AIR) {
                throw new IllegalStateException("Invalid item for \"" + getRegistryName() + "\".");
            }
            itemStack = new ItemStack(item);
        }
        return itemStack.copy();
    }

    protected FruitBlock createBlock(Block.Properties properties) {
        computeItemStack(); //This fetches the itemstack before the block is created.
        return new FruitBlock(properties, this);
    }

    public LootTable.Builder createBlockDrops() {
        return DTLootTableProvider.createFruitDrops(getBlock(), computeItemStack().getItem(), getAgeProperty(), getMaxAge());
    }

}
