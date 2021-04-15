package github.pitbox46.reroll.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    private static int cachedExp = -1;
    private static int cachedLapis = -1;

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleDataReturn(DataReturn msg) {
        cachedExp = msg.levelsPerReroll;
        cachedLapis = msg.lapisPerReroll;
    }

    public static int getExpPerReroll() {
        if(cachedExp == -1) {
            requestData();
            return 1;
        }

        return cachedExp;
    }

    public static int getLapisPerReroll() {
        if(cachedLapis == - 1) {
            requestData();
            return 0;
        }

        return cachedLapis;
    }

    private static void requestData() {
        assert Minecraft.getInstance().player != null;
        RerollPacketHandler.CHANNEL.sendToServer(new DataRequest());
    }
}
