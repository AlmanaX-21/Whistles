package me.almana.whistles.block;

import java.util.List;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import me.almana.whistles.AllBlockEntities;
import me.almana.whistles.Whistles;
import me.almana.whistles.sound.AutomaticArrivalOrder;
import me.almana.whistles.sound.SoundIds;
import me.almana.whistles.sound.TrainSoundSettings;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrainSoundPostBlockEntity extends BlockEntity implements IHaveGoggleInformation {

	public static final ResourceLocation DEFAULT_WHISTLE = Whistles.asResource("train_sound/steam_whistle");
	public static final ResourceLocation DEFAULT_HORN = Whistles.asResource("train_sound/steam_horn");
	public static final String AUTOMATIC_ARRIVAL = "AutomaticArrival";
	public static final String AUTOMATIC_ARRIVAL_ORDER = "AutomaticArrivalOrder";

	private ResourceLocation sound;
	private TrainSoundSettings settings;
	private boolean automaticArrival;
	private long automaticArrivalOrder;
	private boolean migrateSettings;

	public TrainSoundPostBlockEntity(BlockPos pos, BlockState state) {
		super(AllBlockEntities.TRAIN_SOUND_POST.get(), pos, state);
		sound = state.getValue(TrainSoundPostBlock.MODE) == SoundMode.HORN ? DEFAULT_HORN : DEFAULT_WHISTLE;
		settings = TrainSoundSettings.fromConfig();
	}

	public ResourceLocation getSound() {
		return sound;
	}

	public void setSound(ResourceLocation sound) {
		this.sound = sound;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	public TrainSoundSettings getSettings() {
		return settings;
	}

	public void setSettings(TrainSoundSettings settings) {
		this.settings = settings;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	public boolean isAutomaticArrival() {
		return automaticArrival;
	}

	public void setAutomaticArrival(boolean automaticArrival, long automaticArrivalOrder) {
		if (this.automaticArrival == automaticArrival)
			return;
		this.automaticArrival = automaticArrival;
		this.automaticArrivalOrder = automaticArrival ? automaticArrivalOrder : 0;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SoundMode mode = getBlockState().getValue(TrainSoundPostBlock.MODE);
		tooltip.add(Component.literal("    ")
			.append(Component.translatable("whistles.goggles." + mode.getSerializedName()))
			.withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal("     ")
			.append(SoundIds.displayName(sound.getPath()))
			.withStyle(ChatFormatting.AQUA));
		return true;
	}

	/** Keeps an untouched post on its mode's default instead of stranding a whistle sound on a horn. */
	public void followDefaultFor(SoundMode mode) {
		if (mode == SoundMode.HORN && sound.equals(DEFAULT_WHISTLE))
			setSound(DEFAULT_HORN);
		else if (mode == SoundMode.WHISTLE && sound.equals(DEFAULT_HORN))
			setSound(DEFAULT_WHISTLE);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putString("Sound", sound.toString());
		tag.put("Settings", settings.write());
		tag.putBoolean(AUTOMATIC_ARRIVAL, automaticArrival);
		tag.putLong(AUTOMATIC_ARRIVAL_ORDER, automaticArrivalOrder);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Sound"))
			sound = new ResourceLocation(tag.getString("Sound"));
		automaticArrival = tag.getBoolean(AUTOMATIC_ARRIVAL);
		automaticArrivalOrder = automaticArrival ? tag.getLong(AUTOMATIC_ARRIVAL_ORDER) : 0;
		if (tag.contains("Settings", Tag.TAG_COMPOUND)) {
			TrainSoundSettings loaded = TrainSoundSettings.read(tag.getCompound("Settings"));
			if (loaded.valid()) {
				settings = loaded;
				migrateSettings = false;
				return;
			}
		}
		settings = TrainSoundSettings.fromConfig();
		migrateSettings = true;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide && automaticArrival)
			AutomaticArrivalOrder.get(level.getServer())
				.observe(automaticArrivalOrder);
		if (migrateSettings && !level.isClientSide) {
			migrateSettings = false;
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
