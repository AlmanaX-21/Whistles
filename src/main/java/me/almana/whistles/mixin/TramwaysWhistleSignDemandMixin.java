package me.almana.whistles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.compat.TramwaysWhistle;

@Pseudo
@Mixin(targets = "purplecreate.tramways.content.signs.demands.forge.WhistleSignDemandImpl", remap = false)
public abstract class TramwaysWhistleSignDemandMixin {

	@Inject(method = "sendWhistlePacket", at = @At("HEAD"), cancellable = true)
	private static void whistles$playAutomaticSound(Train train, boolean honking, CallbackInfo ci) {
		if (TramwaysWhistle.play(train, honking))
			ci.cancel();
	}
}
