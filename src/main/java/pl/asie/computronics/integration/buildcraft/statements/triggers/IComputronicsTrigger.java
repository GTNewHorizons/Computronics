package pl.asie.computronics.integration.buildcraft.statements.triggers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

/**
 * @author Vexatos
 */
public interface IComputronicsTrigger {

    public boolean isTriggerActive(TileEntity tile, ForgeDirection side, IStatementContainer container,
            IStatementParameter[] statements);

}
