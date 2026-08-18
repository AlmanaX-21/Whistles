package me.almana.whistles;

import me.almana.whistles.net.SetTrainSoundPacket;
import me.almana.whistles.net.TrainSoundPacket;

import java.util.Optional;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AllPackets {

	private static final String VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
		.named(Whistles.asResource("main"))
		.networkProtocolVersion(() -> VERSION)
		.clientAcceptedVersions(VERSION::equals)
		.serverAcceptedVersions(VERSION::equals)
		.simpleChannel();

	public static void register() {
		CHANNEL.registerMessage(0, SetTrainSoundPacket.class, SetTrainSoundPacket::write, SetTrainSoundPacket::new,
			SetTrainSoundPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		CHANNEL.registerMessage(1, TrainSoundPacket.class, TrainSoundPacket::write, TrainSoundPacket::new,
			TrainSoundPacket::handle, Optional.empty());
	}
}
