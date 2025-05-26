package maxhyper.dtphc2.compat.waila;

import maxhyper.dtphc2.blocks.FruitVineBlock;
import maxhyper.dtphc2.blocks.MapleSpileCommon;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class WailaCompat implements IWailaPlugin {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WailaVineHandler.INSTANCE, FruitVineBlock.class);
        registration.registerBlockComponent(WailaSpileHandler.INSTANCE, MapleSpileCommon.class);
        registration.registerBlockIcon(WailaSpileHandler.INSTANCE, MapleSpileCommon.class);
    }

    @SuppressWarnings("removal")
    @Override
    public void register(IWailaCommonRegistration registrar) {
        //registrar.registerStackProvider(new WailaSpileHandler(), MapleSpileCommon.class);
    }

}
