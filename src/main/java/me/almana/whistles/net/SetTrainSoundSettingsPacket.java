package me.almana.whistles.net;

import java.util.function.Supplier;

import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

public class SetTrainSoundSettingsPacket {

	final BlockPos pos;
	final TrainSoundSettings settings;

	public SetTrainSoundSettingsPacket(BlockPos pos, TrainSoundSettings settings) {
		this.pos = pos;
		this.settings = settings;
	}

	public SetTrainSoundSettingsPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		settings = TrainSoundSettings.read(buffer);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		settings.write(buffer);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get()
			.enqueueWork(() -> {
				ServerPlayer sender = context.get()
					.getSender();
				if (sender == null || !settings.valid() || !sender.level()
					.isLoaded(pos) || sender.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64)
					return;
				if (sender.level()
					.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
					be.setSettings(settings);
			});
		context.get()
			.setPacketHandled(true);
	}
}
