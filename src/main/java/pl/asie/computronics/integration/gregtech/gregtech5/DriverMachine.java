package pl.asie.computronics.integration.gregtech.gregtech5;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Config;

public class DriverMachine extends DriverSidedTileEntity {

    public static class ManagedEnvironmentMachine extends ManagedEnvironmentOCTile<IMachineProgress> {

        public ManagedEnvironmentMachine(IMachineProgress tile, String name) {
            super(tile, name);
        }

        @Override
        public int priority() {
            return 1;
        }

        @Callback(doc = "function():boolean; Returns true if the machine currently has work to do", direct = true)
        public Object[] hasWork(Context c, Arguments a) {
            return new Object[] { tile.hasThingsToDo() };
        }

        @Callback(doc = "function():number; Returns the current progress of this block", direct = true)
        public Object[] getWorkProgress(Context c, Arguments a) {
            return new Object[] { tile.getProgress() };
        }

        @Callback(doc = "function():number; Returns the max progress of this block", direct = true)
        public Object[] getWorkMaxProgress(Context c, Arguments a) {
            return new Object[] { tile.getMaxProgress() };
        }

        @Callback(doc = "function():boolean; Returns whether this block is currently allowed to work", direct = true)
        public Object[] isWorkAllowed(Context c, Arguments a) {
            return new Object[] { tile.isAllowedToWork() };
        }

        @Callback(doc = "function(work:boolean); Sets whether this block is currently allowed to work", direct = true)
        public Object[] setWorkAllowed(Context c, Arguments a) {
            if (a.count() == 1 && a.isBoolean(0)) {
                if (a.checkBoolean(0)) {
                    tile.enableWorking();
                } else {
                    tile.disableWorking();
                }
            }
            return null;
        }

        @Callback(doc = "function():boolean; Returns whether the machine is currently active", direct = true)
        public Object[] isMachineActive(Context c, Arguments a) {
            return new Object[] { tile.isActive() };
        }

        @Callback(doc = "function():table; Returns machine coordinates", direct = true)
        public Object[] getCoordinates(Context c, Arguments a) {
            if (Config.GT_COORDINATES) return new Object[] { tile.getXCoord(), tile.getYCoord(), tile.getZCoord() };
            else return new Object[] {};
        }

        @Callback(doc = "function():string; Returns the machine's name", direct = true)
        public Object[] getName(Context c, Arguments a) {
            return new Object[] { tile.getIGregTechTileEntity(tile.getXCoord(), tile.getYCoord(), tile.getZCoord())
                    .getMetaTileEntity().getMetaName() };
        }

        @Callback(
                doc = "function():number or (nil, string); Returns the parallel limit of a purification unit",
                direct = true)
        public Object[] getParallelLimit(Context c, Arguments a) {
            IMetaTileEntity mte = tile.getIGregTechTileEntity(tile.getXCoord(), tile.getYCoord(), tile.getZCoord())
                    .getMetaTileEntity();
            if (!(mte instanceof MTEPurificationUnitBase<?>unit)) {
                return new Object[] { null, "Machine is not a purification unit." };
            }
            return new Object[] { unit.getMaxParallel() };
        }

        @Callback(
                doc = "function(limit:number):boolean or (nil, string); Sets the parallel limit of a purification unit for the next cycle. Must be between 1 and 2147483647. Returns true on success.")
        public Object[] setParallelLimit(Context c, Arguments a) {
            IMetaTileEntity mte = tile.getIGregTechTileEntity(tile.getXCoord(), tile.getYCoord(), tile.getZCoord())
                    .getMetaTileEntity();
            if (!(mte instanceof MTEPurificationUnitBase<?>unit)) {
                return new Object[] { null, "Machine is not a purification unit." };
            }
            int limit = a.checkInteger(0);
            if (limit < 1) {
                return new Object[] { null, "Parallel limit must be at least 1." };
            }
            unit.setMaxParallel(limit);
            unit.markDirty();
            return new Object[] { true };
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return IMachineProgress.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        return new ManagedEnvironmentMachine((IMachineProgress) world.getTileEntity(x, y, z), "gt_machine");
    }
}
