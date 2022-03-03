package jackyy.dimensionaledibles.block.tile;

import jackyy.dimensionaledibles.registry.ModConfig;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TileIslandCake extends TileDimensionCake {

    public boolean isPersonalCake = false;
    public UUID owner;

    public TileIslandCake() {
        super(ModConfig.tweaks.islandCake.islandDimension, "Island");
    }

    public boolean isPersonalCake() {
        return isPersonalCake;
    }

    public void setPersonalCake(boolean personalCake) {
        isPersonalCake = personalCake;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isPersonalCake", isPersonalCake());
        if (owner != null) {
            nbt.setUniqueId("owner", getOwner());
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.isPersonalCake = nbt.getBoolean("isPersonalCake");
        if (nbt.hasKey("owner"))
            this.owner = nbt.getUniqueId("owner");
    }
}
