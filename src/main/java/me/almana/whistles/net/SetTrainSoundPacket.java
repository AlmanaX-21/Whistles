package me.almana.whistles.net;

import java.util.function.Supplier;

import me.almana.whistles.block.TrainSoundPostBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

public class SetTrainSoundPacket {

	private final BlockPos pos;
	private final ResourceLocation sound;

	public SetTrainSoundPacket(BlockPos pos, ResourceLocation sound) {
		this.pos = pos;
		this.sound = sound;
	}

	public SetTrainSoundPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		sound = buffer.readResourceLocation();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeResourceLocation(sound);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get()
			.enqueueWork(() -> {
				ServerPlayer sender = context.get()
					.getSender();
				if (sender == null || !sender.level()
					.isLoaded(pos) || sender.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64)
					return;
				if (sender.level()
					.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
					be.setSound(sound);
			});
		context.get()
			.setPacketHandled(true);
	}
}
