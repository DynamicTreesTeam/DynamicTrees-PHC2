package maxhyper.dtphc2.blocks;

import com.dtteam.dynamictrees.api.registry.TypedRegistry;
import com.dtteam.dynamictrees.block.leaves.DynamicLeavesBlock;
import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.block.leaves.PalmLeavesProperties;
import maxhyper.dtphc2.DynamicTreesPHC2;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nonnull;

public class DragonFruitLeavesProperties extends PalmLeavesProperties {

    public static final TypedRegistry.EntryType<LeavesProperties> TYPE = TypedRegistry.newType(DragonFruitLeavesProperties::new);

    public DragonFruitLeavesProperties(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public MapColor getDefaultMapColor() {
        return MapColor.PLANT;
    }

    @Override
    @Nonnull
    protected DynamicLeavesBlock createDynamicLeaves(@Nonnull BlockBehaviour.Properties properties) {
        return new DynamicDragonfruitLeavesBlock(this, properties);
    }

    public static class DynamicDragonfruitLeavesBlock extends DynamicPalmLeavesBlock {

        public DynamicDragonfruitLeavesBlock(LeavesProperties leavesProperties, Properties properties) {
            super(leavesProperties, properties.strength(0.4F).sound(SoundType.WOOL));
        }

        private static final double hurtMovementDelta = 0.003;
        @Override
        public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entity) {
            boolean damage = false;
            if (DynamicTreesPHC2.DTPlusConfig.cactusPrickleOnMoveOnlyConfig() && entity instanceof LivingEntity) {
                boolean falling = entity.getDeltaMovement().y < 0;
                entity.setDeltaMovement(entity.getDeltaMovement().x * 0.25, entity.getDeltaMovement().y * (falling?0.5:1), entity.getDeltaMovement().z  * 0.25);
                if (!worldIn.isClientSide && (entity.xOld != entity.getX() || entity.yOld != entity.getY() || entity.zOld != entity.getZ())) {
                    double xMovement = Math.abs(entity.getX() - entity.xOld);
                    double yMovement = Math.abs(entity.getY() - entity.yOld);
                    double zMovement = Math.abs(entity.getZ() - entity.zOld);
                    if (xMovement >= hurtMovementDelta || yMovement >= hurtMovementDelta || zMovement >= hurtMovementDelta) {
                        damage = true;
                    }
                }
            } else if (!(entity instanceof ItemEntity) || DynamicTreesPHC2.DTPlusConfig.cactusKillItemsConfig()) {
                damage = true;
            }

            if (damage) entity.hurt(worldIn.damageSources().cactus(), 1.0F);
        }


    }

}
