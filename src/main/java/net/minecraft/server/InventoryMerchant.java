package net.minecraft.server;

// CraftBukkit start
import java.util.List;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.player.PlayerMerchantTradeEvent;
// CraftBukkit end

public class InventoryMerchant implements IInventory {

    private final IMerchant merchant;
    private ItemStack[] itemsInSlots = new ItemStack[3];
    private final EntityHuman player;
    private MerchantRecipe recipe;
    private int e;

    // CraftBukkit start
    public MerchantRecipeList offers;
    public MerchantRecipeList merchantOffers;
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return this.itemsInSlots;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public void setMaxStackSize(int i) {
        maxStack = i;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return player.getBukkitEntity();
    }
    // CraftBukkit end

    public InventoryMerchant(EntityHuman entityhuman, IMerchant imerchant) {
        this.player = entityhuman;
        this.merchant = imerchant;
        // CraftBukkit start - customize the offer for this situation
        this.merchantOffers = imerchant.getOffers(entityhuman); // Store the current merchant's offers so we can detect changes later
        PlayerMerchantTradeEvent event = CraftEventFactory.callPlayerMerchantTradeEvent(entityhuman, imerchant, merchantOffers, null, merchantOffers);
        List<org.bukkit.inventory.MerchantRecipe> offer = event.getOffer();
        this.offers = new MerchantRecipeList();
        for (org.bukkit.inventory.MerchantRecipe r : offer) {
            this.offers.add(this.offers.size(), new MerchantRecipe(((CraftItemStack) r.getBuyingItem1()).clone().getHandle(), ((CraftItemStack) r.getBuyingItem2()).clone().getHandle(), ((CraftItemStack) r.getResult()).clone().getHandle()));
        }
        // CraftBukkit end
    }

    public int getSize() {
        return this.itemsInSlots.length;
    }

    public ItemStack getItem(int i) {
        return this.itemsInSlots[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.itemsInSlots[i] != null) {
            ItemStack itemstack;

            if (i == 2) {
                itemstack = this.itemsInSlots[i];
                this.itemsInSlots[i] = null;
                return itemstack;
            } else if (this.itemsInSlots[i].count <= j) {
                itemstack = this.itemsInSlots[i];
                this.itemsInSlots[i] = null;
                if (this.d(i)) {
                    this.g();
                }

                return itemstack;
            } else {
                itemstack = this.itemsInSlots[i].a(j);
                if (this.itemsInSlots[i].count == 0) {
                    this.itemsInSlots[i] = null;
                }

                if (this.d(i)) {
                    this.g();
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    private boolean d(int i) {
        return i == 0 || i == 1;
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.itemsInSlots[i] != null) {
            ItemStack itemstack = this.itemsInSlots[i];

            this.itemsInSlots[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.itemsInSlots[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }

        if (this.d(i)) {
            this.g();
        }
    }

    public String getName() {
        return "mob.villager";
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.merchant.l_() == entityhuman;
    }

    public void startOpen() {}

    public void f() {}

    public void update() {
        this.g();
    }

    public void g() {
        this.recipe = null;
        ItemStack itemstack = this.itemsInSlots[0];
        ItemStack itemstack1 = this.itemsInSlots[1];

        if (itemstack == null) {
            itemstack = itemstack1;
            itemstack1 = null;
        }

        if (itemstack == null) {
            this.setItem(2, (ItemStack) null);
        } else {
            MerchantRecipeList merchantrecipelist = this.merchant.getOffers(this.player);
            // CraftBukkit start
            if (!merchantrecipelist.equals(this.merchantOffers)) {
                PlayerMerchantTradeEvent event = CraftEventFactory.callPlayerMerchantTradeEvent(this.player, this.merchant, merchantOffers, null, merchantOffers);
                List<org.bukkit.inventory.MerchantRecipe> offer = event.getOffer();
                this.offers = new MerchantRecipeList();
                for (org.bukkit.inventory.MerchantRecipe r : offer) {
                    this.offers.add(this.offers.size(), new MerchantRecipe(((CraftItemStack) r.getBuyingItem1()).clone().getHandle(), ((CraftItemStack) r.getBuyingItem2()).clone().getHandle(), ((CraftItemStack) r.getResult()).clone().getHandle()));
                }
                this.merchantOffers = merchantrecipelist;
            }
            // Last, set it to the offer
            merchantrecipelist = this.offers;
            // CraftBukkit end

            if (merchantrecipelist != null) {
                MerchantRecipe merchantrecipe = merchantrecipelist.a(itemstack, itemstack1, this.e);

                if (merchantrecipe != null) {
                    this.recipe = merchantrecipe;
                    this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
                } else if (itemstack1 != null) {
                    merchantrecipe = merchantrecipelist.a(itemstack1, itemstack, this.e);
                    if (merchantrecipe != null) {
                        this.recipe = merchantrecipe;
                        this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
                    } else {
                        this.setItem(2, (ItemStack) null);
                    }
                } else {
                    this.setItem(2, (ItemStack) null);
                }
            }
        }
    }

    // CraftBukkit start - get stored offers
    public MerchantRecipeList getOffers() {
        return this.offers;
    }
    // CraftBukkit end

    public MerchantRecipe getRecipe() {
        return this.recipe;
    }

    public void c(int i) {
        this.e = i;
        this.g();
    }
}
