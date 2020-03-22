package wtf.choco.relics.runnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import wtf.choco.relics.Relics;
import wtf.choco.relics.api.ArtifactManager;
import wtf.choco.relics.api.artifact.Artifact;

public class ArtifactEffectRunnable extends BukkitRunnable {

    private static final Random RANDOM = new Random();

    private static ArtifactEffectRunnable instance;

    private int partialTick;

    private final Relics plugin;
    private final Map<Item, Artifact> itemEntities = new HashMap<>();

    private ArtifactEffectRunnable(Relics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ArtifactManager manager = plugin.getArtifactManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) {
                    continue;
                }

                Artifact artifact = manager.getArtifact(item);
                if (artifact == null) {
                    continue;
                }

                artifact.tick(player, item, i, RANDOM, partialTick);
            }
        }

        Iterator<Entry<Item, Artifact>> itemEntityIterator = itemEntities.entrySet().iterator();
        while (itemEntityIterator.hasNext()) {
            Entry<Item, Artifact> entry = itemEntityIterator.next();

            Item item = entry.getKey();
            if (!item.isValid() || item.isDead()) {
                itemEntityIterator.remove();
                continue;
            }

            entry.getValue().worldTick(item, partialTick);
        }

        // Reset partial tick back to 0 after 20 whole ticks
        if (++partialTick == 20) {
            this.partialTick = 0;
        }
    }

    public void trackArtifactItem(Item item, Artifact artifact) {
        this.itemEntities.put(item, artifact);
    }

    public void clearArtifactEntities() {
        this.itemEntities.clear();
    }

    public static ArtifactEffectRunnable runTask(Relics plugin) {
        Preconditions.checkArgument(instance == null, "Obelisk effect runnable has already been scheduled");

        instance = new ArtifactEffectRunnable(plugin);
        instance.runTaskTimer(plugin, 0L, 1L);
        return instance;
    }

}
