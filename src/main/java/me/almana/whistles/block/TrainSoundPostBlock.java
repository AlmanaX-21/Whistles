package me.almana.whistles.block;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import me.almana.whistles.client.TrainSoundSelectScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class TrainSoundPostBlock extends HorizontalDirectionalBlock implements IWrenchable, EntityBlock {

	public static final EnumProperty<SoundMode> MODE = EnumProperty.create("mode", SoundMode.class);
	public static final MapCodec<TrainSoundPostBlock> CODEC = simpleCodec(TrainSoundPostBlock::new);

	private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

	public TrainSoundPostBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH)
			.setValue(MODE, SoundMode.WHISTLE));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection()
			.getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrainSoundPostBlockEntity(pos, state);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!level.isClientSide) {
			SoundMode mode = state.getValue(MODE)
				.next();
			level.setBlockAndUpdate(pos, state.setValue(MODE, mode));
			if (level.getBlockEntity(pos) instanceof TrainSoundPostBlockEntity be)
				be.followDefaultFor(mode);
			IWrenchable.playRotateSound(level, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
		BlockHitResult hit) {
		if (level.isClientSide && FMLEnvironment.dist == Dist.CLIENT)
			TrainSoundSelectScreen.open(pos);
		return InteractionResult.SUCCESS;
	}
}
