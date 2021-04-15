package github.pitbox46.reroll;

import github.pitbox46.reroll.config.Config;
import github.pitbox46.reroll.impl.PlayerEntityManipulator;
import github.pitbox46.reroll.network.ClientProxy;
import github.pitbox46.reroll.network.CommonProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tfar.balancedenchanting.BalancedEnchanting;


@Mod("reroll")
public class Reroll {
    public static CommonProxy PROXY;
    private static boolean hasBalancedEnchanting = false;

    public Reroll() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onProcessIMC);
    }

    private void onProcessIMC(InterModProcessEvent event) {
        hasBalancedEnchanting = event.getIMCStream().anyMatch(mod -> mod.getModId().equals("balancedenchanting"));
    }

    public static void reroll(PlayerEntity player) {
        int lapisToRemove = Config.LAPIS_PER_REROLL.get();
        int levelsPerReroll = Config.LEVELS_PER_REROLL.get();

        if (player.openContainer instanceof EnchantmentContainer) {
            Inventory inventory = (Inventory) ((EnchantmentContainer) player.openContainer).tableInventory;
            ItemStack input = inventory.getStackInSlot(0);

            // If the input stack does not have an item, do not reroll.
            if(input.isEmpty()) {
                return;
            }

            int playerLevels = player.experienceLevel;
            ItemStack lapisStack = inventory.getStackInSlot(1);

            if ((playerLevels >= levelsPerReroll && lapisStack.getCount() >= lapisToRemove) || player.isCreative()) {
                // update seed & enchantment screen
                ((PlayerEntityManipulator) player).rerollEnchantmentSeed();
                ((EnchantmentContainer) player.openContainer).xpSeed.set(player.getXPSeed());
                player.openContainer.onCraftMatrixChanged(inventory);

                // take cost from player
                if(!player.isCreative()) {
                    try {
                        player.giveExperiencePoints(-BalancedEnchanting.convertLevelToTotalxp(levelsPerReroll));
                    } catch (NoClassDefFoundError e) {
                        player.addExperienceLevel(-levelsPerReroll);
                    }
                    ItemStack newLapisStack = lapisStack.copy();
                    newLapisStack.shrink(lapisToRemove);
                    inventory.setInventorySlotContents(1, newLapisStack);
                }
            }
        }
    }
}
