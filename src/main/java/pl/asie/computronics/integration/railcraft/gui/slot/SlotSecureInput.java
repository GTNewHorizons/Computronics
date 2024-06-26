package pl.asie.computronics.integration.railcraft.gui.slot;

import net.minecraft.inventory.IInventory;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.gui.slots.SlotSecure;

/**
 * @author Vexatos
 */
public class SlotSecureInput extends SlotSecure {

    public SlotSecureInput(IStackFilter filter, IInventory contents, int id, int x, int y) {
        super(filter, contents, id, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 64;
    }
}
