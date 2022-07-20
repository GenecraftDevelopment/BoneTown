package com.chaosbuffalo.bonetown.network;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class StringTypeNetworkDeserializer<T> implements ITypedNetworkDeserializer<T, String>  {

    private final HashMap<String, Function<FriendlyByteBuf, T>> deserializers;

    public StringTypeNetworkDeserializer(){
        deserializers = new HashMap<>();
    }

    @Override
    public void addNetworkDeserializer(String messageType, Function<FriendlyByteBuf, T> callback) {
        deserializers.put(messageType, callback);
    }

    @Nullable
    @Override
    public T deserialize(FriendlyByteBuf message) {
        String type = message.readUtf();
        if (deserializers.containsKey(type)){
            return deserializers.get(type).apply(message);
        } else {
            BoneTown.LOGGER.error("Failed to find deserializer for type: {}", type);
            return null;
        }
    }
}
