package jackyy.dimensionaledibles.islands;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static jackyy.dimensionaledibles.DimensionalEdibles.MODID;
import static jackyy.dimensionaledibles.DimensionalEdibles.logger;

public class IslandManager {
    private static final HashMap<World, IslandManager> MANAGERS = new HashMap<>();
    private final IslandsWorldSavedData data;

    private IslandManager(World world) {
        data = IslandsWorldSavedData.get(world);
    }

    public static IslandManager forWorld(World world) {
        IslandManager manager = MANAGERS.get(world);
        if (manager == null) {
            manager = new IslandManager(world);
            MANAGERS.put(world, manager);
        }
        return manager;
    }

    /**
     * Returns an island for a player by id, creating one if the player doesn't already have an island
     */
    public Island getIslandForPlayer(UUID id) {
        Island island = data.getByOwningPlayer(id);
        if (island == null) {
            island = data.createPlayerOwnedIsland(id);
        }
        return island;
    }

    /**
     * Returns an island for a team by id, creating one if the team doesn't already have an island
     */
    public Island getIslandForTeam(short id) {
        Island island = data.getByOwningTeam(id);
        if (island == null) {
            island = data.createTeamOwnedIsland(id);
        }
        return island;
    }

    public static class IslandsWorldSavedData extends WorldSavedData {
        private static final int COMPOUND_TYPE = 10;
        private static final String DATA_NAME = MODID + "_IslandData";
        private final HashMap<UUID, Island> islands = new HashMap<>();
        private int index = 0;

        public IslandsWorldSavedData() {
            super(DATA_NAME);
        }

        @SuppressWarnings("unused")
        public IslandsWorldSavedData(String s) {
            super(s);
        }

        public static IslandsWorldSavedData get(World world) {
            MapStorage storage = world.getPerWorldStorage();
            IslandsWorldSavedData instance = (IslandsWorldSavedData) storage.getOrLoadData(IslandsWorldSavedData.class, DATA_NAME);

            if (instance == null) {
                instance = new IslandsWorldSavedData();
                storage.setData(DATA_NAME, instance);
            }
            return instance;
        }

        @Nullable
        public Island getByOwningPlayer(UUID id) {
            return islands.values().stream().filter(i -> i.getOwningPlayer().equals(id)).findFirst().orElse(null);
        }

        @Nullable
        public Island getByOwningTeam(short id) {
            return islands.values().stream().filter(i -> i.getOwningTeam() == id).findFirst().orElse(null);
        }

        public Island createPlayerOwnedIsland(UUID id) {
            BlockPos teleport = Util.islandTeleportLocation(index);
            Island island = new Island(index++, teleport);
            island.setOwningPlayer(id);
            islands.put(island.getUuid(), island);
            this.markDirty();
            return island;
        }

        public Island createTeamOwnedIsland(short id) {
            BlockPos teleport = Util.islandTeleportLocation(index);
            Island island = new Island(index++, teleport);
            island.setOwningTeam(id);
            islands.put(island.getUuid(), island);
            this.markDirty();
            return island;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            index = nbt.getInteger("index");
            islands.clear();
            NBTTagList list = nbt.getTagList("islands", COMPOUND_TYPE);
            for (NBTBase i : list) {
                Island island = new Island();
                island.deserializeNBT((NBTTagCompound) i);
                islands.put(island.getUuid(), island);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setInteger("index", index);
            NBTTagList islandList = new NBTTagList();
            for (Island island : islands.values()) {
                islandList.appendTag(island.serializeNBT());
            }
            compound.setTag("islands", islandList);
            return compound;
        }
    }
}
