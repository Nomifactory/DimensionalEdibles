package jackyy.dimensionaledibles.islands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class Island implements INBTSerializable<NBTTagCompound> {
    private UUID uuid = UUID.randomUUID();
    private int index;
    private BlockPos teleportLocation;
    private UUID owningPlayer;
    private short owningTeam;

    public Island() {
    }

    public Island(int index, BlockPos teleportLocation) {
        this.index = index;
        this.teleportLocation = teleportLocation;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getOwningPlayer() {
        return owningPlayer;
    }

    public void setOwningPlayer(UUID owningPlayer) {
        this.owningPlayer = owningPlayer;
    }

    public short getOwningTeam() {
        return owningTeam;
    }

    public void setOwningTeam(short owningTeam) {
        this.owningTeam = owningTeam;
    }

    public BlockPos getTeleportLocation() {
        return teleportLocation;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("uuid", uuid);
        nbt.setInteger("index", index);
        nbt.setLong("teleportLocation", teleportLocation.toLong());

        if (owningPlayer != null) { // fix NPE on save
            nbt.setUniqueId("owningPlayer", owningPlayer);
        }

        nbt.setShort("owningTeam", owningTeam);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        uuid = nbt.getUniqueId("uuid");
        index = nbt.getInteger("index");
        teleportLocation = BlockPos.fromLong(nbt.getLong("teleportLocation"));
        if (nbt.hasKey("owningPlayer"))
            owningPlayer = nbt.getUniqueId("owningPlayer");
        owningTeam = nbt.getShort("owningTeam");
    }
}
