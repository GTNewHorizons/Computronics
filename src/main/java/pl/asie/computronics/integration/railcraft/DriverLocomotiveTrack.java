package pl.asie.computronics.integration.railcraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.DriverTileEntity;
import li.cil.oc.api.prefab.ManagedEnvironment;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackLocomotive;
import mods.railcraft.common.carts.EntityLocomotive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.integration.CCMultiPeripheral;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.reference.Names;

import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class DriverLocomotiveTrack {

	private static Object[] setMode(TileTrack tile, Object[] arguments) {
		byte mode = ((Double) arguments[0]).byteValue();
		NBTTagCompound data = new NBTTagCompound();
		tile.getTrackInstance().writeToNBT(data);
		data.setByte("mode", (byte) Math.abs(mode - 2));
		tile.getTrackInstance().readFromNBT(data);
		((TrackLocomotive) tile.getTrackInstance()).sendUpdateToClient();
		return new Object[] { true };
	}

	private static Object[] getMode(TileTrack tile) {
		NBTTagCompound data = new NBTTagCompound();
		tile.getTrackInstance().writeToNBT(data);
		return new Object[] { data.hasKey("mode") ? Math.abs(data.getByte("mode") % EntityLocomotive.LocoMode.VALUES.length - 2) : null };
	}

	private static Object[] modes() {
		LinkedHashMap<String, Integer> modeMap = new LinkedHashMap<String, Integer>();
		for(EntityLocomotive.LocoMode mode : EntityLocomotive.LocoMode.VALUES) {
			modeMap.put(mode.name().toLowerCase(Locale.ENGLISH), Math.abs(mode.ordinal() - 2));
		}
		return new Object[] { modeMap };
	}

	private static Object[] isPowered(TileTrack tile) {
		return new Object[] { ((TrackLocomotive) tile.getTrackInstance()).isPowered() };
	}

	public static class OCDriver extends DriverTileEntity {
		public class InternalManagedEnvironment extends ManagedEnvironmentOCTile<TileTrack> {

			public InternalManagedEnvironment(TileTrack tile) {
				super(tile, Names.Railcraft_LocomotiveTrack);
			}

			@Callback(doc = "function(mode:number):boolean; sets the Locomotive mode to the specified value; returns true on success")
			public Object[] setMode(Context c, Arguments a) {
				a.checkInteger(0);
				if(a.checkInteger(0) > 2 || a.checkInteger(0) < 0) {
					throw new IllegalArgumentException(
						"bad argument #1 (0, 1 or 2 expected, got " + a.checkInteger(0) + ")");
				}
				return DriverLocomotiveTrack.setMode(tile, a.toArray());
			}

			@Callback(doc = "function():number; returns the current Locomotive mode")
			public Object[] getMode(Context c, Arguments a) {
				return DriverLocomotiveTrack.getMode(tile);
			}

			@Callback(doc = "This is a table of every available Locomotive mode", getter = true)
			public Object[] modes(Context c, Arguments a) {
				return DriverLocomotiveTrack.modes();
			}

			@Callback(doc = "function():boolean; returns whether the track is currently receiving a redstone signal")
			public Object[] isPowered(Context c, Arguments a) {
				return DriverLocomotiveTrack.isPowered(tile);
			}
		}

		@Override
		public Class<?> getTileEntityClass() {
			return TileTrack.class;
		}

		@Override
		public boolean worksWith(World world, int x, int y, int z) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return (tileEntity != null) && tileEntity instanceof TileTrack
				&& ((TileTrack) tileEntity).getTrackInstance() instanceof TrackLocomotive;
		}

		@Override
		public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
			return new InternalManagedEnvironment((TileTrack) world.getTileEntity(x, y, z));
		}
	}

	public static class CCDriver extends CCMultiPeripheral<TileTrack> {
		public CCDriver() {
		}

		public CCDriver(TileTrack track, World world, int x, int y, int z) {
			super(track, Names.Railcraft_LocomotiveTrack, world, x, y, z);
		}

		@Override
		public IMultiPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileTrack && ((TileTrack) te).getTrackInstance() instanceof TrackLocomotive) {
				return new CCDriver((TileTrack) te, world, x, y, z);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "setMode", "getMode", "modes", "isPowered" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
			switch(method){
				case 0:{
					if(arguments.length < 1 || !(arguments[0] instanceof Double)) {
						throw new LuaException("first argument needs to be a number");
					}
					if((Double) arguments[0] > 2 || (Double) arguments[0] < 0) {
						throw new LuaException("mode needs to be between 0 and 2");
					}
					return DriverLocomotiveTrack.setMode(tile, arguments);
				}
				case 1:{
					return DriverLocomotiveTrack.getMode(tile);
				}
				case 2:{
					return DriverLocomotiveTrack.isPowered(tile);
				}
				case 3:{
					return DriverLocomotiveTrack.modes();
				}
			}
			return null;
		}
	}
}
