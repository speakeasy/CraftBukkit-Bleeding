package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

import org.bukkit.block.Beacon.ActivationState;
import org.bukkit.craftbukkit.block.CraftBeacon;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.potion.CraftPotionBrewer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.BeaconPulseEvent;
import org.bukkit.event.player.PlayerPayBeaconEvent;
import org.bukkit.potion.PotionEffect;
// CraftBukkit end

public class TileEntityBeacon extends TileEntity implements IInventory {

    public static final MobEffectList[][] a = new MobEffectList[][] { { MobEffectList.FASTER_MOVEMENT, MobEffectList.FASTER_DIG}, { MobEffectList.RESISTANCE, MobEffectList.JUMP}, { MobEffectList.INCREASE_DAMAGE}, { MobEffectList.REGENERATION}};
    private boolean d;
    public int e = -1; // CraftBukkit - private -> public
    private int f;
    private int g;
    private ItemStack inventorySlot;
    private String i;
    // CraftBukkit start
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;
    public boolean customEffects = false;
    public List<MobEffect> effects;
    public double radius = -1;
    public ActivationState overrideState = ActivationState.DEFAULT;

    public ItemStack[] getContents() {
        return new ItemStack[] { this.inventorySlot };
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

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    public boolean isEnabled() {
        if (overrideState == ActivationState.OFF) {
            return false;
        } else {
            if (overrideState == ActivationState.ON || this.d && this.e > 0) {
                updateEffects();
                return !effects.isEmpty();
            }
        }
        return false;
    }

    public void updateEffects() {
        if (!customEffects) {
            effects = getDefaultEffects(this.e, this.f, this.g);
        }
    }

    public List<MobEffect> getDefaultEffects() {
        return getDefaultEffects(this.e, this.f, this.g);
    }

    public static List<MobEffect> getDefaultEffects(int pyramid, int primary, int secondary) {
        if (pyramid <= 0 || primary <= 0) {
            return ImmutableList.of();
        } else if (pyramid >= 4 && primary == secondary) {
            return ImmutableList.of(new MobEffect(primary, 180, 1, true));
        } else if (pyramid >= 4 && secondary > 0) {
            return ImmutableList.of(new MobEffect(primary, 180, 0, true), new MobEffect(secondary, 180, 0, true));
        } else {
            return ImmutableList.of(new MobEffect(primary, 180, 0, true));
        }
    }

    // Arguments must be pre-validated
    public void setEffectIds(int left, int right) {
        this.f = left;
        this.g = right;
    }

    public double getRadius() {
        if (radius == -1) {
            return this.e >= 4 ? 50D : (double) (this.e * 10 + 10);
        }
        return radius;
    }
    // CraftBukkit end

    public TileEntityBeacon() {}

    public void h() {
        if (this.world.getTime() % 80L == 0L) {
            this.v();
            this.u();
        }
    }

    private void u() {
        // CraftBukkit start
        if (this.world.isStatic) {
            return;
        }
        if (isEnabled()) {
            double d0 = getRadius();
            // CraftBukkit end

            AxisAlignedBB axisalignedbb = AxisAlignedBB.a().a((double) this.x, (double) this.y, (double) this.z, (double) (this.x + 1), (double) (this.y + 1), (double) (this.z + 1)).grow(d0, d0, d0);

            axisalignedbb.e = (double) this.world.getHeight();
            List list = this.world.a(EntityHuman.class, axisalignedbb);
            // CraftBukkit start - call event, use custom effects
            List<HumanEntity> affected = new ArrayList<HumanEntity>(list.size());
            for (Object o : list) {
                affected.add(((EntityHuman) o).getBukkitEntity());
            }
            BeaconPulseEvent event = CraftEventFactory.callBeaconPulseEvent(this, affected);
            List<PotionEffect> eventEffects = event.getEffects();

            for (HumanEntity entity : event.getPlayers()) {
                // We must use vanilla addEffect() code because it's different
                EntityHuman entityhuman = ((CraftHumanEntity) entity).getHandle();
                for (PotionEffect eff : eventEffects) {
                    entityhuman.addEffect(new MobEffect(eff.getType().getId(), 180, eff.getAmplifier(), true));
                }
                // CraftBukkit end
            }
        }
    }

    private void v() {
        if (!this.world.l(this.x, this.y + 1, this.z)) {
            this.d = false;
            this.e = 0;
        } else {
            this.d = true;

            // CraftBukkit - split into method
            countPyramid(4);

            if (this.e == 0) {
                this.d = false;
            }
        }
    }

    // CraftBukkit start - split method
    public int countPyramid(int max) {
        this.e = 0;
        for (int i = 1; i <= max; this.e = i++) {
            int j = this.y - i;

            if (j < 0) {
                break;
            }

            boolean flag = true;

            for (int k = this.x - i; k <= this.x + i && flag; ++k) {
                for (int l = this.z - i; l <= this.z + i; ++l) {
                    int i1 = this.world.getTypeId(k, j, l);

                    if (i1 != Block.EMERALD_BLOCK.id && i1 != Block.GOLD_BLOCK.id && i1 != Block.DIAMOND_BLOCK.id && i1 != Block.IRON_BLOCK.id) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                break;
            }
        }
        return this.e;
    }
    // CraftBukkit end

    public int j() {
        return this.f;
    }

    public int k() {
        return this.g;
    }

    public int l() {
        return this.e;
    }

    // CraftBukkit start - call event
    public boolean pickEffects(EntityPlayer player, int prim, int seco) {
        int newPri = d(prim); // should be choosePrimary
        int newSec = e(seco); // should be chooseSecondary
        // The event clones the list, so we can compare the results to the original
        List<PotionEffect> newEffects = CraftPotionBrewer.nmsToBukkitEffects(getDefaultEffects(this.e, newPri, newSec));
        PlayerPayBeaconEvent event = new PlayerPayBeaconEvent(new CraftBeacon(this.world.getWorld().getBlockAt(this.x, this.y, this.z)), player.getBukkitEntity(), newEffects);
        this.world.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            setEffectIds(newPri, newSec);
            // Set customEffects to false if the effects weren't changed
            if (newEffects.equals(event.getNewEffects())) {
                customEffects = false;
                updateEffects();
            } else {
                // TODO if given effects are possible, set pri/sec to those
                customEffects = true;
                effects = CraftPotionBrewer.bukkitToNmsEffects(event.getNewEffects());
            }
            return true;
        }
        return false;
    }

    public int d(int i) {
        // CraftBukkit end
        for (int j = 0; j < this.e && j < 3; ++j) {
            MobEffectList[] amobeffectlist = a[j];
            int k = amobeffectlist.length;

            for (int l = 0; l < k; ++l) {
                MobEffectList mobeffectlist = amobeffectlist[l];

                if (mobeffectlist.id == i) {
                    return i; // CraftBukkit - return instead of set
                }
            }
        }
        return 0; // CraftBukkit - return instead of set
    }

    // CraftBukkit - return instead of set
    public int e(int i) {
        if (this.e >= 4) {
            for (int j = 0; j < 4; ++j) {
                MobEffectList[] amobeffectlist = a[j];
                int k = amobeffectlist.length;

                for (int l = 0; l < k; ++l) {
                    MobEffectList mobeffectlist = amobeffectlist[l];

                    if (mobeffectlist.id == i) {
                        return i; // CraftBukkit - return instead of set
                    }
                }
            }
        }
        return 0; // CraftBukkit - return instead of set
    }

    public Packet getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.b(nbttagcompound);
        return new Packet132TileEntityData(this.x, this.y, this.z, 3, nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.f = nbttagcompound.getInt("Primary");
        this.g = nbttagcompound.getInt("Secondary");
        this.e = nbttagcompound.getInt("Levels");
        // CraftBukkit start - persist custom effects
        if (nbttagcompound.hasKey("Bukkit.Effects")) {
            customEffects = true;
            NBTTagList list = (NBTTagList) nbttagcompound.getList("Bukkit.Effects");

            effects = new java.util.ArrayList<MobEffect>();

            final int size = list.size();
            for (int i = 0; i < size; i++) {
                NBTTagCompound effectTag = (NBTTagCompound) list.get(i);
                effects.add(new MobEffect(effectTag.getInt("id"), 180, effectTag.getInt("amp"), true));
            }
        } else {
            updateEffects();
        }
        if (nbttagcompound.hasKey("Bukkit.Radius")) {
            this.radius = nbttagcompound.getDouble("Bukkit.Radius");
            if (radius < 0) {
                radius = -1;
            }
        } else {
            this.radius = -1;
        }
        if (nbttagcompound.hasKey("Bukkit.Override")) {
            this.overrideState = nbttagcompound.getBoolean("Bukkit.Override") ? ActivationState.ON : ActivationState.OFF;
        } else {
            this.overrideState = ActivationState.DEFAULT;
        }
        // CraftBukkit end
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Primary", this.f);
        nbttagcompound.setInt("Secondary", this.g);
        nbttagcompound.setInt("Levels", this.e);
        // CraftBukkit start - persist custom effects
        if (customEffects) {
            NBTTagList tagList = new NBTTagList("Bukkit.Effects");
            for (MobEffect eff : effects) {
                NBTTagCompound effectTag = new NBTTagCompound();
                effectTag.setInt("id", eff.getEffectId());
                effectTag.setInt("amp", eff.getAmplifier());
                tagList.add(effectTag);
            }
            nbttagcompound.set("Bukkit.Effects", tagList);
        }
        if (radius != -1) {
            nbttagcompound.setDouble("Bukkit.Radius", radius);
        }
        if (overrideState != ActivationState.DEFAULT) {
            nbttagcompound.setBoolean("Bukkit.Override", overrideState == ActivationState.ON);
        }
        // CraftBukkit end
    }

    public int getSize() {
        return 1;
    }

    public ItemStack getItem(int i) {
        return i == 0 ? this.inventorySlot : null;
    }

    public ItemStack splitStack(int i, int j) {
        if (i == 0 && this.inventorySlot != null) {
            if (j >= this.inventorySlot.count) {
                ItemStack itemstack = this.inventorySlot;

                this.inventorySlot = null;
                return itemstack;
            } else {
                this.inventorySlot.count -= j;
                return new ItemStack(this.inventorySlot.id, j, this.inventorySlot.getData());
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (i == 0 && this.inventorySlot != null) {
            ItemStack itemstack = this.inventorySlot;

            this.inventorySlot = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i == 0) {
            this.inventorySlot = itemstack;
        }
    }

    public String getName() {
        return this.c() ? this.i : "container.beacon";
    }

    public boolean c() {
        return this.i != null && this.i.length() > 0;
    }

    public void a(String s) {
        this.i = s;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : entityhuman.e((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
    }

    public void startOpen() {}

    public void g() {}

    public boolean b(int i, ItemStack itemstack) {
        return itemstack.id == Item.EMERALD.id || itemstack.id == Item.DIAMOND.id || itemstack.id == Item.GOLD_INGOT.id || itemstack.id == Item.IRON_INGOT.id;
    }
}
