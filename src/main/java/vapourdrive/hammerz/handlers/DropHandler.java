package vapourdrive.hammerz.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import vapourdrive.hammerz.items.HZ_Items;
import vapourdrive.hammerz.items.hammer.HammerInfoHandler;

public class DropHandler
{
	static final List<String> validBlocks = Arrays.asList(new String[]
	{
			"dirt", "sand", "gravel", "cobblestone", "netherrack"
	});

	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		if (event.getHarvester() != null)
		{
			ItemStack stack = event.getHarvester().getHeldItemMainhand();
			if (stack != null && stack.getItem() != null && stack.getItem() == HZ_Items.ItemHammer)
			{
				if (HammerInfoHandler.isStackElvenElementalHammer(stack))
				{
					for (int i = 0; i < event.getDrops().size(); i++)
					{
						ItemStack drop = event.getDrops().get(i);
						if (drop != null)
						{
							Block block = Block.getBlockFromItem(drop.getItem());
							if (block != null && isDisposable(block))
							{
								event.getDrops().remove(i);
							}
						}
					}
				}
				else if(HammerInfoHandler.isStackElementalHammer(stack))
				{
					for (int i = 0; i < event.getDrops().size(); i++)
					{
						ItemStack drop = event.getDrops().get(i);
						if (drop != null)
						{
							Block block = Block.getBlockFromItem(drop.getItem());
							ItemStack oreCluster = getOreCluster(event.getHarvester(), drop);
							if (block != null && oreCluster != null)
							{
								event.getDrops().remove(i);
								event.getDrops().add(oreCluster);
							}
						}
					}
				}
			}
		}
	}


	public ItemStack getOreCluster(EntityPlayer harvester, ItemStack drop)
	{
		float chance = 0.33f;
		chance = chance + (0.14f * EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
		Random random = new Random();
		if (random.nextFloat() <= chance)
		{
			int[] ids = OreDictionary.getOreIDs(new ItemStack(drop.getItem(), 1, drop.getItemDamage()));
			for(int i = 0; i < ids.length; i++)
			{
				String name = OreDictionary.getOreName(ids[i]);
				List<ItemStack> clusterList = OreDictionary.getOres(name.replace("ore", "cluster"));
				if(!clusterList.isEmpty())
				{
					ItemStack cluster = clusterList.get(0);
					if(cluster != null)
					{
						return cluster.copy();
					}
				}
			}
		}
		return null;
	}


	public static boolean isDisposable(Block block)
	{
		for (int id : OreDictionary.getOreIDs(new ItemStack(block)))
		{
			if (validBlocks.contains(OreDictionary.getOreName(id)))
			{
				return true;
			}
		}

		return false;
	}
}
