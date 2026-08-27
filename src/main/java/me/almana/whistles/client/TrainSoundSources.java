package me.almana.whistles.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.block.TrainSoundPostBlock;
import me.almana.whistles.block.TrainSoundPostBlockEntity;
import me.almana.whistles.net.TrainSoundPacket;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

public class TrainSoundSources {

	private static final Comparator<BlockPos> POSITION_ORDER = Comparator.comparingInt((BlockPos pos) -> pos.getX())
		.thenComparingInt(pos -> pos.getY())
		.thenComparingInt(pos -> pos.getZ());

	public record Source(int carriageIndex, CarriageContraptionEntity entity, BlockPos localPos,
		ResourceLocation sound, SoundMode mode, TrainSoundSettings settings, CompoundTag data) {

		public boolean automaticArrival() {
			return data.getBoolean(TrainSoundPostBlockEntity.AUTOMATIC_ARRIVAL);
		}

		public long automaticArrivalOrder() {
			return data.getLong(TrainSoundPostBlockEntity.AUTOMATIC_ARRIVAL_ORDER);
		}

		public void setAutomaticArrival(boolean automaticArrival) {
			setAutomaticArrival(automaticArrival, automaticArrivalOrder());
		}

		public void setAutomaticArrival(boolean automaticArrival, long automaticArrivalOrder) {
			data.putBoolean(TrainSoundPostBlockEntity.AUTOMATIC_ARRIVAL, automaticArrival);
			if (automaticArrival)
				data.putLong(TrainSoundPostBlockEntity.AUTOMATIC_ARRIVAL_ORDER, automaticArrivalOrder);
			else
				data.remove(TrainSoundPostBlockEntity.AUTOMATIC_ARRIVAL_ORDER);
		}

		public boolean stale() {
			return entity.isRemoved();
		}

		public Vec3 worldPosition() {
			return entity.toGlobalVector(Vec3.atCenterOf(localPos), 1);
		}
	}

	public static List<Source> find(Train train, Level level) {
		return find(train, level, TrainSoundPacket.MAX_SOURCES);
	}

	public static List<Source> findAll(Train train, Level level) {
		return find(train, level, Integer.MAX_VALUE);
	}

	private static List<Source> find(Train train, Level level, int limit) {
		List<Source> sources = new ArrayList<>(Math.min(limit, TrainSoundPacket.MAX_SOURCES));
		for (int carriageIndex = 0; carriageIndex < train.carriages.size(); carriageIndex++) {
			Carriage carriage = train.carriages.get(carriageIndex);
			DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(level.dimension());
			if (dimensional == null)
				continue;
			CarriageContraptionEntity entity = dimensional.entity.get();
			if (entity == null)
				continue;
			Map<BlockPos, StructureBlockInfo> blocks = entity.getContraption()
				.getBlocks();
			Collection<BlockPos> postPositions = blocks.entrySet()
				.stream()
				.filter(entry -> entry.getValue()
					.state()
					.getBlock() instanceof TrainSoundPostBlock)
				.map(Map.Entry::getKey)
				.toList();
			int remaining = limit - sources.size();
			for (BlockPos pos : orderPositions(postPositions, remaining)) {
				StructureBlockInfo info = blocks.get(pos);
				SoundMode mode = info.state()
					.getValue(TrainSoundPostBlock.MODE);
				sources.add(new Source(carriageIndex, entity, pos, soundOf(info, mode), mode,
					settingsOf(info, !level.isClientSide), info.nbt()));
			}
			if (sources.size() == limit)
				break;
		}
		return sources;
	}

	static List<BlockPos> orderPositions(Collection<BlockPos> positions, int limit) {
		return positions.stream()
			.sorted(POSITION_ORDER)
			.limit(limit)
			.toList();
	}

	private static ResourceLocation soundOf(StructureBlockInfo info, SoundMode mode) {
		if (info.nbt() != null && info.nbt()
			.contains("Sound"))
			return ResourceLocation.parse(info.nbt()
				.getString("Sound"));
		return mode == SoundMode.HORN ? TrainSoundPostBlockEntity.DEFAULT_HORN
			: TrainSoundPostBlockEntity.DEFAULT_WHISTLE;
	}

	static TrainSoundSettings settingsOf(StructureBlockInfo info, boolean migrateLegacy) {
		if (info.nbt() != null && info.nbt()
			.contains("Settings", Tag.TAG_COMPOUND)) {
			TrainSoundSettings settings = TrainSoundSettings.read(info.nbt()
				.getCompound("Settings"));
			if (settings.valid())
				return settings;
		}

		TrainSoundSettings settings = TrainSoundSettings.fromConfig();
		if (migrateLegacy && info.nbt() != null)
			info.nbt()
				.put("Settings", settings.write());
		return settings;
	}
}
