package pl.asie.computronics.api.multiperipheral;

import net.minecraft.world.World;

import dan200.computercraft.api.peripheral.IPeripheralProvider;

/**
 * @author Vexatos
 */
public interface IMultiPeripheralProvider extends IPeripheralProvider {

    @Override
    public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side);

}
