package me.SMoney.com;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Main extends JavaPlugin implements Listener {

	public static Economy econ = null;
	public static EconomyResponse r;
	public String Prefix = getConfig().getString("Prefix");

	public void onEnable() {
		setupEconomy();
		if (!setupEconomy() ) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player){
			Firework f = (Firework) e.getEntity().getPlayer().getWorld().spawn( e.getEntity().getPlayer().getLocation(), Firework.class);
            
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                            .flicker(false)
                            .trail(true)
                            .with(Type.CREEPER)
                            .withColor(Color.GREEN)
                            .withFade(Color.BLUE)
                            .build());
            fm.setPower(3);
            f.setFireworkMeta(fm);
			Player p = e.getEntity();
			Player killer = p.getKiller();
			r = econ.depositPlayer(killer.getName(), getConfig().getInt("KillerRecieveMoney"));

			if (r.transactionSuccess()) {
				killer.sendMessage(Prefix + getConfig().getString("KillerMessage"));
				p.sendMessage(Prefix + getConfig().getString("VictimMessage"));

				return;
			}
			else {
				p.sendMessage(Prefix + ChatColor.RED + " An error occured when paying out your money. Please contact a server admin.");

			}
		}
	}
}