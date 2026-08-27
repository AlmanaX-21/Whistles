package me.almana.whistles.net;

import java.util.function.Supplier;

import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.sound.AutomaticArrivalOrder;
import me.almana.whistles.sound.SoundIds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

public class SetTrainSoundPacket {

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

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get()
			.enqueueWork(() -> {
				ServerPlayer sender = context.get()
					.getSender();
				if (sender == null || !sender.level()
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
		context.get()
			.setPacketHandled(true);
	}
}
