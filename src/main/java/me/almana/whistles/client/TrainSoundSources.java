package me.almana.whistles.client;

import java.util.Map;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Carriage.DimensionalCarriageEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import me.almana.whistles.block.SoundMode;
import me.almana.whistles.block.TrainSoundPostBlock;
import me.almana.whistles.block.TrainSoundPostBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

public class TrainSoundSources {

	public record Source(CarriageContraptionEntity entity, BlockPos localPos, ResourceLocation sound) {

		public boolean stale() {
			return entity.isRemoved();
		}

		public Vec3 worldPosition() {
			return entity.toGlobalVector(Vec3.atCenterOf(localPos), 1);
		}
	}

	public static Source find(Train train, SoundMode mode, Level level) {
		for (Carriage carriage : train.carriages) {
			DimensionalCarriageEntity dimensional = carriage.getDimensionalIfPresent(level.dimension());
			if (dimensional == null)
				continue;
			CarriageContraptionEntity entity = dimensional.entity.get();
			if (entity == null)
				continue;
			for (Map.Entry<BlockPos, StructureBlockInfo> entry : entity.getContraption()
				.getBlocks()
				.entrySet()) {
				StructureBlockInfo info = entry.getValue();
				if (!(info.state()
					.getBlock() instanceof TrainSoundPostBlock))
					continue;
				if (info.state()
					.getValue(TrainSoundPostBlock.MODE) != mode)
					continue;
				return new Source(entity, entry.getKey(), soundOf(info, mode));
			}
		}
		return null;
	}

	private static ResourceLocation soundOf(StructureBlockInfo info, SoundMode mode) {
		if (info.nbt() != null && info.nbt()
			.contains("Sound"))
			return new ResourceLocation(info.nbt()
				.getString("Sound"));
		return mode == SoundMode.HORN ? TrainSoundPostBlockEntity.DEFAULT_HORN
			: TrainSoundPostBlockEntity.DEFAULT_WHISTLE;
	}
}
