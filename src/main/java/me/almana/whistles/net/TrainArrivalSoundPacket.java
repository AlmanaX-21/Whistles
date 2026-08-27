package me.almana.whistles.net;

import java.util.function.Supplier;

import me.almana.whistles.client.TrainSounds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class TrainArrivalSoundPacket {

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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> TrainSounds.playArrival(this)));
		context.setPacketHandled(true);
	}
}
