package pl.asie.computronics.integration.gregtech.gregtech5;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class DriverTTMultiblockBase extends DriverSidedTileEntity {

    public static class ManagedEnvironmentTTMultiblockBase extends ManagedEnvironmentOCTile<BaseMetaTileEntity> {

        public ManagedEnvironmentTTMultiblockBase(BaseMetaTileEntity tile, String name) {
            super(tile, name);
        }

        @Callback(doc = "function(hatch:int, id: int, val:double); Sets a parameter", direct = true)
        public Object[] setParameters(Context c, Arguments a) {
            Parameters p = ((TTMultiblockBase) tile.getMetaTileEntity()).parametrization;
            p.trySetParameters(a.checkInteger(0), a.checkInteger(1), a.checkDouble(2));
            return null;
        }

        @Callback(doc = "function(hatch:int, id: int);double Returns the value of a parameter", direct = true)
        public Object[] getParameters(Context c, Arguments a) {
            return new Object[] { ((TTMultiblockBase) tile.getMetaTileEntity()).parametrization
                    .getGroup(a.checkInteger(0)).parameterIn[a.checkInteger((1))].get() };
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return BaseMetaTileEntity.class;
    }

    @Override
    public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return (tileEntity != null) && tileEntity instanceof BaseMetaTileEntity
                && ((BaseMetaTileEntity) tileEntity).getMetaTileEntity() instanceof TTMultiblockBase;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        return new ManagedEnvironmentTTMultiblockBase((BaseMetaTileEntity) world.getTileEntity(x, y, z), "tt_machine");
    }
}
