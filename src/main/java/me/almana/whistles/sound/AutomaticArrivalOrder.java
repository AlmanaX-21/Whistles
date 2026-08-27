package me.almana.whistles.sound;

import me.almana.whistles.Whistles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class AutomaticArrivalOrder extends SavedData {

	private static final String DATA_NAME = Whistles.ID + "_automatic_arrival_order";
	private static final String LAST_ORDER = "LastOrder";

	private long lastOrder;

	public static AutomaticArrivalOrder get(MinecraftServer server) {
		return server.overworld()
			.getDataStorage()
			.computeIfAbsent(AutomaticArrivalOrder::load, AutomaticArrivalOrder::new, DATA_NAME);
	}

	static AutomaticArrivalOrder load(CompoundTag tag) {
		AutomaticArrivalOrder order = new AutomaticArrivalOrder();
		order.lastOrder = tag.getLong(LAST_ORDER);
		return order;
	}

	public long next() {
		lastOrder++;
		setDirty();
		return lastOrder;
	}

	public void observe(long order) {
		if (order <= lastOrder)
			return;
		lastOrder = order;
		setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		tag.putLong(LAST_ORDER, lastOrder);
		return tag;
	}
}
