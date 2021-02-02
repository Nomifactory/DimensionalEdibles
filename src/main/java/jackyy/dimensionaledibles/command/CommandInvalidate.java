package jackyy.dimensionaledibles.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import jackyy.dimensionaledibles.registry.ModConfig;

import static jackyy.dimensionaledibles.util.TeleporterHandler.getModNBTData;

public class CommandInvalidate extends CommandBase {

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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        try {
            String playerName = args[0];
            EntityPlayerMP player = getPlayer(server, sender, playerName);
            NBTTagCompound dimensionCache = getModNBTData(player);
            if(dimensionCache.hasKey(args[1])) {
                dimensionCache.removeTag(args[1]);
                sender.sendMessage(new TextComponentTranslation("dimensionaledibles.command.invalidate.success").setStyle(new Style().setColor(TextFormatting.GREEN)));
            }
        }
        catch (IndexOutOfBoundsException exception) {
            sender.sendMessage(new TextComponentTranslation("dimensionaledibles.command.invalidate.failed").setStyle(new Style().setColor(TextFormatting.RED)));
        }


    }
}
