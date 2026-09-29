package com.yny.targethealth;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import dev.xavier.stein.loader.api.HudElement;
import dev.xavier.stein.loader.api.HudPlacement;

final class TargetHealthElement implements HudElement {

    private static final int WIDTH = 132;
    private static final int HEIGHT = 31;
    private static final int BAR_WIDTH = 126;
    private static final int BAR_HEIGHT = 6;
    private static final double VISUAL_RANGE = 32.0D;

    private EntityLivingBase target;
    private EntityLivingBase namedTarget;
    private String name = "Target";
    private String healthText = "20 / 20";
    private float health = 20.0F;
    private float maximum = 20.0F;
    private float absorption;
    private EntityLivingBase distantTarget;

    void updateDistantTarget() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.getRenderViewEntity() == null) {
            distantTarget = null;
            return;
        }
        Entity camera = mc.getRenderViewEntity();
        Vec3 start = camera.getPositionEyes(1.0F);
        Vec3 look = camera.getLook(1.0F);
        Vec3 end = start.addVector(look.xCoord * VISUAL_RANGE, look.yCoord * VISUAL_RANGE,
                look.zCoord * VISUAL_RANGE);
        double limit = blockDistance(mc, start, end, look);
        EntityLivingBase nearest = null;
        double nearestDistance = limit;
        List<?> entities = mc.theWorld.loadedEntityList;
        for (int index = 0, size = entities.size(); index < size; index++) {
            Object value = entities.get(index);
            if (!(value instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase candidate = (EntityLivingBase) value;
            if (candidate == camera || candidate == mc.thePlayer || candidate.isDead || !candidate.canBeCollidedWith()) {
                continue;
            }
            AxisAlignedBB box = candidate.getEntityBoundingBox().expand(0.3D, 0.3D, 0.3D);
            MovingObjectPosition intercept = box.calculateIntercept(start, end);
            if (intercept == null) {
                continue;
            }
            double distance = start.distanceTo(intercept.hitVec);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        distantTarget = nearest;
    }

    private static double blockDistance(Minecraft mc, Vec3 start, Vec3 end, Vec3 direction) {
        Vec3 origin = start;
        for (int pass = 0; pass < 64; pass++) {
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(origin, end, false, false, true);
            if (hit == null) {
                return VISUAL_RANGE;
            }
            Block block = mc.theWorld.getBlockState(hit.getBlockPos()).getBlock();
            if (!(block instanceof BlockLeaves)) {
                return start.distanceTo(hit.hitVec);
            }
            origin = hit.hitVec.addVector(direction.xCoord * 0.01D, direction.yCoord * 0.01D,
                    direction.zCoord * 0.01D);
        }
        return VISUAL_RANGE;
    }

    void updateTarget() {
        target = findTarget();
        if (target == null) {
            return;
        }
        if (target != namedTarget) {
            namedTarget = target;
            name = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(
                    target.getDisplayName().getUnformattedText(), BAR_WIDTH);
        }
        float nextHealth = Math.max(0.0F, target.getHealth());
        float nextMaximum = Math.max(1.0F, target.getMaxHealth());
        float nextAbsorption = Math.max(0.0F, target.getAbsorptionAmount());
        if (health != nextHealth || maximum != nextMaximum) {
            healthText = format(nextHealth) + " / " + format(nextMaximum);
        }
        health = nextHealth;
        maximum = nextMaximum;
        absorption = nextAbsorption;
    }

    @Override
    public String id() {
        return TargetHealthMod.ID + ".target";
    }

    @Override
    public String name() {
        return "Target health";
    }

    @Override
    public boolean layout(boolean preview, float partialTicks) {
        if (!TargetHealthMod.settings.enabled) {
            return false;
        }
        updateTarget();
        if (target == null && !preview) {
            return false;
        }
        if (target == null) {
            namedTarget = null;
            name = "Zombie";
            health = 14.0F;
            maximum = 20.0F;
            absorption = 0.0F;
            healthText = "14 / 20";
        }
        return true;
    }

    private EntityLivingBase findTarget() {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition hit = mc.objectMouseOver;
        if (hit != null && hit.entityHit instanceof EntityLivingBase && hit.entityHit != mc.thePlayer) {
            return (EntityLivingBase) hit.entityHit;
        }
        Entity entity = mc.pointedEntity;
        if (entity instanceof EntityLivingBase && entity != mc.thePlayer) {
            return (EntityLivingBase) entity;
        }
        return distantTarget == mc.thePlayer ? null : distantTarget;
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

    @Override
    public void draw(boolean alignRight, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRendererObj;
        int x = alignRight ? -WIDTH : 0;

        Gui.drawRect(x, 0, x + WIDTH, HEIGHT, 0xB0101010);
        Gui.drawRect(x, 0, x + WIDTH, 1, 0xFFB83B3B);
        String clippedName = font.trimStringToWidth(name, BAR_WIDTH);
        font.drawStringWithShadow(clippedName, x + 3, 4, 0xFFFFFF);

        int barX = x + 3;
        int barY = 18;
        Gui.drawRect(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, 0xFF3B2020);
        int filled = Math.round(BAR_WIDTH * Math.min(health / maximum, 1.0F));
        Gui.drawRect(barX, barY, barX + filled, barY + BAR_HEIGHT, healthColor(health / maximum));

        if (TargetHealthMod.settings.showAbsorption && absorption > 0.0F) {
            int absorptionWidth = Math.round(BAR_WIDTH * Math.min(absorption / maximum, 1.0F));
            Gui.drawRect(barX + filled, barY, Math.min(barX + BAR_WIDTH, barX + filled + absorptionWidth),
                    barY + BAR_HEIGHT, 0xFFE7BE43);
        }
        if (TargetHealthMod.settings.showNumbers) {
            font.drawStringWithShadow(healthText, x + WIDTH - 3 - font.getStringWidth(healthText), 4, 0xFFFFFF);
        }
    }

    private static String format(float value) {
        return value == Math.round(value) ? Integer.toString(Math.round(value)) : String.format("%.1f", value);
    }

    private static int healthColor(float percent) {
        if (percent > 0.5F) return 0xFF45B85A;
        if (percent > 0.25F) return 0xFFE0A93B;
        return 0xFFCF4848;
    }

    @Override
    public HudPlacement defaultPlacement() {
        return new HudPlacement(1, 1, 0, 28, 1.0F, true, false);
    }
}
