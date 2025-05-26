package maxhyper.dtphc2.trees;

import com.dtteam.dynamictrees.api.registry.TypedRegistry;
import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.systems.nodemapper.NetVolumeNode;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedList;
import java.util.List;

public class FruitLogSpecies extends Species {

    public static final TypedRegistry.EntryType<Species> TYPE = createDefaultType(FruitLogSpecies::new);

    //private ResourceLocation dropItemLoc = new ResourceLocation("air");
    private Item dropItem = null;
    private float multiplier = 1;
    private Item fakeLog = Items.AIR;

    public FruitLogSpecies(ResourceLocation name, Family family, LeavesProperties leavesProperties) {
        super(name, family, leavesProperties);
    }

    public LogsAndSticks getLogsAndSticks(NetVolumeNode.Volume volume, boolean silkTouch, int fortuneLevel) {
        float volRaw = volume.getRawVolume() / (float) NetVolumeNode.Volume.VOXELSPERLOG;
        int vol = (int)volRaw;
        float stickVol = volRaw - vol;
        List<ItemStack> drops = new LinkedList<>();

        if (silkTouch){
            int logVol = vol;
            ItemStack logStack = new ItemStack(family.getPrimitiveLog().orElse(Blocks.AIR));
            while (logVol > 0) {
                ItemStack drop = logStack.copy();
                drop.setCount(Math.min(logVol, logStack.getMaxStackSize()));
                drops.add(drop);
                logVol -= logStack.getMaxStackSize();
            }
        } else {
            if (dropItem != Items.AIR) {
                int itemVol = (int)(vol * multiplier);
                ItemStack stack = new ItemStack(dropItem);
                while (itemVol > 0) {
                    ItemStack drop = stack.copy();
                    drop.setCount(Math.min(itemVol, stack.getMaxStackSize()));
                    drops.add(drop);
                    itemVol -= stack.getMaxStackSize();
                }
            }
            if (fakeLog != Items.AIR){
                int logVol = vol;
                ItemStack logStack = new ItemStack(fakeLog);
                while (logVol > 0) {
                    ItemStack drop = logStack.copy();
                    drop.setCount(Math.min(logVol, logStack.getMaxStackSize()));
                    drops.add(drop);
                    logVol -= logStack.getMaxStackSize();
                }
            }
        }
        return new LogsAndSticks(drops, (int)(stickVol * 8));
    }

//    public void setDropItem(ResourceLocation resLoc) {
//        this.dropItemLoc = resLoc;
//    }
    public void setDropItem(Item item) {
        this.dropItem = item;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void setFakeLog(Item fakeLog) {
        this.fakeLog = fakeLog;
    }
}
