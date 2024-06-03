package maxhyper.dtphc2.blocks;

import com.ferreusveritas.dynamictrees.block.FruitBlock;
import com.ferreusveritas.dynamictrees.block.rooty.RootyBlock;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import maxhyper.dtphc2.DynamicTreesPHC2;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

public class FallingFruitBlock extends FruitBlock implements IFallingFruit {

    //DamageSource damageSource;

    public static float randomFruitFallChance = 0.005f;
    public static float playerDistanceToFall = 10f;

    public FallingFruitBlock(Properties properties, Fruit fruit) {
        super(properties, fruit);
        //damageSource = new DamageSource(DamageTypes.FALL)
                //new DamageSource(DynamicTreesPHC2.MOD_ID+".falling_fruit."+ fruit.getRegistryName().getPath());
    }

    @Override
    public void doTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (checkToFall(state, world, pos, random)){
            doFall(state, world, pos);
        } else
            super.doTick(state, world, pos, random);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!isSupported(world, pos, state)) {
            if (!doFall(state, world, pos))
                super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        }
        DebugPackets.sendNeighborsUpdatePacket(world, pos);
    }

    @Override
    public ItemStack getDropOnFallItems(ItemLike item, @Nonnull FallingBlockEntity entity) {
        if (entity.getServer() == null) return ItemStack.EMPTY;
        ServerLevel world = entity.getServer().getLevel(entity.level().dimension());
        if (world == null) return ItemStack.EMPTY;
        List<ItemStack> items = getDrops(entity.getBlockState(), world, entity.blockPosition(), null);
        return items.isEmpty() ? ItemStack.EMPTY : items.get(0);
    }

    @Override
    public DamageSource getDamageSource(Level level) {
        return level.damageSources().fallingBlock(null);
        //return damageSource;
    }

    @Override
    public int getRootY(BlockState state, Level world, BlockPos pos) {
        for (int i=0;i<20;i++){
            BlockPos pos2 = pos.below(i);
            if (world.getBlockState(pos2).getBlock() instanceof RootyBlock){
                return pos2.getY();
            }
        }
        return pos.getY();
    }

    @Override
    public float getRandomFruitFallChance() {
        return randomFruitFallChance;
    }

    @Override
    public float getPlayerDistanceToFall() {
        return playerDistanceToFall;
    }
}
