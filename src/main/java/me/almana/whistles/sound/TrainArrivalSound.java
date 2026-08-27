package me.almana.whistles.sound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.AllPackets;
import me.almana.whistles.Whistles;
import me.almana.whistles.client.TrainSoundSources;
import me.almana.whistles.client.TrainSoundSources.Source;
import me.almana.whistles.net.TrainArrivalSoundPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Whistles.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrainArrivalSound {

	private static final Map<MinecraftServer, Set<UUID>> NORMALIZED_TRAINS = new WeakHashMap<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void tick(LevelTickEvent event) {
		if (event.side != LogicalSide.SERVER || event.phase != Phase.START
			|| event.level.dimension() != Level.OVERWORLD)
			return;
		MinecraftServer server = ((ServerLevel) event.level).getServer();
		var trains = Create.RAILWAYS.sided(event.level).trains;
		Set<UUID> normalizedTrains = NORMALIZED_TRAINS.computeIfAbsent(server, ignored -> new HashSet<>());
		normalizedTrains.retainAll(trains.keySet());
		for (Train train : trains.values()) {
			if (!normalizedTrains.contains(train.id) && normalizeAssembledTrain(train, server))
				normalizedTrains.add(train.id);
		}
	}

	private static boolean normalizeAssembledTrain(Train train, MinecraftServer server) {
		if (!allCarriagesAvailable(train, server))
			return false;
		List<Source> sources = new ArrayList<>();
		for (ServerLevel level : server.getAllLevels())
			sources.addAll(TrainSoundSources.findAll(train, level));
		normalizeSelectedSource(sources);
		if (!sources.isEmpty())
			Create.RAILWAYS.markTracksDirty();
		return true;
	}

	private static boolean allCarriagesAvailable(Train train, MinecraftServer server) {
		for (Carriage carriage : train.carriages) {
			boolean available = false;
			for (ServerLevel level : server.getAllLevels()) {
				DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(level.dimension());
				if (dimensional != null && dimensional.entity.get() != null) {
					available = true;
					break;
				}
			}
			if (!available)
				return false;
		}
		return true;
	}

	public static boolean tryReplaceArrival(CarriageContraptionEntity entity) {
		if (entity.level().isClientSide)
			return false;
		Train train = Create.RAILWAYS.sided(entity.level()).trains.get(entity.trainId);
		if (train == null)
			return false;
		boolean leadCarriage = entity.carriageIndex == (train.speed < 0 ? train.carriages.size() - 1 : 0);
		if (!shouldReplace(train.navigation.announceArrival, train.navigation.distanceToDestination, leadCarriage, true))
			return false;
		ServerLevel level = (ServerLevel) entity.level();
		List<Source> sources = TrainSoundSources.findAll(train, level);
		normalizeSelectedSource(sources);
		int sourceIndex = preferredSourceIndex(sources);
		if (sourceIndex < 0)
			return false;

		Source source = sources.get(sourceIndex);
		train.navigation.announceArrival = false;
		AllPackets.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(source::entity),
			new TrainArrivalSoundPacket(source.entity().getId(), source.localPos(), source.sound(), source.settings()));
		return true;
	}

	static boolean shouldReplace(boolean announced, double distance, boolean leadCarriage, boolean hasSource) {
		return announced && Math.abs(distance) < 60 && leadCarriage && hasSource;
	}

	public static int preferredSourceIndex(List<Source> sources) {
		int selected = selectedSourceIndex(sources);
		if (selected >= 0)
			return selected;
		return sources.isEmpty() ? -1 : 0;
	}

	public static int normalizeSelectedSource(List<Source> sources) {
		int selected = selectedSourceIndex(sources);
		applySelectedSource(sources, selected);
		return selected;
	}

	private static int selectedSourceIndex(List<Source> sources) {
		int selected = -1;
		long selectedOrder = Long.MIN_VALUE;
		for (int i = 0; i < sources.size(); i++) {
			Source source = sources.get(i);
			if (source.automaticArrival() && source.automaticArrivalOrder() > selectedOrder) {
				selected = i;
				selectedOrder = source.automaticArrivalOrder();
			}
		}
		return selected;
	}

	private static void applySelectedSource(List<Source> sources, int selected) {
		SourceKey selectedKey = selected >= 0 ? SourceKey.of(sources.get(selected)) : null;
		long selectedOrder = selected >= 0 ? sources.get(selected)
			.automaticArrivalOrder() : 0;
		for (Source source : sources)
			source.setAutomaticArrival(selectedKey != null && selectedKey.equals(SourceKey.of(source)), selectedOrder);
	}

	private record SourceKey(int carriageIndex, BlockPos localPos) {

		private static SourceKey of(Source source) {
			return new SourceKey(source.carriageIndex(), source.localPos());
		}
	}
}
