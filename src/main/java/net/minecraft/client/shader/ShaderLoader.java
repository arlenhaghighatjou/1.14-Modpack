package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShaderLoader {
    private final ShaderLoader.ShaderType shaderType;
    private final String shaderFilename;
    private final int shader;
    private int shaderAttachCount;

    private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
        this.shaderType = type;
        this.shader = shaderId;
        this.shaderFilename = filename;
    }

    public void attachShader(IShaderManager manager) {

    }

    public void detachShader() {

    }

    public String getShaderFilename() {
        return this.shaderFilename;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum ShaderType {
        VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

        private final String shaderName;
        private final String shaderExtension;
        private final int shaderMode;
        private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

        private ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn) {
            this.shaderName = shaderNameIn;
            this.shaderExtension = shaderExtensionIn;
            this.shaderMode = shaderModeIn;
        }

        public String getShaderName() {
            return this.shaderName;
        }

        public String getShaderExtension() {
            return this.shaderExtension;
        }

        private int getShaderMode() {
            return this.shaderMode;
        }

        public Map<String, ShaderLoader> getLoadedShaders() {
            return this.loadedShaders;
        }
    }
}
