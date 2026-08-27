package me.almana.whistles.net;

import me.almana.whistles.Whistles;
import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.sound.AutomaticArrivalOrder;
import me.almana.whistles.sound.SoundIds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetTrainSoundPacket implements CustomPacketPayload {

	public static final Type<SetTrainSoundPacket> TYPE = new Type<>(Whistles.asResource("set_train_sound"));
	public static final StreamCodec<FriendlyByteBuf, SetTrainSoundPacket> STREAM_CODEC =
		StreamCodec.ofMember(SetTrainSoundPacket::write, SetTrainSoundPacket::new);

	private final BlockPos pos;
	private final ResourceLocation sound;
	private final boolean automaticArrival;

	public SetTrainSoundPacket(BlockPos pos, ResourceLocation sound, boolean automaticArrival) {
		this.pos = pos;
		this.sound = sound;
		this.automaticArrival = automaticArrival;
	}

	public SetTrainSoundPacket(FriendlyByteBuf buffer) {
		pos = buffer.readBlockPos();
		sound = buffer.readResourceLocation();
		automaticArrival = buffer.readBoolean();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeResourceLocation(sound);
		buffer.writeBoolean(automaticArrival);
	}

	public void handleServer(IPayloadContext context) {
		context.enqueueWork(() -> {
				ServerPlayer sender = (ServerPlayer) context.player();
				if (!sender.level()
					.isLoaded(pos) || sender.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64)
					return;
				if (sender.level()
					.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be) {
					boolean changed = be.isAutomaticArrival() != automaticArrival;
					be.setSound(sound);
					if (changed) {
						long order = automaticArrival ? AutomaticArrivalOrder.get(sender.getServer())
							.next() : 0;
						be.setAutomaticArrival(automaticArrival, order);
					}
					if (changed) {
						String name = SoundIds.displayName(sound.getPath());
						sender.displayClientMessage(Component.translatable(automaticArrival
							? "whistles.message.arrival_sound_set"
							: "whistles.message.arrival_sound_cleared", name), false);
					}
				}
			});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
