package pl.asie.computronics.integration.buildcraft.statements.parameters;

import pl.asie.computronics.integration.buildcraft.statements.StatementParameters;
import buildcraft.api.statements.IStatementParameter;

/**
 * @author Vexatos
 */
public abstract class ComputronicsParameter implements IStatementParameter {

    protected final String name;

    public ComputronicsParameter(StatementParameters param) {
        this.name = param.name;
    }

    @Override
    public String getUniqueTag() {
        return "computronics:parameter." + this.name;
    }

}
