package me.almana.whistles;

import me.almana.whistles.net.SetTrainSoundPacket;
import me.almana.whistles.net.SetTrainSoundSettingsPacket;
import me.almana.whistles.net.TrainArrivalSoundPacket;
import me.almana.whistles.net.TrainSoundPacket;

import java.util.Optional;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class AllPackets {

	private static final String VERSION = "5";

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
		CHANNEL.registerMessage(2, TrainArrivalSoundPacket.class, TrainArrivalSoundPacket::write,
			TrainArrivalSoundPacket::new, TrainArrivalSoundPacket::handle,
			Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(3, SetTrainSoundSettingsPacket.class, SetTrainSoundSettingsPacket::write,
			SetTrainSoundSettingsPacket::new, SetTrainSoundSettingsPacket::handle,
			Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
}
