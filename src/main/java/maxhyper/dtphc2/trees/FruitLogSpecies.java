package maxhyper.dtphc2.trees;

import com.dtteam.dynamictrees.api.registry.RegistryHandler;
import com.dtteam.dynamictrees.api.registry.TypedRegistry;
import com.dtteam.dynamictrees.api.worldgen.LevelContext;
import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.item.Seed;
import com.dtteam.dynamictrees.systems.nodemapper.NetVolumeNode;
import com.dtteam.dynamictrees.systems.season.SeasonHelper;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import maxhyper.dtphc2.genfeatures.SyrupGenFeature;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FruitLogSpecies extends Species {

    public static final TypedRegistry.EntryType<Species> TYPE = createDefaultType(FruitLogSpecies::new);

    //private ResourceLocation dropItemLoc = new ResourceLocation("air");
    private Item dropItem = null;
    private float item_multiplier = 1;
    private float fake_log_multiplier = 1;
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
                int itemVol = (int)(vol * item_multiplier);
                ItemStack stack = new ItemStack(dropItem);
                while (itemVol > 0) {
                    ItemStack drop = stack.copy();
                    drop.setCount(Math.min(itemVol, stack.getMaxStackSize()));
                    drops.add(drop);
                    itemVol -= stack.getMaxStackSize();
                }
            }
            if (fakeLog != Items.AIR){
                int logVol = (int)(vol * fake_log_multiplier);
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

    public void setItemMultiplier(float multiplier) {
        this.item_multiplier = multiplier;
    }

    public void setFakeLogMultiplier(float multiplier) {
        this.fake_log_multiplier = multiplier;
    }

    public void setFakeLog(Item fakeLog) {
        this.fakeLog = fakeLog;
    }

    //Appends seasonal hover text to trees that use the Syrup gen feature (maple trees).
    @Override
    public Species generateSeed() {
        return this.shouldGenerateSeed() && this.seed == null ? this.setSeed(RegistryHandler.addItem(this.getSeedName(), () -> new Seed(this){
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                Level level = context.level();
                if (level == null) return;
                if (SeasonHelper.getSeasonValue(LevelContext.create(level), BlockPos.ZERO) == null) return;
                int flags = getSeasonalTooltipFlags(LevelContext.create(level));

                if (flags != 0) {
                    tooltipComponents.add(Component.translatable("desc.dynamictrees.seasonal.fertile_seasons").append(":"));

                    if ((flags & 15) == 15) {
                        tooltipComponents.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.year_round").withStyle(ChatFormatting.LIGHT_PURPLE)));
                    } else {
                        if ((flags & 1) != 0) {
                            tooltipComponents.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.spring").withStyle(ChatFormatting.GREEN)));
                        }
                        if ((flags & 2) != 0) {
                            tooltipComponents.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.summer").withStyle(ChatFormatting.YELLOW)));
                        }
                        if ((flags & 4) != 0) {
                            tooltipComponents.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.autumn").withStyle(ChatFormatting.GOLD)));
                        }
                        if ((flags & 8) != 0) {
                            tooltipComponents.add(Component.literal(" ").append(Component.translatable("desc.sereneseasons.winter").withStyle(ChatFormatting.AQUA)));
                        }
                    }
                }
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }

            public int getSeasonalTooltipFlags(LevelContext levelContext) {
                final float seasonStart = 1f / 6;
                final float seasonEnd = 1 - 1f / 6;
                final float threshold = 0.75f;

                AtomicReference<Float> seasonOffset = new AtomicReference<>();
                AtomicBoolean found = new AtomicBoolean(false);
                getSpecies().getGenFeatures().forEach(gf->{
                    if (gf.getGenFeature() instanceof SyrupGenFeature){
                        seasonOffset.set(gf.get(SyrupGenFeature.SEASONAL_OFFSET));
                        found.set(true);
                    }
                });
                if (!found.get()) return 0;

                int seasonFlags = 0;

                for(int i = 0; i < 4; ++i) {
                    boolean isValidSeason = false;
                    if (seasonOffset.get() != null) {
                        float prod1 = SeasonHelper.globalSeasonalFruitProductionFactor(levelContext, new BlockPos(0, (int)(((float)i + seasonStart - seasonOffset.get()) * 64.0F), 0), true);
                        float prod2 = SeasonHelper.globalSeasonalFruitProductionFactor(levelContext, new BlockPos(0, (int)(((float)i + seasonEnd - seasonOffset.get()) * 64.0F), 0), true);
                        if (Math.min(prod1, prod2) > threshold) {
                            isValidSeason = true;
                        }
                    } else {
                        isValidSeason = true;
                    }

                    if (isValidSeason) {
                        seasonFlags |= 1 << i;
                    }
                }

                return seasonFlags;

            }

        })) : this;
    }
}
