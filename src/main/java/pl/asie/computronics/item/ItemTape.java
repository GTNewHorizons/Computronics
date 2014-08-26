package pl.asie.computronics.item;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.media.IMediaProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.IItemStorage;
import pl.asie.computronics.item.entity.EntityItemIndestructable;
import pl.asie.computronics.tape.Storage;
import pl.asie.lib.util.color.ItemColorizer;

import java.util.List;

@Optional.InterfaceList({
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMediaProvider", modid = "ComputerCraft"),
	@Optional.Interface(iface = "dan200.computercraft.api.media.IMedia", modid = "ComputerCraft")
})
public class ItemTape extends Item implements IItemStorage, IMedia, IMediaProvider {
	public static final int L_SECOND = 4096;
	public static final int L_MINUTE = 4096*60;

	private static final int TAPE_COUNT = 10;
	private static final int[] DEFAULT_LENGTHS = { 4, 8, 16, 32, 64, 2, 6, 16, 128, 128};

	private int[] sizes;
	private IIcon tape_i, tape_g, tape_d, tape_n, tape_c, tape_co, tape_st, tape_greg, tape_ig;

	public ItemTape(String lengths) {
		super();
		this.setUnlocalizedName("computronics.tape");
		this.setTextureName("computronics:tape");
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);

		// parse lengths
		String[] l = lengths.split(",");
		sizes = new int[l.length];
		for(int i = 0; i < TAPE_COUNT; i++) {
			if(i<l.length) {
				sizes[i] = Integer.parseInt(l[i]) * L_MINUTE;
				if(sizes[i] <= 0)
					sizes[i] = 4;
			} else {
				sizes[i]=DEFAULT_LENGTHS[i] * L_MINUTE;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r) {
		tape_i = r.registerIcon("computronics:tape");
		tape_g = r.registerIcon("computronics:tape_gold");
		tape_d = r.registerIcon("computronics:tape_diamond");
		tape_n = r.registerIcon("computronics:tape_nether_star");
		tape_c = r.registerIcon("computronics:tape_cover");
		tape_co = r.registerIcon("computronics:tape_copper");
		tape_st = r.registerIcon("computronics:tape_steel");
		tape_greg = r.registerIcon("computronics:tape_greg");
		tape_ig = r.registerIcon("computronics:tape_greg");
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		if(pass == 0) switch(meta) {
			case 0: return tape_i;
			case 1: return tape_g;
			case 2: return tape_g;
			case 3: return tape_d;
			case 4: return tape_n;
			case 5: return tape_co;
			case 6: return tape_st;
			case 7: return tape_greg;
			case 8: return tape_n;
			case 9: return tape_ig;

			default: return tape_i;
		}
		else return tape_c;
	}

	public String getLabel(ItemStack stack) {
		return stack.getTagCompound().hasKey("label") ? stack.getTagCompound().getString("label") : "";
	}

	public boolean setLabel(ItemStack stack, String label) {
		if(stack == null) return false;
		stack.getTagCompound().setString("label", label);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean par4) {
		int size = getSize(stack);
		int len = (int)Math.floor(size / L_MINUTE);
		if(stack.getTagCompound() != null) {
			String label = getLabel(stack);
			if(label.length() > 0) text.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + label);
		}
		text.add(EnumChatFormatting.GRAY + I18n.format("tooltip.computronics.tape.length", ""+len));

		switch (stack.getItemDamage()){
			case 7:{
				text.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.computronics.tape.balanced"));
				break;
			}
			case 9:{
				String[] local = StatCollector.translateToLocal("tooltip.computronics.tape.ig")
					.replace("\\n", "\n").split("\\n");
				for(String s : local) {
					text.add(EnumChatFormatting.AQUA + s);
				}
				break;
			}
			default: break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for(int i = 0; i < TAPE_COUNT; i++) {
			/*if((i == 7 || i == 9) && !Loader.isModLoaded("gregtech")) {
				//Do nothing. If we return here, we lose all new tapes.
			}*/
			list.add(new ItemStack(item, 1, i));
		}
     }

	public int getSize(ItemStack stack) {
		return getSize(stack.getItemDamage());
	}
	public int getSize(int meta) {
		return sizes[meta % sizes.length];
	}

	public Storage getStorage(ItemStack stack) {
		int size = getSize(stack);

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("storage")) {
			// Exists, read NBT data if everything is alright
			NBTTagCompound nbt = stack.getTagCompound();
			String storageName = nbt.getString("storage");
			if(Computronics.storage.exists(storageName))
				return Computronics.storage.get(storageName, size, 0);
		}

		// Doesn't exist, create new storage and write NBT data
		Storage storage = Computronics.storage.newStorage(size);
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("storage", storage.getName());
		return storage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack != null && itemstack.getItemDamage() == 9)
			return "item.computronics.tape.ig";
		return super.getUnlocalizedName(itemstack);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return stack != null && stack.getItemDamage() == 9;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		if(itemstack != null && itemstack.getItemDamage() == 9) {
			EntityItemIndestructable newTapeEntity = new EntityItemIndestructable(
				world,location.posX,location.posY,location.posZ, itemstack);
			newTapeEntity.delayBeforeCanPickup = 40;
			newTapeEntity.motionX = location.motionX;
			newTapeEntity.motionY = location.motionY;
			newTapeEntity.motionZ = location.motionZ;
			return newTapeEntity;
		}
		return super.createEntity(world, location, itemstack);
	}

	// Colorizing
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        return pass == 0 ? 16777215 : (ItemColorizer.hasColor(stack) ? ItemColorizer.getColor(stack) : 16777215);
    }

	@Override
	public IMedia getMedia(ItemStack stack) {
		if(stack != null && stack.stackSize > 0 && stack.getItem() != null && stack.getItem() instanceof ItemTape) return ((IMedia)stack.getItem());
		return null;
	}

	@Override
	public String getAudioTitle(ItemStack stack) { return null; }
	@Override
	public String getAudioRecordName(ItemStack stack) { return null; }
	@Override
	public IMount createDataMount(ItemStack stack, World world) { return null; }
}
