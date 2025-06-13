package me.twojnick.axospawncore;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AxoSpawnCore extends JavaPlugin implements CommandExecutor {

    private Location spawnLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSpawnLocation();

        getCommand("spawn").setExecutor(this);
        getCommand("setspawn").setExecutor(this);

        getLogger().info("Plugin AxoSpawnCore włączony!");
    }

    @Override
    public void onDisable() {
        saveSpawnLocation();
        getLogger().info("Plugin AxoSpawnCore wyłączony!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTylko gracze mogą używać tej komendy!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("spawn")) {
            if (spawnLocation == null) {
                player.sendMessage("§cSpawn nie został ustawiony! Użyj /setspawn, aby go ustawić.");
                return true;
            }

            new BukkitRunnable() {
                int seconds = 5;

                @Override
                public void run() {
                    if (seconds > 0) {
                        String secondText = seconds == 1 ? "sekundę" : "sekund";
                        // Wyświetlanie tytułu na ekranie
                        player.sendTitle("§c AXO §fSPAWN",
                                "§5Teleportacja na spawn §5za §f" + seconds + " §5" + secondText,
                                0, 25, 5); // fadeIn: 0, stay: 25 ticków (~1.25s), fadeOut: 5 ticków
                        seconds--;
                    } else {
                        player.teleport(spawnLocation);
                        // Po teleportacji pokazujemy potwierdzenie na ekranie
                        player.sendTitle("§aTeleportowano!",
                                "§aZostałeś przeteleportowany na spawn!",
                                10, 40, 10); // fadeIn: 10, stay: 40 ticków (~2s), fadeOut: 10
                        cancel();
                    }
                }
            }.runTaskTimer(this, 0L, 20L); // 20 ticków = 1 sekunda

            return true;
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("axospawncore.setspawn")) {
                player.sendMessage("§cNie masz permisji do tej komendy!");
                return true;
            }

            spawnLocation = player.getLocation();
            saveSpawnLocation();
            player.sendMessage("§aSpawn został ustawiony w Twojej aktualnej lokalizacji!");
            return true;
        }

        return false;
    }

    private void loadSpawnLocation() {
        FileConfiguration config = getConfig();
        if (config.contains("spawn")) {
            spawnLocation = (Location) config.get("spawn");
        }
    }

    private void saveSpawnLocation() {
        FileConfiguration config = getConfig();
        if (spawnLocation != null) {
            config.set("spawn", spawnLocation);
        } else {
            config.set("spawn", null);
        }
        saveConfig();
    }
}
