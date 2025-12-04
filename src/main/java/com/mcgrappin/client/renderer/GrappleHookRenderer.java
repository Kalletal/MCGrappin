package com.mcgrappin.client.renderer;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.entities.GrappleHookEntity;
import com.mcgrappin.entities.GrappleState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GrappleHookRenderer extends EntityRenderer<GrappleHookEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "textures/entity/grapple_hook.png");

    public GrappleHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GrappleHookEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Player owner = entity.getPlayerOwner();
        if (owner == null) {
            return;
        }

        poseStack.pushPose();

        // Render the hook oriented in movement direction (like an arrow/trident)
        poseStack.pushPose();

        // Calculate rotation based on velocity
        Vec3 velocity = entity.getDeltaMovement();
        float yaw = (float) (Mth.atan2(velocity.x, velocity.z) * (180.0 / Math.PI));
        float pitch = (float) (Mth.atan2(velocity.y, velocity.horizontalDistance()) * (180.0 / Math.PI));

        // Apply rotations to orient the projectile
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

        // Scale the projectile
        poseStack.scale(0.5F, 0.5F, 0.5F);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        // Render a quad for the arrow/hook (facing forward)
        float size = 0.5F;
        // Front face
        vertexConsumer.addVertex(matrix4f, -size, 0, -size)
                .setColor(255, 255, 255, 255)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, size, 0, -size)
                .setColor(255, 255, 255, 255)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, size, 0, size)
                .setColor(255, 255, 255, 255)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, -size, 0, size)
                .setColor(255, 255, 255, 255)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);

        // Back face (rotated 90 degrees for visibility from all angles)
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        pose = poseStack.last();
        matrix4f = pose.pose();

        vertexConsumer.addVertex(matrix4f, -size, 0, -size)
                .setColor(255, 255, 255, 255)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, size, 0, -size)
                .setColor(255, 255, 255, 255)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, size, 0, size)
                .setColor(255, 255, 255, 255)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        vertexConsumer.addVertex(matrix4f, -size, 0, size)
                .setColor(255, 255, 255, 255)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0, 1, 0);

        poseStack.popPose();

        // Render the rope from owner to hook
        Vec3 ownerPos = getOwnerPosition(owner, partialTick);
        Vec3 hookPos = entity.position();

        double dx = ownerPos.x - hookPos.x;
        double dy = ownerPos.y - hookPos.y;
        double dz = ownerPos.z - hookPos.z;

        renderRope(poseStack, buffer, dx, dy, dz);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private Vec3 getOwnerPosition(Player owner, float partialTick) {
        double x = Mth.lerp(partialTick, owner.xo, owner.getX());
        double y = Mth.lerp(partialTick, owner.yo, owner.getY()) + owner.getEyeHeight() * 0.8;
        double z = Mth.lerp(partialTick, owner.zo, owner.getZ());
        return new Vec3(x, y, z);
    }

    private void renderRope(PoseStack poseStack, MultiBufferSource buffer,
                            double dx, double dy, double dz) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.lineStrip());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();

        int segments = 16;
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float x = (float) (dx * t);
            float y = (float) (dy * t);
            float z = (float) (dz * t);

            // Metallic chain/rope color (gray)
            vertexConsumer.addVertex(matrix4f, x, y, z)
                    .setColor(120, 120, 130, 255)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose pose,
                               int light, float x, int y, int u, int v) {
        consumer.addVertex(matrix4f, x - 0.5F, (float) y - 0.5F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(GrappleHookEntity entity) {
        return TEXTURE;
    }
}
