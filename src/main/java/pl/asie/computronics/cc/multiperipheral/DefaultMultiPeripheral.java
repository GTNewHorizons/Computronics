package pl.asie.computronics.cc.multiperipheral;

import pl.asie.computronics.api.multiperipheral.WrappedMultiPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * @author Vexatos
 */
public class DefaultMultiPeripheral extends WrappedMultiPeripheral {

    public DefaultMultiPeripheral(IPeripheral peripheral) {
        super(peripheral);
    }

    @Override
    public int peripheralPriority() {
        return 0;
    }
}
