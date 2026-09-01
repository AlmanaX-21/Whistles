package me.almana.whistles.compat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.AllPackets;
import me.almana.whistles.client.TrainSoundSources;
import me.almana.whistles.client.TrainSoundSources.Source;
import me.almana.whistles.net.TrainAutomaticWhistlePacket;
import me.almana.whistles.sound.TrainArrivalSound;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TramwaysWhistle {
	private static final Comparator<Source> SOURCE_ORDER = Comparator.comparingInt(Source::carriageIndex)
		.thenComparingInt(source -> source.localPos().getX())
		.thenComparingInt(source -> source.localPos().getY())
		.thenComparingInt(source -> source.localPos().getZ());

	public static boolean play(Train train, boolean honking) {
		List<Source> sources = findSources(train);
		int sourceIndex = sourceIndex(sources);
		if (sourceIndex < 0)
			return false;

		Source source = sources.get(sourceIndex);
		AllPackets.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(source::entity), new TrainAutomaticWhistlePacket(train.id,
			source.entity().getId(), source.localPos(), source.sound(), honking, source.settings()));
		return true;
	}

	static int sourceIndex(List<Source> sources) {
		return TrainArrivalSound.preferredSourceIndex(sources);
	}

	private static List<Source> findSources(Train train) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		List<List<Source>> sourceGroups = new ArrayList<>();
		for (ServerLevel level : server.getAllLevels())
			sourceGroups.add(TrainSoundSources.findAll(train, level));
		return combineSources(sourceGroups);
	}

	static List<Source> combineSources(List<List<Source>> sourceGroups) {
		return sourceGroups.stream()
			.flatMap(List::stream)
			.sorted(SOURCE_ORDER)
			.toList();
	}
}
