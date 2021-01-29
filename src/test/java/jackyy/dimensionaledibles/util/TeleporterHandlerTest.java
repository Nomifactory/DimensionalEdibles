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

    final MockWorld solidBlockWorld = new MockWorld(null, null, new MockHellWorldProvider(), null, false)
    {
        final IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
        final IBlockState netherrack = Blocks.NETHERRACK.getDefaultState();
        final IBlockState air = Blocks.AIR.getDefaultState();

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
                return netherrack;
            if(pos.getY() > 128)
                return air;
            else // y = 128
                return bedrock;
        }
    };

    @Test
    public void getValidYSpawnPos_spawns_players_above_the_bottom_of_the_map()
    {
        BlockPos result = TeleporterHandler.getValidYSpawnPos(solidBlockWorld, BlockPos.ORIGIN);

        assertThat("Specified Y places player under the map", result.getY(), greaterThan(4));
    }
}