package github.pitbox46.reroll.mixin;

import github.pitbox46.reroll.impl.PlayerEntityManipulator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityManipulator {
    @Shadow protected int xpSeed;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rerollEnchantmentSeed() {
        this.xpSeed = this.rand.nextInt();
    }
}
