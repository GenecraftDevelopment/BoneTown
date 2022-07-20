package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.core.model.BakedAnimatedMesh;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;

import java.util.HashMap;
import java.util.Map;

public class BTAnimatedModelRenderData extends BTModelRenderData {

    private BTAnimatedModel animatedModel;

    public BTAnimatedModelRenderData(BTAnimatedModel modelIn, boolean doCombined) {
        super(modelIn, doCombined);
        this.animatedModel = modelIn;
    }

    @Override
    public Map<String, IBTRenderData> getRenderData() {
        HashMap<String, IBTRenderData> ret = new HashMap<>();
        if (doCombined){
            BakedAnimatedMesh mesh = animatedModel.getCombinedAnimatedMesh();
            ret.put(mesh.name, new BTAnimatedMeshRenderData(mesh));
        } else {
            for (BakedAnimatedMesh mesh : animatedModel.getAnimatedMeshes()){
                ret.put(mesh.name, new BTAnimatedMeshRenderData(mesh));
            }
        }
        return ret;
    }
}
