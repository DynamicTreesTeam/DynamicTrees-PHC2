package maxhyper.dtphc2.fruits;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.PodBlock;
import com.ferreusveritas.dynamictrees.data.provider.DTLootTableProvider;
import com.ferreusveritas.dynamictrees.systems.pod.Pod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;

public class DTPHC2Pod extends Pod {

    public static final TypedRegistry.EntryType<Pod> TYPE = TypedRegistry.newType(DTPHC2Pod::new);

    public DTPHC2Pod(ResourceLocation registryName) {
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

    @Override
    protected PodBlock createBlock(BlockBehaviour.Properties properties) {
        computeItemStack();
        return super.createBlock(properties);
    }

    public LootTable.Builder createBlockDrops() {
        return DTLootTableProvider.createPodDrops(getBlock(), computeItemStack().getItem(), getAgeProperty(), getMaxAge());
    }

}
