package com.chaosbuffalo.bonetown.core.bonemf;


import java.lang.ref.WeakReference;

public class BoneMFAttribute {
    private final WeakReference<BoneMFNode> owner;

    public enum AttributeTypes {
        NULL,
        MESH,
        SKELETON
    }

    public static AttributeTypes getAttributeTypeFromString(String attrType){
        return switch (attrType) {
            case "mesh" -> AttributeTypes.MESH;
            case "skeleton" -> AttributeTypes.SKELETON;
            default -> AttributeTypes.NULL;
        };
    }

    public static String getAttributeNameFromType(AttributeTypes type){
        return switch (type) {
            case MESH -> "mesh";
            case SKELETON -> "skeleton";
            default -> "null";
        };
    }

    private final AttributeTypes type;

    public AttributeTypes getType() {
        return type;
    }

    public BoneMFNode getOwner() {
        return owner.get();
    }

    @Override
    public String toString() {
        return String.format("<BoneMFAttribute type='%s'>", getAttributeNameFromType(type));
    }

    public BoneMFAttribute(AttributeTypes type, BoneMFNode owner){
        this.type = type;
        this.owner = new WeakReference<>(owner);
    }

}
