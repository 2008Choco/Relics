package wtf.choco.artifice.listeners.artifact;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import wtf.choco.artifice.Artifice;

public class ArtifactProtectionListener implements Listener {

    private final Artifice plugin;

    public ArtifactProtectionListener(Artifice plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEatItem(PlayerItemConsumeEvent event) {
        if (plugin.getArtifactManager().getArtifact(event.getItem()) == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (plugin.getArtifactManager().getArtifact(event.getEntity().getItemStack()) == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void placeArtifact(BlockPlaceEvent event) {
        if (plugin.getArtifactManager().getArtifact(event.getItemInHand()) == null) {
            return;
        }

        event.setCancelled(true);
    }
}
