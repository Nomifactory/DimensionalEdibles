package jackyy.dimensionaledibles.command;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.List;

public class DimensionalEdiblesCommand extends CommandTreeBase {

    public DimensionalEdiblesCommand() {
        addSubcommand(new CommandInvalidate());
    }

    @Override
    public String getName() {
        return "dimensionalEdibles";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("de");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimensionaledibles.command.usage";
    }
}
