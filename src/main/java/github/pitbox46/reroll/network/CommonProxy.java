package github.pitbox46.reroll.network;

import github.pitbox46.reroll.Reroll;
import github.pitbox46.reroll.config.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Objects;

public class CommonProxy {
    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        RerollPacketHandler.init();
    }

    /* Server */
    public void handleDataRequest(NetworkEvent.Context ctx) {
        RerollPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(ctx::getSender),
                new DataReturn(Config.LEVELS_PER_REROLL.get(), Config.LAPIS_PER_REROLL.get()));
    }

    public void handleReroll(NetworkEvent.Context ctx) {
        Reroll.reroll(Objects.requireNonNull(ctx.getSender()));
    }

    /* Client */
    public void handleDataReturn(DataReturn msg) {
    }
}
