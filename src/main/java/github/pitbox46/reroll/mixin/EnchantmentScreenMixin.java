package github.pitbox46.reroll.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import github.pitbox46.reroll.network.ClientProxy;
import github.pitbox46.reroll.network.RerollPacketHandler;
import github.pitbox46.reroll.network.RerollRequest;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends ContainerScreen<EnchantmentContainer> {

    @Unique private static final ResourceLocation REROLL_TEXTURE = new ResourceLocation("reroll", "textures/reroll_button.png");
    @Unique private static final ResourceLocation REROLL_TEXTURE_IN = new ResourceLocation("reroll", "textures/reroll_button_in.png");

    // TODO: these might change while the player is in the enchanting UI.
    @Unique private int playerLapis = 0;
    @Unique private int playerLevels = 0;

    private EnchantmentScreenMixin(EnchantmentContainer handler, PlayerInventory inventory, ITextComponent title) {
        super(handler, inventory, title);
    }

    @Inject(method = "<init>(Lnet/minecraft/inventory/container/EnchantmentContainer;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/text/ITextComponent;)V", at = @At("RETURN"))
    private void onInit(EnchantmentContainer handler, PlayerInventory inventory, ITextComponent title, CallbackInfo ci) {
        int lapis = 0;

        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if(stack.getItem().equals(Items.LAPIS_LAZULI)) {
                lapis+=stack.getCount();
            }
        }

        playerLevels = inventory.player.experienceLevel;
        playerLapis = lapis;

        // Prevent delay on button first time due to packets
        ClientProxy.getLapisPerReroll();
        ClientProxy.getExpPerReroll();
    }

    @Inject(method = "drawGuiContainerBackgroundLayer(Lcom/mojang/blaze3d/matrix/MatrixStack;FII)V", at = @At("RETURN"))
    private void onDrawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        assert minecraft != null;

        int x = (this.width - this.xSize) / 2 + 160;
        int y = (this.height - this.ySize) / 2 + 73;

        if(mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9) {
            this.minecraft.getTextureManager().bindTexture(REROLL_TEXTURE_IN);
            AbstractGui.blit(matrices, x, y, 0, 0, 9, 9, 9, 9);

            List<ITextComponent> content = new ArrayList<>();
            content.add(new TranslationTextComponent("reroll.tooltip").mergeStyle(TextFormatting.GRAY));

            if(ClientProxy.getExpPerReroll() > 0) {
                IFormattableTextComponent expPrompt = new TranslationTextComponent("reroll.exp_prompt").mergeStyle(TextFormatting.GREEN);
                IFormattableTextComponent expText = new TranslationTextComponent("reroll.exp_amount", ClientProxy.getExpPerReroll());

                if(playerLevels < ClientProxy.getExpPerReroll()) {
                    expText = expText.mergeStyle(TextFormatting.RED);
                } else {
                    expText = expText.mergeStyle(TextFormatting.GRAY);
                }

                content.add(expPrompt.appendSibling(expText));
            }

            if(ClientProxy.getLapisPerReroll() > 0) {
                IFormattableTextComponent lapisPrompt = new TranslationTextComponent("reroll.lapis_prompt").mergeStyle(TextFormatting.BLUE);
                IFormattableTextComponent lapisText = new TranslationTextComponent("reroll.lapis_amount", ClientProxy.getLapisPerReroll());

                if(playerLapis < ClientProxy.getLapisPerReroll()) {
                    lapisText = lapisText.mergeStyle(TextFormatting.RED);
                } else {
                    lapisText = lapisText.mergeStyle(TextFormatting.GRAY);
                }

                content.add(lapisPrompt.appendSibling(lapisText));
            }

            renderTooltip(matrices, Lists.transform(content, ITextComponent::func_241878_f), mouseX, mouseY);
        } else {
            this.minecraft.getTextureManager().bindTexture(REROLL_TEXTURE);
            AbstractGui.blit(matrices, x, y, 0, 0, 9, 9, 9, 9);
        }
    }

    @Inject(
            method = "mouseClicked(DDI)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        int x = (this.width - this.xSize) / 2 + 160;
        int y = (this.height - this.ySize) / 2 + 73;

        if(mouseX >= x && mouseX <= x + 9 && mouseY >= y && mouseY <= y + 9) {
            RerollPacketHandler.CHANNEL.sendToServer(new RerollRequest());
            cir.setReturnValue(true);
        }
    }
}
