package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/**
 * Small player-shaped 3D model made from explicit ModelPart "bones".
 * The bones are animated independently from the real player's animations.
 */
public class HighFiveCloneModel extends EntityModel<HighFiveCloneEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation("firetrail", "high_five_clone"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public HighFiveCloneModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(32, 48)
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(16, 48)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(HighFiveCloneEntity clone, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float t = clone.getAge() + (ageInTicks - (int) ageInTicks);

        // Neutral pose. No walking animation means the model cannot inherit
        // the player's movement/casting animation.
        head.xRot = 0.0F;
        head.yRot = 0.0F;
        head.zRot = 0.0F;
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;
        leftArm.xRot = 0.08F;
        leftArm.yRot = 0.0F;
        leftArm.zRot = 0.04F;
        rightLeg.xRot = 0.0F;
        leftLeg.xRot = 0.0F;

        // A gentle breathing/idle motion while approaching.
        float idle = (float) Math.sin(t * 0.18F) * 0.025F;
        body.y = idle;
        head.y = idle;

        // Separate three-stage high-five animation:
        // raise -> hold/contact -> lower. Smoothstep avoids snapping.
        float raise = clone.getHighFiveAnimation(0.0F);
        float reach = clone.getHighFiveReach(0.0F);

        // The arm rotates up and slightly toward the player. The small Z tilt
        // makes the pose read as an open hand instead of a punch.
        float handLift = smoothStep(raise);
        rightArm.xRot = -0.95F - 0.48F * handLift;
        rightArm.yRot = -0.10F - 0.18F * handLift;
        rightArm.zRot = -0.10F - 0.18F * handLift;

        // A tiny shoulder/body counter-motion makes the gesture feel organic.
        body.zRot = -0.035F * handLift;
        head.zRot = -0.018F * handLift;

        // During the contact hold, keep the pose perfectly still.
        if (reach > 0.92F && reach < 1.01F) {
            rightArm.xRot = -1.43F;
            rightArm.yRot = -0.28F;
            rightArm.zRot = -0.28F;
        }
    }

    private static float smoothStep(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * (3.0F - 2.0F * x);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
