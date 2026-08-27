package me.almana.whistles.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Navigation;
import me.almana.whistles.sound.TrainArrivalSound;

@Mixin(value = CarriageContraptionEntity.class, remap = false)
public abstract class CarriageContraptionEntityMixin {

	@Redirect(method = "tickContraption", at = @At(value = "FIELD",
		target = "Lcom/simibubi/create/content/trains/entity/Navigation;announceArrival:Z",
		opcode = Opcodes.GETFIELD))
	private boolean whistles$replaceArrival(Navigation navigation) {
		if (!navigation.announceArrival)
			return false;
		CarriageContraptionEntity entity = (CarriageContraptionEntity) (Object) this;
		return !TrainArrivalSound.tryReplaceArrival(entity);
	}
}
