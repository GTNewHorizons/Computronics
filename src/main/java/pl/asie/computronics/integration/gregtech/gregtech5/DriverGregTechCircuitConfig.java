package pl.asie.computronics.integration.gregtech.gregtech5;

import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.common.items.ItemIntegratedCircuit;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;

/**
 * @author Asie, Vexatos, Gemini
 *
 * Driver for GregTech MetaTileEntities that support circuit configuration
 * via {@link IConfigurationCircuitSupport}.
 * Exposes a "gt_circuit_config" component.
 * The managed environment operates on {@link IMetaTileEntity}.
 */
public class DriverGregTechCircuitConfig extends DriverSidedTileEntity {

    private static final int NO_CONFIG = -1; // Similar to GhostCircuitItemStackHandler.NO_CONFIG

    public static class ManagedEnvironmentGregTechCircuitConfig extends ManagedEnvironmentOCTile<IMetaTileEntity> {
        public static final int MAX_CIRCUIT_NUMBER = 24;

        public ManagedEnvironmentGregTechCircuitConfig(IMetaTileEntity tile, String name) {
            super(tile, name);
        }

        @Override
        public int priority() {
            return 0; // Default priority, distinct component name "gt_circuit_config"
        }

        private int getCircuitSlot() {
            if (this.tile instanceof IConfigurationCircuitSupport) {
                return ((IConfigurationCircuitSupport) this.tile).getCircuitSlot();
            }
            return -1;
        }

        @Callback(doc = "function():number; Gets the circuit configuration. Returns -1 if no circuit or not applicable.")
        public Object[] getCircuitConfiguration(Context context, Arguments args) {
            if (!(this.tile instanceof IConfigurationCircuitSupport)) {
                return new Object[]{null, "Machine does not support circuit configuration."};
            }
            int circuitSlot = getCircuitSlot();
            if (circuitSlot < 0 || circuitSlot >= this.tile.getSizeInventory()) {
                // Computronics.log.warn("Invalid circuit slot " + circuitSlot + " for " + this.tile.getMetaName());
                return new Object[]{null, "Invalid circuit slot index."};
            }

            ItemStack stack = this.tile.getStackInSlot(circuitSlot);
            if (stack != null && stack.getItem() instanceof ItemIntegratedCircuit) {
                return new Object[]{stack.getItemDamage()};
            }
            return new Object[]{NO_CONFIG};
        }

        @Callback(doc = "function(config:number):boolean or (nil, string); Sets the circuit configuration. Use -1 to remove circuit. Returns true on success.")
        public Object[] setCircuitConfiguration(Context context, Arguments args) {
            if (!(this.tile instanceof IConfigurationCircuitSupport)) {
                return new Object[]{null, "Machine does not support circuit configuration."};
            }
            int circuitSlot = getCircuitSlot();
            if (circuitSlot < 0 || circuitSlot >= this.tile.getSizeInventory()) {
                // Computronics.log.warn("Invalid circuit slot " + circuitSlot + " for " + this.tile.getMetaName());
                return new Object[]{null, "Invalid circuit slot index."};
            }

            int config = args.checkInteger(0);

            if (config == NO_CONFIG) {
                this.tile.setInventorySlotContents(circuitSlot, null);
                return new Object[]{true};
            } else if (config >= 1 && config <= MAX_CIRCUIT_NUMBER) {
                ItemStack circuitStack = GTUtility.getIntegratedCircuit(config);
                this.tile.setInventorySlotContents(circuitSlot, circuitStack);
                return new Object[]{true};
            } else {
                return new Object[]{null, "Invalid circuit configuration value: " + config + ". Must be between 1 and " + MAX_CIRCUIT_NUMBER + ", or " + NO_CONFIG + "."};
            }
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        // The driver attaches to the BaseMetaTileEntity which hosts the IMetaTileEntity
        return BaseMetaTileEntity.class;
    }

    @Override
    public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof BaseMetaTileEntity) {
            IMetaTileEntity mte = ((BaseMetaTileEntity) tileEntity).getMetaTileEntity();
            return mte instanceof IConfigurationCircuitSupport;
        }
        return false;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof BaseMetaTileEntity) {
            IMetaTileEntity mte = ((BaseMetaTileEntity) tileEntity).getMetaTileEntity();
            if (mte instanceof IConfigurationCircuitSupport) { // Ensure again before creating environment
                return new ManagedEnvironmentGregTechCircuitConfig(mte, "gt_circuit_config");
            }
        }
        return null;
    }
}
