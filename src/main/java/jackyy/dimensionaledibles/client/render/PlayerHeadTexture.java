package jackyy.dimensionaledibles.client.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class PlayerHeadTexture {
    private ResourceLocation resourcelocation;

    public void bruh(GameProfile profile) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
        if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
        {
            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        }
        else
        {
            UUID uuid = EntityPlayer.getUUID(profile);
            resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
        }
    }
}
