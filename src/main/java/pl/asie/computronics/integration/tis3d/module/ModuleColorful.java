package pl.asie.computronics.integration.tis3d.module;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.tis3d.api.machine.Casing;
import li.cil.tis3d.api.machine.Face;
import li.cil.tis3d.api.machine.Pipe;
import li.cil.tis3d.api.machine.Port;
import li.cil.tis3d.api.util.RenderUtil;

/**
 * @author Vexatos
 */
public class ModuleColorful extends ComputronicsModule {

    private short color = 0x6318;

    public ModuleColorful(Casing casing, Face face) {
        super(casing, face);
    }

    @Override
    public void step() {
        super.step();
        for (Port port : Port.VALUES) {
            Pipe receivingPipe = this.getCasing().getReceivingPipe(this.getFace(), port);
            if (!receivingPipe.isReading()) {
                receivingPipe.beginRead();
            }
            if (receivingPipe.canTransfer()) {
                this.color = (short) (receivingPipe.read() & 0x7FFF);
                sendDataToClient();
            }
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.color = 0x6318;
        sendDataToClient();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("c")) {
            this.color = nbt.getShort("c");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("c", this.color);
    }

    private static final ResourceLocation LAMP_ICON = new ResourceLocation(
            "computronics:textures/blocks/lamp_layer_0.png");
    // private static final ResourceLocation front = new
    // ResourceLocation("computronics:textures/blocks/lamp_layer_1.png");

    @Override
    @SideOnly(Side.CLIENT)
    public void render(boolean enabled, float partialTicks) {
        if (!enabled) {
            return;
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.0F);

        RenderUtil.bindTexture(LAMP_ICON);
        GL11.glTranslated(0.0625F, 0.0625F, 0F);
        GL11.glScalef(0.875f, 0.875f, 0.875f);
        // int col = Color.HSBtoRGB((((System.currentTimeMillis() + (hashCode() % 30000)) % 30000) / 30000F), 1F, 1F) &
        // 0xFFFFFF;
        // GL11.glColor3ub((byte) ((col >> 16) & 0xFF), (byte) ((col >> 8) & 0xFF), (byte) (col & 0xFF));
        GL11.glColor3ub(
                (byte) (((color >> 10) & 0x1F) * 8),
                (byte) (((color >> 5) & 0x1F) * 8),
                (byte) ((color & 0x1F) * 8));
        RenderUtil.drawQuad();

        // bindTexture(front);
        // drawQuad();
    }
}
