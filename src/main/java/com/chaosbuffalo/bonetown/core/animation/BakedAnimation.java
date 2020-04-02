package com.chaosbuffalo.bonetown.core.animation;


import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BakedAnimation {

    private List<IPose> frames;

    private ResourceLocation name;

    private double ticksPerSecond;

    private int frameCount;

    public class InterpolationFramesReturn {

        public IPose current;
        public IPose next;
        public float partialTick;

        public InterpolationFramesReturn(IPose current,
                                         IPose next,
                                         float partialTick){
            this.current = current;
            this.next = next;
            this.partialTick = partialTick;
        }
    }

    public BakedAnimation(ResourceLocation name, List<IPose> frames, double ticksPerSecond) {
        this.name = name;
        this.frames = frames;
        this.frameCount = frames.size();
        this.ticksPerSecond = ticksPerSecond;
    }


    public double getDuration() {
        return (double) getFrameCount() / getTicksPerSecond();
    }

    public List<IPose> getFrames() {
        return frames;
    }


    public IPose getFrame(int index){
        return this.frames.get(index);
    }

    public int getFrameCount(){
        return frameCount;
    }

    public double getTicksPerSecond(){
        return ticksPerSecond;
    }

    public ResourceLocation getName(){
        return name;
    }


    public InterpolationFramesReturn getInterpolationFrames(int ticks,
                                                            boolean doLoop,
                                                            float partialTicks){
        double time = (ticks + partialTicks) * (1.0/20.0);
        double animTicks = time * getTicksPerSecond();
        double leftover = animTicks - Math.floor(animTicks);
        int currentFrame = (int) Math.floor(animTicks);
        int totalFrames = getFrameCount();
        int nextFrame = currentFrame + 1;
        if (doLoop){
            currentFrame = currentFrame % totalFrames;
            nextFrame = nextFrame % totalFrames;
        } else {
            currentFrame = Math.min(currentFrame, totalFrames);
            nextFrame = Math.min(nextFrame, totalFrames);
        }
        return new InterpolationFramesReturn(getFrame(currentFrame), getFrame(nextFrame), (float) leftover);

    }


}