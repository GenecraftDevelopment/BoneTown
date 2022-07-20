package com.chaosbuffalo.bonetown.network;

import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ITypedNetworkDeserializer<T, U> {

    void addNetworkDeserializer(U messageType, Function<FriendlyByteBuf, T> callback);

    @Nullable
    T deserialize(FriendlyByteBuf message);
}
