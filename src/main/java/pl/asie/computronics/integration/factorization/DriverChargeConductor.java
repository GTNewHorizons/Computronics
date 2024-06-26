package pl.asie.computronics.integration.factorization;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import factorization.api.IChargeConductor;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

public class DriverChargeConductor {

    private static Object[] getCharge(IChargeConductor tile) {
        if (tile.getCharge() == null) {
            return new Object[] { 0 };
        }
        return new Object[] { tile.getCharge().getValue() };
    }

    public static class OCDriver extends DriverSidedTileEntity {

        public static class InternalManagedEnvironment extends ManagedEnvironmentOCTile<IChargeConductor> {

            public InternalManagedEnvironment(IChargeConductor tile) {
                super(tile, Names.FZ_ChargeConductor);
            }

            @Callback(doc = "function():number; Returns the charge of the block", direct = true)
            public Object[] getCharge(Context c, Arguments a) {
                return DriverChargeConductor.getCharge(tile);
            }
        }

        @Override
        public Class<?> getTileEntityClass() {
            return IChargeConductor.class;
        }

        @Override
        public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
            return new InternalManagedEnvironment((IChargeConductor) world.getTileEntity(x, y, z));
        }
    }

    public static class CCDriver extends CCMultiPeripheral<IChargeConductor> {

        public CCDriver() {}

        public CCDriver(IChargeConductor block, World world, int x, int y, int z) {
            super(block, Names.FZ_ChargeConductor, world, x, y, z);
        }

        @Override
        public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
            Block block = world.getBlock(x, y, z);
            if (block instanceof IChargeConductor) {
                return new CCDriver((IChargeConductor) block, world, x, y, z);
            }
            return null;
        }

        @Override
        public String[] getMethodNames() {
            return new String[] { "getCharge" };
        }

        @Override
        public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
                throws LuaException, InterruptedException {
            switch (method) {
                case 0: {
                    return DriverChargeConductor.getCharge(tile);
                }
            }
            return null;
        }
    }
}
