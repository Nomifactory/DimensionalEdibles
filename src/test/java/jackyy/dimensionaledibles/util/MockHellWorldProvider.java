package jackyy.dimensionaledibles.util;

import net.minecraft.world.*;

class MockHellWorldProvider extends WorldProvider
{
    MockHellWorldProvider()
    {
        nether = true;
        this.setDimension(-1);
    }

    @Override
    public DimensionType getDimensionType()
    {
        return DimensionType.NETHER;
    }
}
