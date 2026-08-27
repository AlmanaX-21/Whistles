package me.almana.whistles.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

class AutomaticArrivalOrderTest {

	@Test
	void keepsSelectionOrderUniqueAcrossTicksAndReloads() {
		AutomaticArrivalOrder order = new AutomaticArrivalOrder();

		assertEquals(1, order.next());
		assertEquals(2, order.next());
		order.observe(12);
		assertEquals(13, order.next());

		CompoundTag tag = order.save(new CompoundTag(), RegistryAccess.EMPTY);
		AutomaticArrivalOrder loaded = AutomaticArrivalOrder.load(tag, RegistryAccess.EMPTY);
		assertEquals(14, loaded.next());
	}
}
