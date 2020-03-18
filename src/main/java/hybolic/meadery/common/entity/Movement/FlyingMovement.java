package hybolic.meadery.common.entity.Movement;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;

public class FlyingMovement extends MovementController {
	private final int Speed;
	private final boolean isFlying;

	public FlyingMovement(MobEntity mob, int speed, boolean isFlying) {
		super(mob);
		this.Speed = speed;
		this.isFlying = isFlying;
	}

	public void tick() {
		if (this.action == MovementController.Action.MOVE_TO) {
			this.action = MovementController.Action.WAIT;
			this.mob.setNoGravity(true);
			double d0 = this.posX - this.mob.posX;
			double d1 = this.posY - this.mob.posY;
			double d2 = this.posZ - this.mob.posZ;
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			if (d3 < (double) 2.5000003E-7F) {
				this.mob.setMoveVertical(0.0F);
				this.mob.setMoveForward(0.0F);
				return;
			}

			float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
			this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, f, 90.0F);
			float f1;
			if (this.mob.onGround) {
				f1 = (float) (this.Speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
			} else {
				f1 = (float) (this.Speed * this.mob.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getValue());
			}

			this.mob.setAIMoveSpeed(f1);
			double d4 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
			float f2 = (float) (-(MathHelper.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
			this.mob.rotationPitch = this.limitAngle(this.mob.rotationPitch, f2, (float) this.Speed);
			this.mob.setMoveVertical(d1 > 0.0D ? f1 : -f1);
		} else {
			if (!this.isFlying) {
				this.mob.setNoGravity(false);
			}

			this.mob.setMoveVertical(0.0F);
			this.mob.setMoveForward(0.0F);
		}

	}
}