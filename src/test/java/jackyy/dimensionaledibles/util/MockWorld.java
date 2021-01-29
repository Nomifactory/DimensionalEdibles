package jackyy.dimensionaledibles.util;

import net.minecraft.profiler.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.storage.*;

class MockWorld extends World
{
    protected MockWorld(ISaveHandler saveHandlerIn,
                        WorldInfo info,
                        WorldProvider providerIn,
                        Profiler profilerIn, boolean client)
    {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Override
    protected IChunkProvider createChunkProvider()
    {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return false;
    }
}
