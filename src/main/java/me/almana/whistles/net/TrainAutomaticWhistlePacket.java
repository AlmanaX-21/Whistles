package me.almana.whistles.net;

import java.util.UUID;
import java.util.function.Supplier;

import me.almana.whistles.client.TrainSounds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class TrainAutomaticWhistlePacket {

	public final UUID trainId;
	public final int entityId;
	public final BlockPos localPos;
	public final ResourceLocation sound;
	public final boolean honking;
	public final TrainSoundSettings settings;

	public TrainAutomaticWhistlePacket(UUID trainId, int entityId, BlockPos localPos, ResourceLocation sound,
		boolean honking, TrainSoundSettings settings) {
		this.trainId = trainId;
		this.entityId = entityId;
		this.localPos = localPos;
		this.sound = sound;
		this.honking = honking;
		this.settings = settings;
	}

	public TrainAutomaticWhistlePacket(FriendlyByteBuf buffer) {
		trainId = buffer.readUUID();
		entityId = buffer.readVarInt();
		localPos = buffer.readBlockPos();
		sound = buffer.readResourceLocation();
		honking = buffer.readBoolean();
		settings = TrainSoundSettings.read(buffer);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(trainId);
		buffer.writeVarInt(entityId);
		buffer.writeBlockPos(localPos);
		buffer.writeResourceLocation(sound);
		buffer.writeBoolean(honking);
		settings.write(buffer);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		context.enqueueWork(() -> {
			if (settings.valid())
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> TrainSounds.receiveAutomatic(this));
		});
		context.setPacketHandled(true);
	}
}
