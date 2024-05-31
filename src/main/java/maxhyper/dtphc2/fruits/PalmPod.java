package maxhyper.dtphc2.fruits;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.PodBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.systems.pod.Pod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PalmPod extends Pod {

    public static final TypedRegistry.EntryType<Pod> TYPE = TypedRegistry.newType(PalmPod::new);

    public PalmPod(ResourceLocation registryName) {
        super(registryName);
    }

    protected PodBlock createBlock(Block.Properties properties) {
        return new PodBlock(properties, this){
            @Override public boolean isSupported(LevelReader world, BlockPos pos, BlockState state) {
                final BlockState branchState = world.getBlockState(pos.relative(state.getValue(HorizontalDirectionalBlock.FACING)));
                final BranchBlock branch = TreeHelper.getBranch(branchState);
                return branch != null && branch.getRadius(branchState) >= 2;
            }
        };
    }

}
