package me.duckdoom5.RpgEssentials.customblocks;

import me.duckdoom5.RpgEssentials.RpgEssentials;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.material.PoweredRail;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.sound.SoundEffect;

public class StorageCartDetectorRail extends Rail {
    private final RpgEssentials plugin;

    public StorageCartDetectorRail(RpgEssentials plugin, String name,
            int[] textureids) {
        super(plugin, 66, true, name, textureids);
        this.plugin = plugin;
    }

    @Override
    public void onEntityMoveAt(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof StorageMinecart) {
            final StorageMinecart minecart = (StorageMinecart) entity;
            final Block block = world.getBlockAt(x, y, z);
            final PoweredRail poweredRail = (PoweredRail) block.getState().getData();
            poweredRail.setPowered(true);
            block.setData(poweredRail.getData(), true);
            SpoutManager.getSoundManager().playGlobalSoundEffect(
                    SoundEffect.CLICK, new Location(world, x, y, z));
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            final PoweredRail poweredRail = (PoweredRail) block.getState().getData();
                            poweredRail.setPowered(false);
                            block.setData(poweredRail.getData(), true);
                        }
                    }, 20L);
        }
    }

    @Override
    public void onNeighborBlockChange(org.bukkit.World world, int x, int y,
            int z, int changedId) {

    }

    @Override
    public void onBlockPlace(org.bukkit.World world, int x, int y, int z,
            org.bukkit.entity.LivingEntity living) {

    }
}
