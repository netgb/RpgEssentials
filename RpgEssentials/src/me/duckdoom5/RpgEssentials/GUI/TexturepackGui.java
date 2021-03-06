package me.duckdoom5.RpgEssentials.GUI;

import java.util.Set;

import me.duckdoom5.RpgEssentials.RpgEssentials;
import me.duckdoom5.RpgEssentials.RpgeManager;
import me.duckdoom5.RpgEssentials.Entity.RpgPlayer;
import me.duckdoom5.RpgEssentials.config.Configuration;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class TexturepackGui extends Gui {
    private final GenericButton cancel = (GenericButton) new GenericButton(
            "Cancel").setWidth(100).setHeight(20).shiftYPos(-20).shiftXPos(-100).setAnchor(WidgetAnchor.BOTTOM_CENTER);
    private final GenericButton save = (GenericButton) new GenericButton("Save").setWidth(100).setHeight(20).shiftYPos(-20).setAnchor(WidgetAnchor.BOTTOM_CENTER);

    private final WidgetAnchor anchor = WidgetAnchor.TOP_CENTER;
    private final GenericLabel label = (GenericLabel) new GenericLabel().setText("Options").setHeight(15).shiftXPos(-15).setAnchor(anchor);
    private GenericListWidget list;
    private final World world;

    public TexturepackGui(RpgEssentials plugin, SpoutPlayer splayer) {
        super(plugin, splayer);
        world = splayer.getWorld();

        final Gui gui = GuiManager.gui.get(splayer);
        if (gui == null) {
            popup = new GenericPopup();
            createPopup(true, false);
        } else {
            popup = gui.getPopup();
            createPopup(false, true);
        }

        GuiManager.gui.put(splayer, this);
    }

    @Override
    protected void createPopup(boolean attach, boolean remove) {
        System.out.println(remove + ", " + attach);
        if (remove) {
            popup.removeWidgets(plugin);
        }

        list = new GenericListWidget();
        list.setAnchor(WidgetAnchor.CENTER_CENTER).setWidth(400).setHeight(200).shiftXPos(-200).shiftYPos(-100);

        if (RpgEssentials.hasPermission(splayer, "rpgessentials.rpg.texturepack.playerschoise")) {
            list.addItem(new ListWidgetItem(ChatColor.YELLOW + "Player's Choice", "", ""));
        }

        if (Configuration.texture.contains(world.getName())) {
            final ConfigurationSection section = Configuration.texture.getConfigurationSection(world.getName());
            final Set<String> keys = section.getKeys(false);

            for (final String name : keys) {
                list.addItem(new ListWidgetItem(name, "", Configuration.texture.getString(world.getName() + "." + name + ".icon")));
            }
        } else {
            splayer.sendNotification("No texture packs found", "for this world!", Material.APPLE);
        }

        popup.attachWidget(plugin, label).attachWidget(plugin, cancel).attachWidget(plugin, save).attachWidget(plugin, list);

        if (attach) {
            GuiManager.close(splayer);
            GuiManager.attach(splayer, popup, plugin);
        }
    }

    @SuppressWarnings ("unused")
    @Override
    public void back() {
        new PlayerOptionsGui((RpgEssentials) plugin, splayer);
    }

    @SuppressWarnings ("unused")
    @Override
    public void save() {
        final RpgPlayer rpgplayer = RpgeManager.getInstance().getRpgPlayerManager().getRpgPlayer(splayer);
        if (list.getSelectedItem() == null) {
            splayer.sendNotification("Error!", "Select a texture pack!",
                    Material.APPLE);
            return;
        }
        final String title = list.getSelectedItem().getTitle();
        if (title.equals(ChatColor.YELLOW + "Player's Choice")) {
            splayer.sendNotification("Texture pack removed!", "",
                    Material.GOLDEN_APPLE);
            splayer.resetTexturePack();
            rpgplayer.setTexturepack(world, "none");
        } else {
            splayer.sendNotification("Texture pack selected!", "Downloading...", Material.GOLDEN_APPLE);
            splayer.setTexturePack(Configuration.texture.getString(world.getName() + "." + title + ".url"));
            rpgplayer.setTexturepack(world, title);
        }
        new PlayerOptionsGui((RpgEssentials) plugin, splayer);
    }
}
