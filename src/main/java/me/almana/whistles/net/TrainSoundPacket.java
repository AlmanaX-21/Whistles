package me.almana.whistles.net;

import java.util.UUID;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.AllPackets;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.client.TrainSounds;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class TrainSoundPacket {

	public final UUID trainId;
	public final SoundMode mode;
	public final boolean active;
	public final byte pitch;

	public TrainSoundPacket(UUID trainId, SoundMode mode, boolean active, byte pitch) {
		this.trainId = trainId;
		this.mode = mode;
		this.active = active;
		this.pitch = pitch;
	}

	public TrainSoundPacket(FriendlyByteBuf buffer) {
		trainId = buffer.readUUID();
		mode = buffer.readEnum(SoundMode.class);
		active = buffer.readBoolean();
		pitch = buffer.readByte();
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(trainId);
		buffer.writeEnum(mode);
		buffer.writeBoolean(active);
		buffer.writeByte(pitch);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		context.enqueueWork(() -> {
			ServerPlayer sender = context.getSender();
			if (sender == null) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> TrainSounds.receive(this));
				return;
			}
			Train train = Create.RAILWAYS.sided(sender.level()).trains.get(trainId);
			if (train == null || !isDriving(sender, train))
				return;
			AllPackets.CHANNEL.send(PacketDistributor.ALL.noArg(), this);
		});
		context.setPacketHandled(true);
	}

	private static boolean isDriving(ServerPlayer sender, Train train) {
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
