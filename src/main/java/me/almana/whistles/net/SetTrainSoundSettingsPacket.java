package me.almana.whistles.net;

import me.almana.whistles.Whistles;
import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetTrainSoundSettingsPacket implements CustomPacketPayload {

	public static final Type<SetTrainSoundSettingsPacket> TYPE =
		new Type<>(Whistles.asResource("set_train_sound_settings"));
	public static final StreamCodec<FriendlyByteBuf, SetTrainSoundSettingsPacket> STREAM_CODEC =
		StreamCodec.ofMember(SetTrainSoundSettingsPacket::write, SetTrainSoundSettingsPacket::new);

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

	public void handleServer(IPayloadContext context) {
		context.enqueueWork(() -> {
				ServerPlayer sender = (ServerPlayer) context.player();
				if (!settings.valid() || !sender.level()
					.isLoaded(pos) || sender.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64)
					return;
				if (sender.level()
					.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
					be.setSettings(settings);
			});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
