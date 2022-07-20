package com.chaosbuffalo.bonetown.entity.animation_state.messages;


import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.FriendlyByteBuf;

public class LayerControlMessage extends AnimationMessage {
    private String stateName;
    private String layerName;

    public static final String START_LAYER = "START_LAYER";
    public static final String STOP_LAYER =  "STOP_LAYER";

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(
                START_LAYER, LayerControlMessage::fromPacketBufferStart
        );
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(
                STOP_LAYER, LayerControlMessage::fromPacketBufferStop
        );
        AnimationComponent.addMessageHandler(STOP_LAYER, LayerControlMessage::handleMessage);
        AnimationComponent.addMessageHandler(START_LAYER, LayerControlMessage::handleMessage);
    }

    public LayerControlMessage(String type, String stateName, String layerName) {
        super(type);
        this.stateName = stateName;
        this.layerName = layerName;
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        if (message instanceof LayerControlMessage){
            LayerControlMessage msg = (LayerControlMessage) message;
            switch (message.getType()){
                case START_LAYER:
                    component.startLayer(msg.getStateName(), msg.getLayerName());
                    break;
                case STOP_LAYER:
                    component.stopLayer(msg.getStateName(), msg.getLayerName());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void toPacketBuffer(FriendlyByteBuf buffer) {
        super.toPacketBuffer(buffer);
        buffer.writeUtf(getStateName());
        buffer.writeUtf(getLayerName());
    }

    private static LayerControlMessage fromPacketBufferStart(FriendlyByteBuf buffer){
        String stateName = buffer.readUtf();
        String layerName = buffer.readUtf();
        return getStartMessage(stateName, layerName);
    }

    private static LayerControlMessage fromPacketBufferStop(FriendlyByteBuf buffer){
        String stateName = buffer.readUtf();
        String layerName = buffer.readUtf();
        return getStopMessage(stateName, layerName);
    }

    public String getStateName() {
        return stateName;
    }

    public String getLayerName() {
        return layerName;
    }

    public static LayerControlMessage getStartMessage(String stateName, String layerName){
        return new LayerControlMessage(START_LAYER, stateName, layerName);
    }

    public static LayerControlMessage getStopMessage(String stateName, String layerName){
        return new LayerControlMessage(STOP_LAYER, stateName, layerName);
    }
}
