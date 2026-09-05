package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GLX;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderLinkHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ShaderLinkHelper staticShaderLinkHelper;

    public static void setNewStaticShaderLinkHelper() {
        staticShaderLinkHelper = new ShaderLinkHelper();
    }

    public static ShaderLinkHelper getStaticShaderLinkHelper() {
        return staticShaderLinkHelper;
    }

    public void deleteShader(IShaderManager manager) {
        manager.getFragmentShaderLoader().detachShader();
        manager.getVertexShaderLoader().detachShader();
        GLX.glDeleteProgram(manager.getProgram());
    }

}
