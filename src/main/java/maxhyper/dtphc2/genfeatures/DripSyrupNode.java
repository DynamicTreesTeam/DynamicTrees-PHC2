package maxhyper.dtphc2.genfeatures;

import com.dtteam.dynamictrees.api.network.NodeInspector;
import com.dtteam.dynamictrees.tree.TreeHelper;
import maxhyper.dtphc2.blocks.MapleSpileBlock;
import maxhyper.dtphc2.blocks.MapleSpileBucketBlock;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class DripSyrupNode implements NodeInspector {

    private boolean finished = false;
    private int bucketsVisited = 0;
    public int maxBuckets = 4;

    @Override
    public boolean run(BlockState blockState, LevelAccessor world, BlockPos pos, Direction fromDir) {

        if(!finished && TreeHelper.isBranch(blockState)) { //Only process branch blocks
            if(fromDir != Direction.DOWN && fromDir != null) {//If we turn then we're no longer in the main trunk where the buckets are. Null direction means we're in the origin node
                finished = true;
                return false;
            }
            for(Direction face : Direction.Plane.HORIZONTAL.shuffledCopy(world.getRandom())) { //Check all sides of this block
                BlockPos offPos = pos.relative(face);
                BlockState state = world.getBlockState(offPos);
                if(state.getBlock() == DTPHC2Blocks.MAPLE_SPILE_BUCKET_BLOCK.get()) { //Found a bucket
                    bucketsVisited++;
                    if(bucketsVisited <= maxBuckets) {
                        int filling = state.getValue(MapleSpileBucketBlock.FILLING);
                        if (filling < MapleSpileBucketBlock.maxFilling) {//Fill it up a little bit if there's room
                            world.setBlock(offPos, state.setValue(MapleSpileBucketBlock.FILLING, filling + 1), 3);
                            finished = true;
                            return false;
                        }
                    } else {
                        finished = true; //We've hit our limit of buckets we can visit
                    }
                }
                else if(state.getBlock() == DTPHC2Blocks.MAPLE_SPILE_BLOCK.get()) {
                    boolean filled = state.getValue(MapleSpileBlock.FILLED);
                    if(!filled) {
                        world.setBlock(offPos, state.setValue(MapleSpileBlock.FILLED, true), 3);
                    }
                    finished = true; //A spile without a bucket simply drips wastefully.
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean returnRun(BlockState blockState, LevelAccessor world, BlockPos pos, Direction fromDir) {
        return false;
    }

}
