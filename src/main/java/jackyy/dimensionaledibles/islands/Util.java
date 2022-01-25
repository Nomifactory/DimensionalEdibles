package jackyy.dimensionaledibles.islands;

import jackyy.dimensionaledibles.registry.ModConfig;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;


class Util {
    //slightly modified from https://stackoverflow.com/a/45333503/6543961
    private static Pair<Integer, Integer> getXZSpiral(int i) {
        if (i == 0) {
            return Pair.of(0, 0);
        }

        int x = 0, y = 0;
        int dx = 0, dy = 1;
        int segment_length = 1, segment_passed = 0;

        for (int n = 0; n < i; ++n) {
            x += dx;
            y += dy;
            ++segment_passed;

            if (segment_passed == segment_length) {
                segment_passed = 0;

                int buffer = dy;
                dy = -dx;
                dx = buffer;

                if (dx == 0) {
                    ++segment_length;
                }
            }
        }

        return Pair.of(x, y);
    }

    public static BlockPos islandTeleportLocation(int i) {
        Pair<Integer, Integer> xz = getXZSpiral(i);
        int x = 256 + xz.getLeft() * ModConfig.tweaks.islandCake.islandGapRegions * 512;
        int z = 256 + xz.getRight() * ModConfig.tweaks.islandCake.islandGapRegions * 512;

        return new BlockPos(x, ModConfig.tweaks.islandCake.yLevel, z);
    }
}
