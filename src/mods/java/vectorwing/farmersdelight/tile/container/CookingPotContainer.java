package vectorwing.farmersdelight.tile.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import vectorwing.farmersdelight.tile.inventory.ItemStackInventory;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.tile.CookingPotTileEntity;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModContainerTypes;


public class CookingPotContainer extends Container
{
	public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation(FarmersDelight.MODID, "item/empty_container_slot_bowl");

	public final IInventory inventoryHandler;
	private final IIntArray cookingPotData;
	private final IWorldPosCallable canInteractWithCallable;
	private final net.minecraft.world.World playerWorld;

	public CookingPotContainer(final int windowId, final PlayerInventory playerInventory, final IInventory inventoryIn, IIntArray cookingPotDataIn)
	{
		super(ModContainerTypes.COOKING_POT, windowId);
		this.playerWorld = playerInventory.player.world;
		this.inventoryHandler = inventoryIn;
		this.cookingPotData = cookingPotDataIn;
		this.canInteractWithCallable = IWorldPosCallable.of(playerInventory.player.world, playerInventory.player.getPosition());

		// Ingredient Slots - 2 Rows x 3 Columns
		int startX = 8;
		int startY = 18;
		int inputStartX = 30;
		int inputStartY = 17;
		int borderSlotSize = 18;
		for (int row = 0; row < 2; ++row) {
			for (int column = 0; column < 3; ++column) {
				this.addSlot(new Slot(inventoryHandler, (row * 3) + column,
						inputStartX + (column * borderSlotSize),
						inputStartY + (row * borderSlotSize)));
			}
		}

		// Meal Display
		this.addSlot(new CookingPotMealSlot(inventoryHandler, 6, 124, 26));

		// Bowl Input
		this.addSlot(new Slot(inventoryHandler, 7, 92, 55));

		// Bowl Output
		this.addSlot(new CookingPotResultSlot(inventoryHandler, 8, 124, 55));

		// Main Player Inventory
		int startPlayerInvY = startY * 4 + 12;
		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
						startPlayerInvY + (row * borderSlotSize)));
			}
		}

		// Hotbar
		for (int column = 0; column < 9; ++column) {
			this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
		}

		this.trackIntArray(cookingPotDataIn);
	}

	public CookingPotContainer(final int windowId, final PlayerInventory playerInventory) {
		this(windowId, playerInventory, new ItemStackInventory(CookingPotTileEntity.INVENTORY_SIZE), new IntArray(5));
	}

	@Nullable
	public CookingPotTileEntity getTileEntity() {
		BlockPos pos = new BlockPos(this.cookingPotData.get(2), this.cookingPotData.get(3), this.cookingPotData.get(4));
		TileEntity tileAtPos = this.playerWorld.getTileEntity(pos);
		return tileAtPos instanceof CookingPotTileEntity ? (CookingPotTileEntity) tileAtPos : null;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.COOKING_POT);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		int indexContainerInput = 7;
		int indexOutput = 8;
		int startPlayerInv = indexOutput + 1;
		int endPlayerInv = startPlayerInv + 36;
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == indexOutput) {
				if (!this.mergeItemStack(itemstack1, startPlayerInv, endPlayerInv, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index > indexOutput) {
				if (itemstack1.getItem() == Items.BOWL && !this.mergeItemStack(itemstack1, indexContainerInput, indexContainerInput+1, false)) {
					return ItemStack.EMPTY;
				} else if (!this.mergeItemStack(itemstack1, 0, indexOutput, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, startPlayerInv, endPlayerInv, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@OnlyIn(Dist.CLIENT)
	public int getCookProgressionScaled() {
		int i = this.cookingPotData.get(0);
		int j = this.cookingPotData.get(1);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isHeated() {
		CookingPotTileEntity tileEntity = this.getTileEntity();
		return tileEntity != null && tileEntity.isAboveLitHeatSource();
	}
}
