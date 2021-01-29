package jackyy.dimensionaledibles.util;

import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import org.junit.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.number.OrderingComparison.*;

public class TeleporterHandlerTest
{
    @BeforeClass
    public static void bootStrap()
    {
        Bootstrap.register();
    }

    final IBlockState bedrock    = Blocks.BEDROCK.getDefaultState();
    final IBlockState netherrack = Blocks.NETHERRACK.getDefaultState();
    final IBlockState lava       = Blocks.LAVA.getDefaultState();
    final IBlockState air        = Blocks.AIR.getDefaultState();

    final MockWorld solidBlockWorld = new MockWorld(null, null, new MockHellWorldProvider(), null, false)
    {
        /*
            ...
            Y>=129 Air
            Y=128 Bedrock
            Y=127 Netherrack
            ...
            Y=5 Netherrack
            Y=4 Bedrock
            ...
            Y=0 Bedrock
            Y<=-1 Air
            ...
         */
        @Override
        public IBlockState getBlockState(BlockPos pos)
        {
            if(pos.getY() < 0)
                return air;
            if(pos.getY() <= 4)
                return bedrock;
            if(pos.getY() < 128)
                if(pos.getX() > 3)
                    return air;
                else
                    return netherrack;
            if(pos.getY() > 128)
                return air;
            else // y = 128
                return bedrock;
        }
    };

    final MockWorld lavaWorld = new MockWorld(null, null, new MockHellWorldProvider(), null, false)
    {
        /*
            ...
            Y>=129 Air
            Y=128 Bedrock
            Y=127 Air
            Y=126 Air
            Y=125 Netherrack
            Y=124 Lava
            ...
            Y=5 Lava
            Y=4 Bedrock
            ...
            Y=0 Bedrock
            Y<=-1 Air
            ...
         */
        @Override
        public IBlockState getBlockState(BlockPos pos)
        {
            if(pos.getY() > 128)
                return air;
            else if(pos.getY() == 128)
                return bedrock;
            else if(pos.getY() > 125)
                return air;
            else if(pos.getY() == 125)
                return netherrack;
            else if(pos.getY() <= 4)
                return bedrock;
            else if(pos.getY() < 0)
                return air;
            else
                return lava;
        }
    };

    @Test
    public void getValidYSpawnPos_spawns_players_above_the_bottom_of_the_map()
    {
        BlockPos result = TeleporterHandler.getValidYSpawnPos(solidBlockWorld, BlockPos.ORIGIN);

        assertThat("Specified Y places player under the map", result.getY(), greaterThan(4));
    }

    @Test
    public void getValidYSpawnPos_will_not_spawn_players_in_lava()
    {
        BlockPos result = TeleporterHandler.getValidYSpawnPos(lavaWorld, BlockPos.ORIGIN);

        assertThat("Specified Y places player in lava", result.getY(), greaterThan(124));
    }
}