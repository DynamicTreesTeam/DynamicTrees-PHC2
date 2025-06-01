package maxhyper.dtphc2.event;

import com.dtteam.dynamictrees.tree.TreeHelper;
import maxhyper.dtphc2.init.DTPHC2Blocks;
import maxhyper.dtphc2.init.DTPHC2Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

import static maxhyper.dtphc2.DynamicTreesPHC2.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class SpilePlacementEvent {

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() != Items.IRON_INGOT) return;

        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if(!state.is(DTPHC2Registries.CAN_BE_SPILED)) return;

        if (!TreeHelper.isBranch(state) || TreeHelper.getRadius(world, pos) < 7) return;

        BlockPos spilePos = pos.relative(Objects.requireNonNull(event.getFace()));
        if (!world.getBlockState(spilePos).canBeReplaced()) return;

        if (!player.isCreative()) heldItem.shrink(1);
        // Play a sound
        world.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1,1);
        // Place the MapleSpileBlock at the clicked block's position
        BlockPlaceContext context = new BlockPlaceContext(player, hand, heldItem, new BlockHitResult(player.getEyePosition(1.0f), event.getFace(), pos, false));
        BlockState placeState = DTPHC2Blocks.MAPLE_SPILE_BLOCK.get().getStateForPlacement(context);
        if (placeState == null) return;
        world.setBlock(spilePos, placeState, 3);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}