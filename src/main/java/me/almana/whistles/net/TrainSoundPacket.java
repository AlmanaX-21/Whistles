package me.almana.whistles.net;

import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.Whistles;
import me.almana.whistles.client.TrainSoundSources;
import me.almana.whistles.client.TrainSounds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TrainSoundPacket implements CustomPacketPayload {

	public static final int MAX_SOURCES = 3;
	public static final Type<TrainSoundPacket> TYPE =
		new Type<>(Whistles.asResource("train_sound"));
	public static final StreamCodec<FriendlyByteBuf, TrainSoundPacket> STREAM_CODEC =
		StreamCodec.ofMember(TrainSoundPacket::write, TrainSoundPacket::new);

	public final UUID trainId;
	public final int sourceIndex;
	public final boolean active;
	public final byte pitch;
	public final TrainSoundSettings settings;

	public TrainSoundPacket(UUID trainId, int sourceIndex, boolean active, byte pitch, TrainSoundSettings settings) {
		this.trainId = trainId;
		this.sourceIndex = sourceIndex;
		this.active = active;
		this.pitch = pitch;
		this.settings = settings;
	}

	public TrainSoundPacket(FriendlyByteBuf buffer) {
		trainId = buffer.readUUID();
		sourceIndex = buffer.readUnsignedByte();
		active = buffer.readBoolean();
		pitch = buffer.readByte();
		settings = TrainSoundSettings.read(buffer);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(trainId);
		buffer.writeByte(sourceIndex);
		buffer.writeBoolean(active);
		buffer.writeByte(pitch);
		settings.write(buffer);
	}

	public void handleClient(IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!isValidSourceIndex(sourceIndex))
				return;
			if (settings.valid() && FMLEnvironment.dist == Dist.CLIENT)
				TrainSounds.receive(this);
		});
	}

	public void handleServer(IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!isValidSourceIndex(sourceIndex))
				return;
			ServerPlayer sender = (ServerPlayer) context.player();
			Train train = Create.RAILWAYS.sided(sender.level()).trains.get(trainId);
			if (train == null || !isDriving(sender, train))
				return;
			var sources = TrainSoundSources.find(train, sender.level());
			if (sourceIndex >= sources.size())
				return;
			TrainSoundPacket relayed = new TrainSoundPacket(trainId, sourceIndex, active, pitch,
				sources.get(sourceIndex)
					.settings());
			PacketDistributor.sendToAllPlayers(relayed);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static boolean isValidSourceIndex(int sourceIndex) {
		return sourceIndex >= 0 && sourceIndex < MAX_SOURCES;
	}

	static boolean isDriving(ServerPlayer sender, Train train) {
		for (Carriage carriage : train.carriages) {
			DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(sender.level()
				.dimension());
			if (dimensional == null)
				continue;
			CarriageContraptionEntity entity = dimensional.entity.get();
			if (entity != null && entity.getControllingPlayer()
				.map(sender.getUUID()::equals)
				.orElse(false))
				return true;
		}
		return false;
	}
}
