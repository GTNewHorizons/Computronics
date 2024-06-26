package pl.asie.computronics.cc.multiperipheral;

import java.util.ArrayList;

import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralRegistry;

/**
 * @author Vexatos
 */
public class MultiPeripheralRegistry implements IMultiPeripheralRegistry {

    public final ArrayList<IMultiPeripheralProvider> peripheralProviders;

    public MultiPeripheralRegistry() {
        this.peripheralProviders = new ArrayList<IMultiPeripheralProvider>();
    }

    @Override
    public void registerPeripheralProvider(IMultiPeripheralProvider provider) {
        peripheralProviders.add(provider);
    }
}
