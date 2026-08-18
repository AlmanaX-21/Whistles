package me.almana.whistles.block;

import java.util.List;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import me.almana.whistles.AllBlockEntities;
import me.almana.whistles.Whistles;
import me.almana.whistles.sound.SoundIds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

	private ResourceLocation sound;

	public TrainSoundPostBlockEntity(BlockPos pos, BlockState state) {
		super(AllBlockEntities.TRAIN_SOUND_POST.get(), pos, state);
		sound = state.getValue(TrainSoundPostBlock.MODE) == SoundMode.HORN ? DEFAULT_HORN : DEFAULT_WHISTLE;
	}

	public ResourceLocation getSound() {
		return sound;
	}

	public void setSound(ResourceLocation sound) {
		this.sound = sound;
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
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Sound"))
			sound = new ResourceLocation(tag.getString("Sound"));
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
