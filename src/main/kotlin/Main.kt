import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.Delegates

class Main: JavaPlugin(), Listener {
    lateinit var titles: List<String>
    lateinit var bossBar: BossBar

    override fun onEnable() {
        saveResource("config.yml", false)

        titles = config.getStringList("titles")
        bossBar = Bukkit.createBossBar(
            titles[0],
            BarColor.values()[config.getInt("color")],
            BarStyle.SEGMENTED_10
        )

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer)
        bossBar.setVisible(true)
        Bukkit.getScheduler().runTaskTimer(this, startChange(), 0, 20)
    }

    private fun startChange(): Runnable {
        var index: Int = 1
        var progress = 0.0
        return Runnable {
            if (progress <= 0) {
                if (index > titles.size - 1) index = 0
                bossBar.setTitle(titles[index])
                progress = 1.0
                index++
            }
            bossBar.progress = progress
            progress -= 0.1
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = bossBar.addPlayer(event.player)

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) = bossBar.removePlayer(event.player)

    override fun onDisable() {
        bossBar.removeAll()
        bossBar.isVisible = false
    }
}