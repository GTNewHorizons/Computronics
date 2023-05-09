package pl.asie.computronics.oc.block;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import li.cil.oc.api.driver.EnvironmentProvider;

/**
 * @author Vexatos
 */
public class ComputronicsBlockEnvironmentProvider implements EnvironmentProvider {

    @Override
    public Class<?> getEnvironment(ItemStack stack) {
        if (stack == null || stack.getItem() == null
                || !(stack.getItem() instanceof ItemBlock)
                || !(((ItemBlock) stack.getItem()).field_150939_a instanceof IComputronicsEnvironmentBlock)) {
            return null;
        }

        return ((IComputronicsEnvironmentBlock) ((ItemBlock) stack.getItem()).field_150939_a)
                .getTileEntityClass(stack.getItemDamage());
    }
}
