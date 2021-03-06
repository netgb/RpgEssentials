package me.duckdoom5.RpgEssentials;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.duckdoom5.RpgEssentials.config.Configuration;
import me.duckdoom5.RpgEssentials.config.MyConfiguration;
import me.duckdoom5.RpgEssentials.customblocks.CustomBlock;
import me.duckdoom5.RpgEssentials.customblocks.CustomOre;
import me.duckdoom5.RpgEssentials.customblocks.CustomStair;
import me.duckdoom5.RpgEssentials.customblocks.CustomStairEdge;
import me.duckdoom5.RpgEssentials.customblocks.OriginalOre;
import me.duckdoom5.RpgEssentials.customblocks.RegisterBlock;
import me.duckdoom5.RpgEssentials.customblocks.SafeBlock;
import me.duckdoom5.RpgEssentials.items.CustomItems;
import me.duckdoom5.RpgEssentials.items.food.CustomFood;
import me.duckdoom5.RpgEssentials.items.tools.CustomTool;
import me.duckdoom5.RpgEssentials.util.AddonType;
import me.duckdoom5.RpgEssentials.util.BlockType;
import me.duckdoom5.RpgEssentials.util.ItemType;
import me.duckdoom5.RpgEssentials.util.MaterialType;
import me.duckdoom5.RpgEssentials.util.ToolType;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.material.item.GenericCustomTool;

public class MaterialManager {

    private final RpgEssentials plugin;

    private final HashMap<GenericCustomBlock, BlockType> blocks = new LinkedHashMap<>();
    private final HashMap<GenericCustomItem, ItemType> items = new LinkedHashMap<>();
    private final HashMap<org.getspout.spoutapi.material.Material, MaterialType> materials = new LinkedHashMap<>();

    public static Set<OriginalOre> originalores = new LinkedHashSet<>();

    public MaterialManager(RpgEssentials plugin) {
        this.plugin = plugin;
        registerBlocks();
    }

    public Set<GenericCustomItem> getItems() {
        return items.keySet();
    }

    public GenericCustomItem getItemByName(String name) {
        for (final GenericCustomItem item : items.keySet()) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }

    public Set<GenericCustomBlock> getBlocks() {
        return blocks.keySet();
    }

    public GenericCustomBlock getBlockByName(String name) {
        for (final GenericCustomBlock block : blocks.keySet()) {
            if (block.getName().equals(name))
                return block;
        }
        return null;
    }

    public boolean hasMaterial(org.getspout.spoutapi.material.Material material) {
        if (materials.containsKey(material))
            return true;

        return false;
    }

    public boolean hasMaterial(String name) {
        for (final org.getspout.spoutapi.material.Material material : materials.keySet()) {
            if (material.getName().equals(name))
                return true;
        }
        return false;
    }

    public Set<org.getspout.spoutapi.material.Material> getMaterials() {
        return materials.keySet();
    }

    public org.getspout.spoutapi.material.Material getMaterialByName(String name) {
        for (final org.getspout.spoutapi.material.Material material : materials.keySet()) {
            if (material.getName().equals(name))
                return material;
        }
        return null;
    }

    public Set<GenericCustomBlock> getStairs() {
        final Set<GenericCustomBlock> stair = new HashSet<>();
        for (final GenericCustomBlock block : blocks.keySet()) {
            if (blocks.get(block).equals(BlockType.STAIR)) {
                stair.add(block);
            }
        }
        return stair;
    }

    // BLOCKS

    // default

    private void CustomBlock() {
        final ConfigurationSection section = Configuration.block.getConfigurationSection("Custom Blocks");
        final Iterator<?> keys = section.getKeys(false).iterator();
        while (keys.hasNext()) {
            final String name = (String) keys.next();
            final String textureIds = Configuration.block.getString("Custom Blocks." + name + ".textureIds", "0,0,0,0,0,0");
            final int light = Configuration.block.getInt("Custom Blocks." + name + ".lightlevel", 0);

            final String design = Configuration.block.getString("Custom Blocks." + name + ".design", "block");
            final float brightness = (float) Configuration.block.getDouble("Custom Blocks." + name + ".brightness", 0.0D);
            final String textureurl = Configuration.block.getString("Custom Blocks." + name + ".textureurl");
            final int texturesize = Configuration.block.getInt("Custom Blocks." + name + ".texturesize", 16);
            final int base = Configuration.block.getInt("Custom Blocks." + name + ".base", 1);
            final boolean canRotate = Configuration.block.getBoolean("Custom Blocks." + name + ".rotate", false);
            final boolean opaque = Configuration.block.getBoolean("Custom Blocks." + name + ".opaque", false);

            final HashMap<Integer, SubTexture> subtex = new HashMap<>();
            if (Configuration.block.contains("Custom Blocks." + name + ".customtexturecoords")) {
                final List<String> coordslist = Configuration.block.getStringList("Custom Blocks." + name + ".customtexturecoords");
                try {
                    final Iterator<String> keys2 = coordslist.iterator();
                    int id = 0;
                    while (keys2.hasNext()) {
                        final String[] coords = keys2.next().toString().split(" ");
                        final Texture texture = new Texture(plugin, textureurl, 16 * texturesize, 16 * texturesize, texturesize);
                        subtex.put(id, new SubTexture(texture, Integer.parseInt(coords[0]), texture.height - (Integer.parseInt(coords[1]) + Integer.parseInt(coords[3])), Integer.parseInt(coords[2]), Integer.parseInt(coords[3])));
                        id++;
                    }
                } catch (final Exception e) {
                    plugin.getLogger().severe("Error on coords of custom block: " + name);
                }
            }

            final String[] k = textureIds.split(",");
            final int[] ids = new int[k.length];

            for (int i = 0; i < k.length; i++) {
                ids[i] = Integer.parseInt(k[i]);
            }

            if (design.equalsIgnoreCase("block")) {
                addblock(plugin, name, ids, opaque, light, base, canRotate);
            } else if (design.equalsIgnoreCase("stair")) {
                addStair(plugin, name, ids);
            } else if (design.equalsIgnoreCase("stairedge")) {
                addStairEdge(plugin, name, ids);

            } else {
                final File file = new File("plugins/RpgEssentials/Designs/" + design + ".yml");
                if (file.exists()) {
                    final MyConfiguration config = MyConfiguration.loadConfiguration(file);

                    final GenericBlockDesign blockdesign = addDesign(design, plugin, config, ids, textureurl, texturesize, subtex);
                    blockdesign.setBrightness(brightness);

                    boolean custom = true;

                    for (final BlockType blocktype : BlockType.values()) {
                        if (blocktype.toString().replace("_", " ").equalsIgnoreCase(design)) {
                            addblock(plugin, name, light, canRotate, blockdesign, BlockType.valueOf(design.toUpperCase().replace(" ", "_")));
                            custom = false;
                        }
                    }
                    if (custom) {
                        addblock(plugin, name, light, canRotate, blockdesign, BlockType.CUSTOM);
                    }
                } else {
                    plugin.getLogger().warning("!!! " + name + " has no design. Please use: block/slab/stair/stairedge/bush/flower !!!");
                }
            }
        }
    }

    public void addblock(RpgEssentials plugin, String name, int[] ids, boolean opaque, int light, int base, boolean canRotate) {
        final CustomBlock block = new CustomBlock(plugin, name, ids, opaque, light, base, canRotate);
        blocks.put(block, BlockType.DEFAULT);
        materials.put(block, MaterialType.BLOCK);
    }

    public void addblock(RpgEssentials plugin, String name, int base, boolean canRotate, GenericBlockDesign design, BlockType type) {
        final CustomBlock block = new CustomBlock(plugin, name, base, canRotate, design);
        blocks.put(block, type);
        materials.put(block, MaterialType.valueOf(type.toString()));
    }

    // ores
    private void CustomOres() {
        final ConfigurationSection section = Configuration.block.getConfigurationSection("Custom Ores");
        final Iterator<?> keys = section.getKeys(false).iterator();
        while (keys.hasNext()) {
            final String name = (String) keys.next();
            if (!name.equals("use default config")) {
                final int textureID = Configuration.block.getInt("Custom Ores." + name + ".textureID");
                final int freq = Configuration.block.getInt("Custom Ores." + name + ".frequency");
                final int size = Configuration.block.getInt("Custom Ores." + name + ".max vein size");
                final int minY = Configuration.block.getInt("Custom Ores." + name + ".min height");
                final int maxY = Configuration.block.getInt("Custom Ores." + name + ".max height");
    
                float hard = 0;
                int light = 0;
                final int amount = 1;
                int base = 1;
                final org.getspout.spoutapi.material.Material drop = null;
                if (Configuration.block.contains("Custom Ores." + name + ".hardness")) {
                    hard = (float) Configuration.block.getDouble("Custom Ores." + name + ".hardness", 0.0D);
                }
                if (Configuration.block.contains("Custom Ores." + name + ".lightlevel")) {
                    light = Configuration.block.getInt("Custom Ores." + name + ".lightlevel", 0);
                }
                if (Configuration.block.contains("Custom Ores." + name + ".friction")) {
                }
                if (Configuration.block.contains("Custom Ores." + name + ".base")) {
                    base = Configuration.block.getInt("Custom Ores." + name + ".base", 1);
                }
                addOre(plugin, name, textureID, freq, minY, maxY, size, hard, light, drop, amount, base);
                /*
                 * lets try without doing this logic yet...
                 * if(Configuration.block.contains("Custom Ores." + name +
                 * ".drop")){ if(Configuration.block.contains("Custom Ores." + name
                 * + ".drop.amount")){ amount =
                 * Configuration.block.getInt("Custom Ores." + name +
                 * ".drop.amount", 1); } String sdrop =
                 * Configuration.block.getString("Custom Ores." + name + ".drop");
                 * try{ mdrop = Material.getMaterial(Integer.parseInt(sdrop));
                 * addOre(plugin, name, textureID, freq, minY, maxY, size, hard,
                 * light, friction, mdrop, amount, base);
                 * }catch(NumberFormatException e){
                 * if(materials.containsValue(sdrop)){ drop =
                 * getMaterialByName(sdrop); } } } if(drop != null){ addOre(plugin,
                 * name, textureID, freq, minY, maxY, size, hard, light, friction,
                 * drop, amount, base); }else{ addOre(plugin, name, textureID, freq,
                 * minY, maxY, size, hard, light, friction, mdrop, amount, base); }
                 */
            }
        }
    }

    private void UpdateOres() {
        final ConfigurationSection section = Configuration.block.getConfigurationSection("Custom Ores");
        final Iterator<?> keys = section.getKeys(false).iterator();
        while (keys.hasNext()) {
            final String name = (String) keys.next();
            int amount;
            final CustomOre block = (CustomOre) getMaterialByName(name);
            org.getspout.spoutapi.material.Material drop = null;
            if (Configuration.block.contains("Custom Ores." + name + ".drop")) {
                if (Configuration.block.contains("Custom Ores." + name + ".drop.amount")) {
                    amount = Configuration.block.getInt("Custom Ores." + name + ".drop.amount", 1);
                } else {
                    amount = 1;
                }
                final String sdrop = Configuration.block.getString("Custom Ores." + name + ".drop");
                drop = getMaterialByName(sdrop);
                if (drop != null) {
                    block.setItemDrop(new SpoutItemStack(drop, amount));
                }
            }
        }
    }

    public void addOre(RpgEssentials plugin, String name, int textureID, int freq, int minY, int maxY, int size, float hard, int light, org.getspout.spoutapi.material.Material drop, int amount, int base) {
        plugin.getLogger().info("Added " + name);
        final CustomOre ore = new CustomOre(plugin, name, textureID, freq, minY, maxY, size, drop, hard, light, amount, base);
        blocks.put(ore, BlockType.ORE);
        materials.put(ore, MaterialType.ORE);
    }

    public void addOre(RpgEssentials plugin, String name, int textureID, int freq, int minY, int maxY, int size, float hard, int light, Material drop, int amount, int base) {
        plugin.getLogger().info("Added " + name);
        final CustomOre ore = new CustomOre(plugin, name, textureID, freq, minY, maxY, size, drop, hard, light, amount, base);
        blocks.put(ore, BlockType.ORE);
        materials.put(ore, MaterialType.ORE);
    }

    public Set<GenericCustomBlock> getOres() {
        final Set<GenericCustomBlock> ores = new HashSet<>();
        for (final GenericCustomBlock block : blocks.keySet()) {
            if (blocks.get(block).equals(BlockType.ORE)) {
                ores.add(block);
            }
        }
        return ores;
    }

    // ITEMS

    // Default

    private void CustomItems() {
        final ConfigurationSection section = Configuration.items.getConfigurationSection("Custom Items");
        final Iterator<?> keys = section.getKeys(false).iterator();
        while (keys.hasNext()) {
            final String name = (String) keys.next();
            final String textureurl = Configuration.items.getString("Custom Items." + name + ".texture url");
            final String type = Configuration.items.getString("Custom Items." + name + ".type", "default");
            addItem(plugin, name, textureurl, ItemType.valueOf(type.toUpperCase().replace(" ", "_")));
        }
        final ConfigurationSection section2 = Configuration.items.getConfigurationSection("Custom Tools");
        final Iterator<?> keys2 = section2.getKeys(false).iterator();
        while (keys2.hasNext()) {
            final String name = (String) keys2.next();
            final String textureurl = Configuration.items.getString("Custom Tools." + name + ".texture url");
            final int durability = Configuration.items.getInt("Custom Tools." + name + ".durability");
            final String type = Configuration.items.getString("Custom Tools." + name + ".type", "sword");
            addTool(plugin, name, textureurl, durability, ToolType.valueOf(type.toUpperCase().replace(" ", "_")));
        }
        final ConfigurationSection section3 = Configuration.items.getConfigurationSection("Custom Food");
        final Iterator<?> keys3 = section3.getKeys(false).iterator();
        while (keys3.hasNext()) {
            final String name = (String) keys3.next();
            final String textureurl = Configuration.items.getString("Custom Food." + name + ".texture url");
            final int restore = Configuration.items.getInt("Custom Food." + name + ".restore");
            final int heal = Configuration.items.getInt("Custom Food." + name + ".heal");
            addFood(plugin, name, textureurl, restore, heal);
        }
        final ConfigurationSection section4 = Configuration.items.getConfigurationSection("Custom Fish");
        final Iterator<?> keys4 = section4.getKeys(false).iterator();
        while (keys4.hasNext()) {
            final String name = (String) keys4.next();
            final String textureurl = Configuration.items.getString("Custom Fish." + name + ".texture url");
            final int restore = Configuration.items.getInt("Custom Fish." + name + ".restore");
            final int heal = Configuration.items.getInt("Custom Fish." + name + ".heal");
            addFood(plugin, name, textureurl, restore, heal);
            addFish(name);
        }
    }

    public void addItem(RpgEssentials plugin, String name, String textureurl, ItemType type) {
        final CustomItems item = new CustomItems(plugin, name, textureurl);
        items.put(item, type);
        materials.put(item, type == ItemType.DEFAULT ? MaterialType.ITEM : MaterialType.valueOf(type.toString()));
    }

    public ItemStack getCustomItem(String name) {
        return new ItemStack(Material.FLINT, 1, (short) RpgeManager.getInstance().getMaterialManager().getItemByName(name).getCustomId());
    }

    // Tools

    public void addTool(RpgEssentials plugin, String name, String textureurl, int durability, ToolType type) {
        final CustomTool tool = new CustomTool(plugin, name, textureurl, (short) durability, type);
        items.put(tool, ItemType.TOOL);
        materials.put(tool, MaterialType.TOOL);
    }

    public boolean isTool(GenericCustomItem item) {
        if (item instanceof GenericCustomTool) {
            return true;
        }
        return false;
    }

    public CustomTool getTool(ItemStack itemstack) {
        final SpoutItemStack tool = new SpoutItemStack(itemstack);
        if (tool.isCustomItem()) {
            return (CustomTool) tool.getMaterial();
        }
        return null;
    }

    public Set<GenericCustomItem> getTools() {
        final Set<GenericCustomItem> tools = new HashSet<>();
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.TOOL)) {
                tools.add(item);
            }
        }
        return tools;
    }

    public CustomTool getToolByName(String name) {
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.TOOL)) {
                if (item.getName().equals(name)) {
                    return (CustomTool) item;
                }
            }
        }
        return null;
    }

    // Food

    public void addFood(RpgEssentials plugin, String name, String textureurl, int restore, int heal) {
        final CustomFood food = new CustomFood(plugin, name, textureurl, restore, heal);
        items.put(food, ItemType.FOOD);
        materials.put(food, MaterialType.FOOD);
    }

    public CustomFood getFoodByName(String name) {
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.FOOD)) {
                if (item.getName().equals(name)) {
                    return (CustomFood) item;
                }
            }
        }
        return null;
    }

    public Set<GenericCustomItem> getFood() {
        final Set<GenericCustomItem> food = new HashSet<>();
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.FOOD)) {
                food.add(item);
            }
        }
        return food;
    }

    // fish

    public void addFish(String name) {
        final CustomFood fish = getFoodByName(name);
        items.put(fish, ItemType.FISH);
        materials.put(fish, MaterialType.FISH);
    }

    public CustomFood getFishByName(String name) {
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.FISH)) {
                if (item.getName().equals(name)) {
                    return (CustomFood) item;
                }
            }
        }
        return null;
    }

    public Set<GenericCustomItem> getFish() {
        final Set<GenericCustomItem> fish = new HashSet<>();
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.FISH)) {
                fish.add(item);
            }
        }
        return fish;
    }

    public int getFishSize() {
        final Set<GenericCustomItem> fish = new HashSet<>();
        for (final GenericCustomItem item : items.keySet()) {
            if (items.get(item).equals(ItemType.FISH)) {
                fish.add(item);
            }
        }
        return fish.size();
    }

    // DESIGNS
    private static int quadnumber = 0;
    private static Set<String> names = new LinkedHashSet<>();

    private static GenericBlockDesign addDesign(String designname, RpgEssentials plugin, MyConfiguration config, int[] ids, String textureurl, int texturesize, HashMap<Integer, SubTexture> subtex) {
        try {
            final GenericBlockDesign design = new GenericBlockDesign();

            final String[] boundingbox = config.getString("BoundingBox").split(",");

            design.setBoundingBox(Float.parseFloat(boundingbox[0]), Float.parseFloat(boundingbox[1]), Float.parseFloat(boundingbox[2]), Float.parseFloat(boundingbox[3]), Float.parseFloat(boundingbox[4]), Float.parseFloat(boundingbox[5]));

            final Texture texture = new Texture(plugin, textureurl, 16 * texturesize, 16 * texturesize, texturesize);

            design.setTexture(plugin, texture.getTexture()).setMinBrightness(0F).setMaxBrightness(1F);

            final ConfigurationSection section = config.getConfigurationSection("Shape");
            final Iterator<?> keys = section.getKeys(false).iterator();
            quadnumber = 1;
            names.clear();
            while (keys.hasNext()) {
                final String name = (String) keys.next();
                names.add(name);
                quadnumber++;
            }

            design.setQuadNumber(quadnumber);

            quadnumber = 0;
            for (final String name : names) {
                final List<String> coords = config.getStringList("Shape." + name + ".Coords");

                try {
                    final Iterator<String> keys2 = coords.iterator();
                    final String[] row1 = keys2.next().toString().split(" ");
                    final String[] row2 = keys2.next().toString().split(" ");
                    final String[] row3 = keys2.next().toString().split(" ");
                    final String[] row4 = keys2.next().toString().split(" ");
                    Quad quad = null;
                    final int textureid = config.getInt("Shape." + name + ".TextureId");

                    if (subtex.isEmpty()) {
                        quad = new Quad(quadnumber,
                                texture.getSubTexture(ids[textureid]));
                    } else {
                        quad = new Quad(quadnumber, subtex.get(textureid));
                    }
                    quad.addVertex(0, Float.parseFloat(row1[0]), Float.parseFloat(row1[1]), Float.parseFloat(row1[2]));
                    quad.addVertex(1, Float.parseFloat(row2[0]), Float.parseFloat(row2[1]), Float.parseFloat(row2[2]));
                    quad.addVertex(2, Float.parseFloat(row3[0]), Float.parseFloat(row3[1]), Float.parseFloat(row3[2]));
                    quad.addVertex(3, Float.parseFloat(row4[0]), Float.parseFloat(row4[1]), Float.parseFloat(row4[2]));

                    design.setQuad(quad);
                    quadnumber++;
                } catch (final Exception e) {
                    plugin.getLogger().severe("Error on design : " + designname + " coord: " + name);
                }
            }
            subtex.clear();
            return design;
        } catch (final Exception e) {
            plugin.getLogger().severe("Error on design: " + designname);
            e.printStackTrace();
        }
        subtex.clear();
        return null;
    }

    public void addStair(RpgEssentials plugin, String name, int[] ids) {
        final CustomStair stair = new CustomStair(plugin, name, ids);
        blocks.put(stair, BlockType.STAIR);
        materials.put(stair, MaterialType.STAIR);
    }

    public void addStairEdge(RpgEssentials plugin, String name, int[] ids) {
        final CustomStairEdge stair = new CustomStairEdge(plugin, name, ids);
        blocks.put(stair, BlockType.STAIREDGE);
        materials.put(stair, MaterialType.STAIREDGE);
    }

    private void registerBlocks() {
        // orginal ores
        final OriginalOre CoalOre = new OriginalOre(Material.COAL_ORE, Configuration.block.getInt("Original Ores.Coal Ore.frequency"), Configuration.block.getInt("Original Ores.Coal Ore.max vein size"), Configuration.block.getInt("Original Ores.Coal Ore.min height"), Configuration.block.getInt("Original Ores.Coal Ore.max height"));
        final OriginalOre IronOre = new OriginalOre(Material.IRON_ORE, Configuration.block.getInt("Original Ores.Iron Ore.frequency"), Configuration.block.getInt("Original Ores.Iron Ore.max vein size"), Configuration.block.getInt("Original Ores.Iron Ore.min height"), Configuration.block.getInt("Original Ores.Iron Ore.max height"));
        final OriginalOre LapisOre = new OriginalOre(Material.LAPIS_ORE, Configuration.block.getInt("Original Ores.Lapis Ore.frequency"), Configuration.block.getInt("Original Ores.Gold Ore.max vein size"), Configuration.block.getInt("Original Ores.Lapis Ore.min height"), Configuration.block.getInt("Original Ores.Lapis Ore.max height"));
        final OriginalOre GoldOre = new OriginalOre(Material.GOLD_ORE, Configuration.block.getInt("Original Ores.Gold Ore.frequency"), Configuration.block.getInt("Original Ores.Coal Ore.max vein size"), Configuration.block.getInt("Original Ores.Gold Ore.min height"), Configuration.block.getInt("Original Ores.Gold Ore.max height"));
        final OriginalOre RedstoneOre = new OriginalOre(Material.REDSTONE_ORE, Configuration.block.getInt("Original Ores.Redstone Ore.frequency"), Configuration.block.getInt("Original Ores.Redstone Ore.max vein size"), Configuration.block.getInt("Original Ores.Redstone Ore.min height"), Configuration.block.getInt("Original Ores.Redstone Ore.max height"));
        final OriginalOre DiamondOre = new OriginalOre(Material.DIAMOND_ORE, Configuration.block.getInt("Original Ores.Diamond Ore.frequency"), Configuration.block.getInt("Original Ores.Diamond Ore.max vein size"), Configuration.block.getInt("Original Ores.Diamond Ore.min height"), Configuration.block.getInt("Original Ores.Diamond Ore.max height"));
        originalores.add(CoalOre);
        originalores.add(IronOre);
        originalores.add(LapisOre);
        originalores.add(GoldOre);
        originalores.add(RedstoneOre);
        originalores.add(DiamondOre);

        // Add ores with drop as flint (default)
        CustomOres();
        CustomItems();
        CustomBlock();

        // Update ores to the correct drop information
        UpdateOres();

        if (RpgeManager.getInstance().isAddonEnabled(AddonType.BANKS)) {
            plugin.getLogger().info("!!! safe added !!!");
            final int[] id = { 6, 6, 6, 5, 6, 6 };
            final SafeBlock safeBlock = new SafeBlock(plugin, "Safe", id);
            blocks.put(safeBlock, BlockType.SAFE);
            materials.put(safeBlock, MaterialType.SAFE);
        }

        if (RpgeManager.getInstance().isAddonEnabled(AddonType.STORES)) {
            final RegisterBlock registerBlock = new RegisterBlock(plugin);
            blocks.put(registerBlock, BlockType.REGISTER);
            materials.put(registerBlock, MaterialType.REGISTER);
        }

        /*
         * EmptyCartDetectorRail emptycartdetectorrail = new
         * EmptyCartDetectorRail(plugin, "Empty Cart Detector", new
         * int[]{6,6,6,5,6,6}); blocks.put(emptycartdetectorrail,
         * BlockType.RAILS); materials.put(emptycartdetectorrail,
         * MaterialType.RAILS); StorageCartDetectorRail storagecartdetectorrail
         * = new StorageCartDetectorRail(plugin, "Storage Cart Detector", new
         * int[]{6,6,6,5,6,6}); blocks.put(storagecartdetectorrail,
         * BlockType.RAILS); materials.put(storagecartdetectorrail,
         * MaterialType.RAILS); PoweredMinecartDetectorRail
         * poweredcartdetectorrail = new PoweredMinecartDetectorRail(plugin,
         * "Powered Cart Detector", new int[]{6,6,6,5,6,6});
         * blocks.put(poweredcartdetectorrail, BlockType.RAILS);
         * materials.put(poweredcartdetectorrail, MaterialType.RAILS);
         * LoaderRail loaderrail = new LoaderRail(plugin, "StorageCart Loader",
         * new int[]{6,6,6,5,6,6}); blocks.put(loaderrail, BlockType.RAILS);
         * materials.put(loaderrail, MaterialType.RAILS); UnloaderRail
         * unloaderrail = new UnloaderRail(plugin, "StorageCart Unloader", new
         * int[]{6,6,6,5,6,6}); blocks.put(unloaderrail, BlockType.RAILS);
         * materials.put(unloaderrail, MaterialType.RAILS);
         */
    }
}
