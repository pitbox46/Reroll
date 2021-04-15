package github.pitbox46.reroll.network;

import github.pitbox46.reroll.Reroll;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class RerollPacketHandler {
    private static final String PROTOCOL_VERSION = "3.2.0";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("reroll","main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int ID = 0;

    public static void init() {
        CHANNEL.registerMessage(
                ID++,
                DataRequest.class,
                (msg, pb) -> {},
                pb -> new DataRequest(),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> Reroll.PROXY.handleDataRequest(ctx.get()));
                    ctx.get().setPacketHandled(true);
                });
        CHANNEL.registerMessage(
                ID++,
                DataReturn.class,
                (msg, pb) -> {
                    pb.writeVarInt(msg.levelsPerReroll);
                    pb.writeVarInt(msg.lapisPerReroll);
                },
                pb -> new DataReturn(pb.readVarInt(), pb.readVarInt()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> Reroll.PROXY.handleDataReturn(msg));
                    ctx.get().setPacketHandled(true);
                });
        CHANNEL.registerMessage(
                ID++,
                RerollRequest.class,
                (msg, pb) -> {},
                pb -> new RerollRequest(),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> Reroll.PROXY.handleReroll(ctx.get()));
                    ctx.get().setPacketHandled(true);
                });
    }
}
