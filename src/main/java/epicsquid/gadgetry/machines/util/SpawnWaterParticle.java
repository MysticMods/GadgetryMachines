package epicsquid.gadgetry.machines.util;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleWaterWake;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class SpawnWaterParticle {

  public static void spawn(@Nonnull Vec3i dir, @Nonnull World world, float baseX, float baseY, float baseZ, float sin) {
    for (int i = 0; i < 4; i++) {
      ParticleWaterWake p = new ParticleWaterWake(world, baseX + (9f / 16f) * (i / 3f) * dir.getZ(), baseY, baseZ + (9f / 16f) * (i / 3f) * dir.getX(),
          sin * 0.25 * dir.getX() + (-0.1f + 0.2f * (i / 3f)) * dir.getZ(), 0.6, sin * 0.25 * dir.getZ() + (-0.1f + 0.2f * (i / 3f)) * dir.getX()) {
        @Override
        public void onUpdate() {
          if (this.particleAge == 0) {
            particleGravity = 0.05f;
            particleMaxAge = 40;
          }
          this.particleAge++;
          super.onUpdate();
        }
      };
      Minecraft.getMinecraft().effectRenderer.addEffect(p);
    }
  }
}
