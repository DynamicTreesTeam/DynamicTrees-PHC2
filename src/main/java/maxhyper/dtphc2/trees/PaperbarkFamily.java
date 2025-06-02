package maxhyper.dtphc2.trees;

import com.dtteam.dynamictrees.api.registry.TypedRegistry;
import com.dtteam.dynamictrees.block.branch.BasicBranchBlock;
import com.dtteam.dynamictrees.block.branch.BranchBlock;
import com.dtteam.dynamictrees.platform.Services;
import com.dtteam.dynamictrees.platform.services.IConfigHelper;
import com.dtteam.dynamictrees.tree.TreeHelper;
import com.dtteam.dynamictrees.tree.family.Family;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PaperbarkFamily extends Family {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(PaperbarkFamily::new);

    ItemStack paperStack;
    float barkRegrowChance = 0.01f;

    public PaperbarkFamily(ResourceLocation name) {
        super(name);
        paperStack = new ItemStack(Items.PAPER);
    }

    public ItemStack getPaperStack(Level world) {
        ItemStack stack = paperStack.copy();
        stack.setCount(1 + world.random.nextInt(3));
        return stack;
    }

    @Override
    protected BranchBlock createBranchBlock(ResourceLocation name) {
        final BasicBranchBlock branch = new BasicBranchBlock(name, this.getProperties().randomTicks()){
            @Override
            public void stripBranch(BlockState state, Level world, BlockPos pos, Player player, ItemStack heldItem) {
                Vec3 center = new Vec3(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
                Vec3 offsetDir = player.position().subtract(center).normalize().multiply(0.5,0.5,0.5);
                center = center.add(offsetDir);
                world.addFreshEntity(new ItemEntity(world, center.x, center.y, center.z, getPaperStack(world)));
                super.stripBranch(state,world,pos, getRadius(state));
            }

            @Override
            protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
                if (rand.nextFloat() < barkRegrowChance && isStrippedBranch()){
                    int radius = TreeHelper.getRadius(level, pos);
                    int radiusDown = TreeHelper.isBranch(level.getBlockState(pos.below())) ? TreeHelper.getRadius(level, pos.below()) : getMaxRadius();
                    this.getFamily().getBranch().ifPresent(branch -> branch.setRadius(level, pos,
                                    Math.min(radiusDown, radius + (Services.CONFIG.getBoolConfig(IConfigHelper.ENABLE_STRIP_RADIUS_REDUCTION) ? 1 : 0)),
                                    null
                            )
                    );
                }
                super.randomTick(state, level, pos, rand);
            }

        };
        if (this.isFireProof()) branch.setFireSpreadSpeed(0).setFlammability(0);
        return branch;
    }

}
