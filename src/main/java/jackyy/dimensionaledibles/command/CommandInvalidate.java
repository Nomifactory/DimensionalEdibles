package jackyy.dimensionaledibles.command;

import jackyy.dimensionaledibles.registry.ModConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

import static jackyy.dimensionaledibles.util.TeleporterHandler.getModNBTData;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandInvalidate extends CommandBase
{
    @Override
    public String getName() {
        return "invalidate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimensionaledibles.command.invalidate.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return ModConfig.general.operatorInvalidationLevel;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server,
                                          ICommandSender sender,
                                          String[] args,
                                          @Nullable BlockPos targetPos)
    {
        if(args.length == 1) {
            String[] playerNames = server.getOnlinePlayerNames();
            return getListOfStringsMatchingLastWord(args, playerNames);
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server,
                        ICommandSender sender,
                        String[] args) throws CommandException
    {
        if(args.length != 2)
            throw new WrongUsageException("dimensionaledibles.command.invalidate.usage");

        String playerName = args[0];
        String dimension  = args[1];
        //Will display MC player not found error if the player does not exist
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        NBTTagCompound dimensionCache = getModNBTData(player);
        if(dimensionCache.hasKey(dimension)) {
            dimensionCache.removeTag(dimension);
            sender.sendMessage(successMessage());
        } else
            sender.sendMessage(failMessage());
    }

    private ITextComponent successMessage() {
        return new TextComponentTranslation("dimensionaledibles.command.invalidate.success")
                .setStyle(new Style().setColor(TextFormatting.GREEN));
    }

    private ITextComponent failMessage() {
        return new TextComponentTranslation("dimensionaledibles.command.invalidate.failed")
                .setStyle(new Style().setColor(TextFormatting.RED));
    }
}
