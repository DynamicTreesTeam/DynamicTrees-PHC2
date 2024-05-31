package maxhyper.dtphc2.fruits;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.FruitBlock;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

public class CobwebFruit extends Fruit {

    public static final TypedRegistry.EntryType<Fruit> TYPE = TypedRegistry.newType(CobwebFruit::new);

    public CobwebFruit(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected FruitBlock createBlock(Block.Properties properties) {
        return new FruitBlock(properties.noCollission(), this){
            @Override
            public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
                pEntity.makeStuckInBlock(pState, new Vec3(0.25D, (double)0.05F, 0.25D));
            }
        };
    }

    public Material getDefaultMaterial() {
        return Material.WEB;
    }

}
