package com.firesoftitan.play.titanbox.rfp.fakes;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;

import javax.annotation.Nullable;

public class FakeNetworkManager  extends NetworkManager {
    public FakeNetworkManager(EnumProtocolDirection enumprotocoldirection) {
        super(enumprotocoldirection);
        this.k = new FakeChannel();
        this.l = this.k.remoteAddress();
        this.preparing = false;
    }
    @Override
    public void stopReading() {

    }
    @Override
    public void sendPacket(Packet<?> packet) {

    }
    @Override
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {


    }

}
