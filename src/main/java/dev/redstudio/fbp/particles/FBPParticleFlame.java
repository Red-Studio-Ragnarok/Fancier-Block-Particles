package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.renderer.CubeBatchRenderer;
import dev.redstudio.fbp.renderer.RenderType;
import dev.redstudio.fbp.renderer.color.ColorUtil;
import dev.redstudio.fbp.renderer.light.LightUtil;
import dev.redstudio.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.redcore.vectors.Vector3F;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static dev.redstudio.fbp.FBP.MC;

public class FBPParticleFlame extends ParticleFlame {

    double startScale, scaleAlpha, prevParticleScale, prevParticleAlpha;
    double endMult = 1;

    boolean spawnAnother;

    Vector3F startPos;

    final float AngleY;

    protected FBPParticleFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mY, boolean spawnAnother) {
        super(worldIn, xCoordIn, yCoordIn - 0.06, zCoordIn, 0, mY, 0);
        IBlockState blockState = worldIn.getBlockState(new BlockPos(posX, posY, posZ));

        this.spawnAnother = spawnAnother;

        if (blockState == Blocks.TORCH.getDefaultState())
            prevPosY = posY = posY + 0.04;

        startPos = new Vector3F((float) posX, (float) posY, (float) posZ);

        motionY = -0.00085;
        particleGravity = -0.05f;

        particleScale *= FBP.scaleMult * 2.5;
        particleMaxAge = FBP.RANDOM.nextInt(3, 5);

        particleRed = 1;
        particleGreen = 1;
        particleBlue = 0;

        AngleY = rand.nextFloat() * 80;

        particleAlpha = 1;

        if (FBP.randomFadingSpeed)
            endMult *= FBP.RANDOM.nextDouble(0.9875, 1);

        multipleParticleScaleBy(1);
    }

    @Override
    public Particle multipleParticleScaleBy(float scale) {
        Particle particle = super.multipleParticleScaleBy(scale);

        startScale = particleScale;
        scaleAlpha = particleScale * 0.35;

        float newScale = particleScale / 80;

        setBoundingBox(new AxisAlignedBB(posX - newScale, posY - newScale, posZ - newScale, posX + newScale, posY + newScale, posZ + newScale));

        return particle;
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        prevParticleAlpha = particleAlpha;
        prevParticleScale = particleScale;

        if (!FBP.fancyFlame)
            isExpired = true;

        if (++particleAge >= particleMaxAge) {
            if (FBP.randomFadingSpeed)
                particleScale *= 0.95 * endMult;
            else
                particleScale *= 0.95;

            if (particleAlpha > 0.01 && particleScale <= scaleAlpha) {
                if (FBP.randomFadingSpeed)
                    particleAlpha *= 0.9 * endMult;
                else
                    particleAlpha *= 0.9;
            }

            if (particleAlpha <= 0.01)
                setExpired();
            else if (particleAlpha <= 0.325 && spawnAnother && world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == Blocks.TORCH) {
                spawnAnother = false;

                MC.effectRenderer.addEffect(new FBPParticleFlame(world, startPos.x, startPos.y, startPos.z, 0, false));
            }
        }

        motionY -= 0.02 * particleGravity;
        move(0, motionY, 0);
        motionY *= 0.95;

        if (onGround) {
            motionX *= 0.89;
            motionZ *= 0.89;
        }
    }

    @Override
    public void move(double x, double y, double z) {
        double Y = y;

        List<AxisAlignedBB> list = world.getCollisionBoxes(null, getBoundingBox().expand(x, y, z));

        for (AxisAlignedBB axisalignedbb : list) {
            y = axisalignedbb.calculateYOffset(getBoundingBox(), y);
        }

        setBoundingBox(getBoundingBox().offset(0, y, 0));

        for (AxisAlignedBB axisalignedbb : list) {
            x = axisalignedbb.calculateXOffset(getBoundingBox(), x);
        }

        setBoundingBox(getBoundingBox().offset(x, 0, 0));

        for (AxisAlignedBB axisalignedbb : list) {
            z = axisalignedbb.calculateZOffset(getBoundingBox(), z);
        }

        setBoundingBox(getBoundingBox().offset(0, 0, z));

        // RESET
        resetPositionToBB();
        onGround = y != Y;
    }

    @Override
    protected void resetPositionToBB() {
        AxisAlignedBB axisalignedbb = getBoundingBox();
        posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2;
        posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2;
        posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!FBP.enabled && particleMaxAge != 0)
            particleMaxAge = 0;

        float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        int brightness = getBrightnessForRender(partialTicks);

        float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

        float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);

        if (particleAge >= particleMaxAge)
            particleGreen = (float) (scale / startScale);

        scale *= 0.0125F;

        CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE, x, y, z, 0.0F, AngleY, 0.0F, scale, scale, scale,
                TextureUtil.pointTexCoordProvider(0.82109374F, 0.28984374F),
                ColorUtil.multiplyingColorProvider(particleRed, particleGreen, particleBlue, alpha, 0.95F),
                LightUtil.uniformLightCoordProvider(brightness));
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
    }
}
