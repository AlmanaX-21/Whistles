package me.almana.whistles;

import me.almana.whistles.net.SetTrainSoundPacket;
import me.almana.whistles.net.SetTrainSoundSettingsPacket;
import me.almana.whistles.net.TrainArrivalSoundPacket;
import me.almana.whistles.net.TrainSoundPacket;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

public class AllPackets {

	private static final String VERSION = "5";

	public static void register(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(Whistles.ID)
			.versioned(VERSION);
		registrar.playToServer(SetTrainSoundPacket.TYPE, SetTrainSoundPacket.STREAM_CODEC,
			SetTrainSoundPacket::handleServer);
		registrar.playBidirectional(TrainSoundPacket.TYPE, TrainSoundPacket.STREAM_CODEC,
			new DirectionalPayloadHandler<>(TrainSoundPacket::handleClient, TrainSoundPacket::handleServer));
		registrar.playToClient(TrainArrivalSoundPacket.TYPE, TrainArrivalSoundPacket.STREAM_CODEC,
			TrainArrivalSoundPacket::handleClient);
		registrar.playToServer(SetTrainSoundSettingsPacket.TYPE, SetTrainSoundSettingsPacket.STREAM_CODEC,
			SetTrainSoundSettingsPacket::handleServer);
	}
}
