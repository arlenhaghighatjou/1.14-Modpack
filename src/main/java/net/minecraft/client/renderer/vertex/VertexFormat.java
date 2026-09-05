package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<VertexFormatElement> elements = Lists.newArrayList();
   private final List<Integer> offsets = Lists.newArrayList();
   private int vertexSize;
   private int colorElementOffset = -1;
   private final List<Integer> uvOffsetsById = Lists.newArrayList();
   private int normalElementOffset = -1;
   private int eaglerAttribBits = -1;
   private boolean eaglerLayoutValid = false;

   public VertexFormat(VertexFormat vertexFormatIn) {
      this();

      for(int i = 0; i < vertexFormatIn.getElementCount(); ++i) {
         this.addElement(vertexFormatIn.getElement(i));
      }

      this.vertexSize = vertexFormatIn.getSize();
   }

   public VertexFormat() {
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorElementOffset = -1;
      this.uvOffsetsById.clear();
      this.normalElementOffset = -1;
      this.vertexSize = 0;
      this.eaglerAttribBits = -1;
      this.eaglerLayoutValid = false;
   }

   public VertexFormat addElement(VertexFormatElement element) {
      if (element.isPositionElement() && this.hasPosition()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.elements.add(element);
         this.offsets.add(this.vertexSize);
         switch(element.getUsage()) {
         case NORMAL:
            this.normalElementOffset = this.vertexSize;
            break;
         case COLOR:
            this.colorElementOffset = this.vertexSize;
            break;
         case UV:
            this.uvOffsetsById.add(element.getIndex(), this.vertexSize);
         }

         this.vertexSize += element.getSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalElementOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalElementOffset;
   }

   public boolean hasColor() {
      return this.colorElementOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorElementOffset;
   }

   public boolean hasUv(int id) {
      return this.uvOffsetsById.size() - 1 >= id;
   }

   public int getUvOffsetById(int id) {
      return this.uvOffsetsById.get(id);
   }

   public String toString() {
      String s = "format: " + this.elements.size() + " elements: ";

      for(int i = 0; i < this.elements.size(); ++i) {
         s = s + this.elements.get(i).toString();
         if (i != this.elements.size() - 1) {
            s = s + " ";
         }
      }

      return s;
   }

   private boolean hasPosition() {
      int i = 0;

      for(int j = this.elements.size(); i < j; ++i) {
         VertexFormatElement vertexformatelement = this.elements.get(i);
         if (vertexformatelement.isPositionElement()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getSize() / 4;
   }

   public int getSize() {
      return this.vertexSize;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int index) {
      return this.elements.get(index);
   }

   public int getOffset(int index) {
      return this.offsets.get(index);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_equals_1_;
         if (this.vertexSize != vertexformat.vertexSize) {
            return false;
         } else {
            return !this.elements.equals(vertexformat.elements) ? false : this.offsets.equals(vertexformat.offsets);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.elements.hashCode();
      i = 31 * i + this.offsets.hashCode();
      i = 31 * i + this.vertexSize;
      return i;
   }

   public net.lax1dude.eaglercraft.opengl.VertexFormat toEagler()
   {
      boolean tex = this.hasUv(0);
      boolean color = this.hasColor();
      boolean normal = this.hasNormal();
      boolean lightmap = this.hasUv(1);
      if (tex && color && normal && lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.BLOCK_SHADERS;
      if (tex && color && !normal && lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.BLOCK;
      if (tex && color && normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_TEX_COLOR_NORMAL;
      if (tex && color && !normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_TEX_COLOR;
      if (tex && !color && normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_TEX_NORMAL;
      if (tex && !color && !normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_TEX;
      if (!tex && color && !normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_COLOR;
      if (!tex && !color && normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_NORMAL;
      if (!tex && !color && !normal && !lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION;
      if (tex && !color && !normal && lightmap) return net.lax1dude.eaglercraft.opengl.VertexFormat.POSITION_TEX_LMAP_COLOR;
      return net.lax1dude.eaglercraft.opengl.VertexFormat.BLOCK;
   }

   public int getEaglerAttribBits() {
      if (this.eaglerAttribBits == -1) {
         this.computeEaglerInfo();
      }
      return this.eaglerAttribBits;
   }

   public boolean isEaglerCompatible() {
      if (this.eaglerAttribBits == -1) {
         this.computeEaglerInfo();
      }
      return this.eaglerLayoutValid;
   }

   private void computeEaglerInfo() {
      net.lax1dude.eaglercraft.opengl.VertexFormat eag = this.toEagler();
      boolean valid = eag.attribStride == this.vertexSize;
      if (valid && this.hasUv(0)) valid = this.getUvOffsetById(0) == eag.attribTextureOffset;
      if (valid && this.hasColor()) valid = this.getColorOffset() == eag.attribColorOffset;
      if (valid && this.hasNormal()) valid = this.getNormalOffset() == eag.attribNormalOffset;
      if (valid && this.hasUv(1)) valid = this.getUvOffsetById(1) == eag.attribLightmapOffset;
      this.eaglerAttribBits = valid ? eag.eaglercraftAttribBits : 0;
      this.eaglerLayoutValid = valid;
   }
}
