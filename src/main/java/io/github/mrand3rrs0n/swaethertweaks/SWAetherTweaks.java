package io.github.mrand3rrs0n.swaethertweaks;

import io.github.mrand3rrs0n.swaethertweaks.network.serverbound.CreativeAccessorySlotPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(SWAetherTweaks.MOD_ID)
public class SWAetherTweaks {
    public static final String MOD_ID = "swaethertweaks";

    public SWAetherTweaks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPackets);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(CreativeAccessorySlotPacket.TYPE, CreativeAccessorySlotPacket.STREAM_CODEC, CreativeAccessorySlotPacket::execute);
    }
}
