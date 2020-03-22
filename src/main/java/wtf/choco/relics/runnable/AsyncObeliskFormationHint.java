package wtf.choco.relics.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import wtf.choco.relics.Relics;
import wtf.choco.relics.api.ObeliskManager;
import wtf.choco.relics.api.obelisk.Obelisk;
import wtf.choco.relics.api.obelisk.ObeliskStructure;

public class AsyncObeliskFormationHint extends BukkitRunnable {

    private static AsyncObeliskFormationHint instance;

    private final Relics plugin;

    public AsyncObeliskFormationHint(Relics plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ObeliskManager obeliskManager = plugin.getObeliskManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Block targetBlock = player.getTargetBlock(null, 4);
            Location targetLocation = targetBlock.getLocation();

            if (obeliskManager.getObelisk(targetBlock, true) != null) {
                continue; // Ignore obelisks that have already been created
            }

            for (Obelisk obelisk : plugin.getObeliskManager().getRegisteredObelisks()) {
                ObeliskStructure structure = obelisk.getStructure();
                Location obeliskLocation = targetLocation.clone().subtract(structure.getFormationX(), structure.getFormationY(), structure.getFormationZ());

                if (!structure.matches(obeliskLocation)) {
                    continue;
                }

                double increment = (1 / 3.0);
                for (double x = 0.0; x <= 1.0; x += increment) {
                    for (double y = 0.0; y <= 1.0; y += increment) {
                        for (double z = 0.0; z <= 1.0; z += increment) {
                            int components = 0;

                            if (x <= 0.0 || x >= 1.0) {
                                components++;
                            }
                            if (y <= 0.0 || y >= 1.0) {
                                components++;
                            }
                            if (z <= 0.0 || z >= 1.0) {
                                components++;
                            }

                            if (components >= 2) {
                                targetLocation.add(x, y, z);
                                player.spawnParticle(Particle.FLAME, targetLocation, 1, 0, 0, 0, 0);
                                targetLocation.subtract(x, y, z);
                            }
                        }
                    }
                }

                break;
            }
        }
    }

    public static AsyncObeliskFormationHint runTask(Relics plugin) {
        if (instance == null) {
            instance = new AsyncObeliskFormationHint(plugin);
            instance.runTaskTimerAsynchronously(plugin, 0L, 20L);
        }

        return instance;
    }

}
