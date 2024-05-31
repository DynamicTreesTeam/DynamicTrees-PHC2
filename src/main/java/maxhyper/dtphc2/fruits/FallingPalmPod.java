package maxhyper.dtphc2.fruits;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.PodBlock;
import com.ferreusveritas.dynamictrees.systems.pod.Pod;
import maxhyper.dtphc2.blocks.FallingPodBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class FallingPalmPod extends Pod {

    public static final TypedRegistry.EntryType<Pod> TYPE = TypedRegistry.newType(FallingPalmPod::new);

    public FallingPalmPod(ResourceLocation registryName) {
        super(registryName);
    }

    protected PodBlock createBlock(Block.Properties properties) {
        return new FallingPodBlock(properties, this);
    }

}
