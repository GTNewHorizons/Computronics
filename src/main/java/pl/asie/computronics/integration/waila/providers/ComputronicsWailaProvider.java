package pl.asie.computronics.integration.waila.providers;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public abstract class ComputronicsWailaProvider implements IComputronicsWailaProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        return currenttip;
    }

    @Override
    public abstract List<String> getWailaBody(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config);

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        return currenttip;
    }
}
