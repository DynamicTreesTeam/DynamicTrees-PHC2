package maxhyper.dtphc2.models;

import com.mojang.datafixers.util.Pair;
import maxhyper.dtphc2.models.baked_models.LargePalmLeavesBakedModel;
import maxhyper.dtphc2.models.baked_models.MediumPalmLeavesBakedModel;
import maxhyper.dtphc2.models.baked_models.SmallPalmLeavesBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PalmLeavesModelGeometry implements IUnbakedGeometry<PalmLeavesModelGeometry> {

    protected final ResourceLocation frondsResLoc;

    private final int frondType;

    public PalmLeavesModelGeometry (@Nullable final ResourceLocation frondsResLoc, int type){
        this.frondsResLoc = frondsResLoc;
        this.frondType = type;
    }

//    @Override
//    public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
//        if (frondsResLoc == null) return new HashSet<>();
//        return Collections.singleton(new Material(TextureAtlas.LOCATION_BLOCKS, frondsResLoc));
//    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return switch (frondType) {
            default -> new LargePalmLeavesBakedModel(modelLocation, frondsResLoc);
            case 1 -> new MediumPalmLeavesBakedModel(modelLocation, frondsResLoc);
            case 2 -> new SmallPalmLeavesBakedModel(modelLocation, frondsResLoc);
        };
    }
}