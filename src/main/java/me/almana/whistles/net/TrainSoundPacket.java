package me.almana.whistles.net;

import java.util.UUID;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.AllPackets;
import me.almana.whistles.client.TrainSoundSources;
import me.almana.whistles.client.TrainSounds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class TrainSoundPacket {

	public static final int MAX_SOURCES = 3;

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

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		context.enqueueWork(() -> {
			if (!isValidSourceIndex(sourceIndex))
				return;
			ServerPlayer sender = context.getSender();
			if (sender == null) {
				if (settings.valid())
					DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> TrainSounds.receive(this));
				return;
			}
			Train train = Create.RAILWAYS.sided(sender.level()).trains.get(trainId);
			if (train == null || !isDriving(sender, train))
				return;
			var sources = TrainSoundSources.find(train, sender.level());
			if (sourceIndex >= sources.size())
				return;
			TrainSoundPacket relayed = new TrainSoundPacket(trainId, sourceIndex, active, pitch,
				sources.get(sourceIndex)
					.settings());
			AllPackets.CHANNEL.send(PacketDistributor.ALL.noArg(), relayed);
		});
		context.setPacketHandled(true);
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
