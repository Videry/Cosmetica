package com.eyezah.cosmetics.mixin;

import com.eyezah.cosmetics.Cosmetica;
import com.eyezah.cosmetics.CosmeticaSkinManager;
import com.eyezah.cosmetics.utils.Debug;
import io.netty.handler.codec.sctp.SctpOutboundByteStreamHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
	@Shadow @Final private Minecraft minecraft;

	@Inject(at = @At("RETURN"), method = "handleLogin")
	private void onHandleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
		String address = "fake server " + System.currentTimeMillis();
		if (Minecraft.getInstance().getCurrentServer() != null && !Objects.equals(Minecraft.getInstance().getCurrentServer().ip, Cosmetica.authServerHost + ":" + Cosmetica.authServerPort)) address = Minecraft.getInstance().getCurrentServer().ip;
		if (Cosmetica.currentServerAddressCache.isEmpty() || !Objects.equals(Cosmetica.currentServerAddressCache, address)) {
			Cosmetica.currentServerAddressCache = address;
			Debug.info("Clearing all player data due to login.");
			Cosmetica.clearAllCaches();
			Cosmetica.getPlayerData(this.minecraft.player);
		}
	}
}
