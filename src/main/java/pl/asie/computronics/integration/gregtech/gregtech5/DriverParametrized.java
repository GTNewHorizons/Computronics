package pl.asie.computronics.integration.gregtech.gregtech5;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import tectech.thing.metaTileEntity.multi.base.parameter.BooleanParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.CompositeParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.DoubleParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.IParametrized;
import tectech.thing.metaTileEntity.multi.base.parameter.IntegerParameter;
import tectech.thing.metaTileEntity.multi.base.parameter.Parameter;
import tectech.thing.metaTileEntity.multi.base.parameter.StringParameter;

public class DriverParametrized extends DriverSidedTileEntity {

    private static class ParameterKV {

        public String key;
        public Parameter<?> value;

        public ParameterKV(String key, Parameter<?> value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class ParameterStreams {

        public static Stream<ParameterKV> flatten(List<Parameter<?>> parameterList) {
            return parameterList.stream().flatMap(p -> flatten("", p));
        }

        private static Stream<ParameterKV> flatten(String prefix, Parameter<?> parameter) {
            String fullKey = prefix.isEmpty() ? parameter.getNbtKey() : prefix + "." + parameter.getNbtKey();
            if (parameter instanceof CompositeParameter compositeParameter) {
                return compositeParameter.getValue().stream().flatMap(child -> flatten(fullKey, child));
            }
            return Stream.of(new ParameterKV(fullKey, parameter));
        }
    }

    public static class ManagedEnvironmentParametrized extends ManagedEnvironmentOCTile<BaseMetaTileEntity> {

        public ManagedEnvironmentParametrized(BaseMetaTileEntity tile, String name) {
            super(tile, name);
        }

        @Callback(doc = "function(key:string, val:any); Sets a parameter", direct = false)
        public Object[] setParameter(Context c, Arguments a) {
            List<Parameter<?>> parameterList = ((IParametrized) tile.getMetaTileEntity()).getParameters();
            String key = a.checkString(0);
            Parameter<?> p = ParameterStreams.flatten(parameterList).filter(parameterKV -> parameterKV.key.equals(key))
                    .map(parameterKV -> parameterKV.value).findFirst().orElseThrow(() -> {
                        List<String> validKeys = ParameterStreams.flatten(parameterList)
                                .map(parameterKV -> parameterKV.key).collect(Collectors.toList());
                        return new IllegalArgumentException("invalid parameter key, must be in " + validKeys);
                    });

            if (p instanceof BooleanParameter boolParam) {
                boolParam.setValue(a.checkBoolean(1));
                return null;
            }
            if (p instanceof DoubleParameter doubleParam) {
                doubleParam.setValue(a.checkDouble(1));
                return null;
            }
            if (p instanceof IntegerParameter intParam) {
                intParam.setValue(a.checkInteger(1));
                return null;
            }
            if (p instanceof StringParameter strParam) {
                strParam.setValue(a.checkString(1));
                return null;
            }
            throw new IllegalArgumentException("unsupported parameter type");
        }

        @Callback(doc = "function():table; Returns the value of all parameters", direct = true)
        public Object[] getParameters(Context c, Arguments a) {
            List<Parameter<?>> parameterList = ((IParametrized) tile.getMetaTileEntity()).getParameters();
            LinkedHashMap<String, Object> parameters = ParameterStreams.flatten(parameterList).collect(
                    Collectors.toMap(
                            parameterKV -> parameterKV.key,
                            parameterKV -> parameterKV.value,
                            (parameterA, parameterB) -> parameterB,
                            LinkedHashMap::new));
            return new Object[] { parameters };
        }
    }

    @Override
    public Class<?> getTileEntityClass() {
        return BaseMetaTileEntity.class;
    }

    @Override
    public boolean worksWith(World world, int x, int y, int z, ForgeDirection side) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof BaseMetaTileEntity bmt) {
            return bmt.getMetaTileEntity() instanceof IParametrized;
        }
        return false;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, int x, int y, int z, ForgeDirection side) {
        return new ManagedEnvironmentParametrized((BaseMetaTileEntity) world.getTileEntity(x, y, z), "tt_machine");
    }
}
