package me.almana.whistles.net;

import me.almana.whistles.Whistles;
import me.almana.whistles.client.TrainSounds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TrainArrivalSoundPacket implements CustomPacketPayload {

	public static final Type<TrainArrivalSoundPacket> TYPE =
		new Type<>(Whistles.asResource("train_arrival_sound"));
	public static final StreamCodec<FriendlyByteBuf, TrainArrivalSoundPacket> STREAM_CODEC =
		StreamCodec.ofMember(TrainArrivalSoundPacket::write, TrainArrivalSoundPacket::new);

	public final int entityId;
	public final BlockPos localPos;
	public final ResourceLocation sound;
	public final TrainSoundSettings settings;

	public TrainArrivalSoundPacket(int entityId, BlockPos localPos, ResourceLocation sound,
		TrainSoundSettings settings) {
		this.entityId = entityId;
		this.localPos = localPos;
		this.sound = sound;
		this.settings = settings;
	}

	public TrainArrivalSoundPacket(FriendlyByteBuf buffer) {
		entityId = buffer.readVarInt();
		localPos = buffer.readBlockPos();
		sound = buffer.readResourceLocation();
		settings = TrainSoundSettings.read(buffer);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(entityId);
		buffer.writeBlockPos(localPos);
		buffer.writeResourceLocation(sound);
		settings.write(buffer);
	}

	public void handleClient(IPayloadContext context) {
		context.enqueueWork(() -> {
			if (FMLEnvironment.dist == Dist.CLIENT)
				TrainSounds.playArrival(this);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
