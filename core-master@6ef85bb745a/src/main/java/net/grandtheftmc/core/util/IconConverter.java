package net.grandtheftmc.core.util;

import java.util.HashMap;
import java.util.Map;

public class IconConverter {

    private static final String[] CHARACTER_NAME_LIST;
    private static final Map<String, Character> CHARACTER_NAME_MAP;
    private static final int UNICODE_OFFSET = 36864;
    private static boolean escape;

    static {
        CHARACTER_NAME_LIST = new String[]{
                "woodensword", "woodenpickaxe", "woodenshovel", "woodenaxe", "woodenhoe",
                "stonesword", "stone pickaxe", "stoneshovel", "stoneaxe", "stonehoe",
                "goldensword", "goldenpickaxe", "goldenshovel", "goldenaxe", "goldenhoe", "unused",

                "ironsword", "ironpickaxe", "ironshovel", "ironaxe", "ironhoe",
                "diamondsword", "diamondpickaxe", "diamondshovel", "diamondaxe", "diamond hoe",
                "bow", "bow1", "bow2", "bow3", "arrow", "quiver",

                "head", "body", "legs", "boots",
                "whiteleathercap", "whiteleathertunic", "whiteleatherpants", "whiteleatherboots",
                "blankleathercap", "blankleathertunic", "blankleatherpants", "blankleatherboots",
                "leathercap", "leathertunic", "leatherpants", "leather boots",

                "chainhelmet", "chainchestplate", "chainleggings", "chainboots",
                "goldenhelmet", "goldenchestplate", "goldenleggings", "goldenboots",
                "ironhelmet", "ironchestplate", "ironleggings", "ironboots",
                "diamondhelmet", "diamondchestplate", "diamondleggings", "diamondboots",

                "compasssouth", "compasssouthwest", "compasswest", "compassnorthwest",
                "compassnorth", "compassnortheast", "compasseast", "compasssoutheast",
                "clocknoon", "clockevening", "clockdusk", "clocknight",
                "clockmidnight", "clockweehours", "clockdawn", "clockmorning",

                "fishingrod", "fishingrodcast", "carrotonastick", "flintandsteel", "shears", "lead", "name tag",
                "iron horsearmor", "goldhorsearmor", "diamondhorsearmor", "saddle",
                "minecart", "minecartwithchest", "minecartwithfurnace", "minecartwithhopper", "minecartwithtnt",

                "boat", "book", "bookandquill", "writtenbook", "enchantedbook", "map", "emptymap",
                "bucket", "waterbucket", "lavabucket", "milk",
                "snowball", "egg", "enderpearl", "eyeofender", "firecharge",

                "disc13", "disccat", "disc blocks", "disc chirp", "disc far", "disc mall",
                "discmellohi", "disc stal", "disc strad", "disc ward", "disc 11", "disc wait",
                "bottle o' enchanting", "firework", "firework star blank", "firework star spots",

                "firework star white", "firework star orange", "firework star magenta", "firework star light blue",
                "firework star yellow", "firework star lime", "firework star pink", "firework star gray",
                "firework star light gray", "firework star cyan", "firework star purple", "firework star blue",
                "firework star brown", "firework star green", "firework star red", "firework star black",

                "bonemeal", "orange dye", "magenta dye", "light blue dye", "dandelion yellow", "lime dye", "pink dye", "gray dye",
                "light gray dye", "cyan dye", "purple dye", "lapis lazuli", "cocoa beans", "cactus green", "rose red", "ink sac",

                "sign", "painting", "item frame", "flower pot", "wooden door", "iron door", "hopper", "brewing stand",
                "cauldron", "bed", "repeater", "comparator", "redstone dust", "glowstone dust", "gunpowder", "sugar",

                "iron ingot", "gold ingot", "brick", "nether brick", "coal", "charcoal", "diamond", "ruby", "emerald",
                "nether quartz", "flint", "gold nugget", "ghast tear", "nether star", "clay ball", "slimeball",

                "skeletonskull", "witherskeletonskull", "zombiehead", "head", "creeperhead",
                "stick", "bone", "blazerod", "blazepowder", "magma cream", "leather",
                "feather", "paper", "string", "fermentedspidereye", "spidereye",

                "seeds", "melon seeds", "pumpkin seeds", "netherwart", "sugarcane", "wheat", "carrot", "golden carrot",
                "potato", "baked potato", "poisonous potato", "melon", "glistering melon", "apple", "golden apple", "rotten flesh",

                "rawporkchop", "porkchop", "rawbeef", "steak", "rawfish", "cookedfish", "rawchicken", "cookedchicken",
                "bowl", "mushroomstew", "bread", "cookie", "cake", "pumpkinpie", "glassbottle", "waterbottle",

                "potionofregeneration", "splash potion of regeneration", "potion of swiftness", "splash potion of swiftness",
                "potionoffire resistance", "splash potion of fire resistance", "potion of poison", "splash potion of poison",
                "potionof healing", "splash potion of healing", "potion of night vision", "splash potion of night vision",
                "potionof weakness", "splash potion of weakness", "potion of strength", "splash potion of strength",

                "potion of slowness", "splash potion of slowness", "potion of harming", "splash potion of harming",
                "potion of invisibility", "splash potion of invisibility", "splash potion bottle", "potion liquid",
                "potion of milk", "splash potion of milk", "white potion", "white splash potion",
                "swiftness potion", "splash swiftness potion", "slowness potion", "splash slowness potion",

                "haste potion potion", "splash haste potion", "dullness potion", "splash dullness potion",
                "strength potion", "splash strength potion", "health potion", "splash health potion",
                "harm potion", "splash harm potion", "jump boost potion", "splash jump boost potion",
                "nausea potion", "splash nausea potion", "regeneration potion", "splash regeneration potion",

                "resistance potion", "splash resistance potion", "fire resistance potion", "splash fire resistance potion",
                "water breathing potion", "splash water breathing potion", "invisibility potion", "splash invisibility potion",
                "blindness potion", "splash blindness potion", "night vision potion", "splash night vision potion",
                "hungerpotion", "splash hunger potion", "weakness potion", "splash weakness potion",
                "poisonpotion", "splash poison potion", "wither potion", "splash wither potion",
                "healthboostpotion", "splash health boost potion", "absorption potion", "splash absorption potion",
                "saturationpotion", "splash saturation potion", "splash mundane potion",
                "spawnegg", "spawn egg spots", "white spawn egg", "spawn creeper", "spawn skeleton",

                "spawn spider", "spawn zombie", "spawn slime", "spawn ghast", "spawn zombie pigman", "spawn enderman",
                "spawn cave spider", "spawn silverfish", "spawn blaze", "spawn magma cube", "spawn bat", "spawn witch",
                "spawn pig", "spawn sheep", "spawn cow", "spawn chicken",

                "spawn squid", "spawn wolf", "spawn mooshroom", "spawn ocelot", "spawn horse", "spawn villager",
                "spawn snow golem", "spawn iron golem", "spawn wither skeleton", "spawn wither",
                "spawn human", "spawn tnt", "spawn spawner", "unused", "unused", "unused",

                "tfda wooden sword", "tfda wooden pickaxe", "tfda wooden shovel", "tfda wooden axe", "tfda wooden hoe",
                "tfda stone sword", "tfda stone pickaxe", "tfda stone shovel", "tfda stone axe", "tfda stone hoe",
                "tfda golden sword", "tfda golden pickaxe", "tfda golden shovel", "tfda golden axe", "tfda golden hoe", "unused",

                "tfda iron sword", "tfda iron pickaxe", "tfda iron shovel", "tfda iron axe", "tfda iron hoe",
                "tfda diamond sword", "tfda diamond pickaxe", "tfda diamond shovel", "tfda diamond axe", "tfda diamond hoe",
                "tfda fishing rod", "tfda fishing rod cast", "tfda carrot on a stick", "unused", "tfda wooden door", "tfda iron door",

                "tfda white leather cap", "tfda white leather tunic", "tfda white leather pants", "tfda white leather boots",
                "tfda blank leather cap", "tfda blank leather tunic", "tfda blank leather pants", "tfda blank leather boots",
                "tfda leather cap", "tfda leather tunic", "tfda leather pants", "tfda leather boots",
                "tfda chain helmet", "tfda chain chestplate", "tfda chain leggings", "tfda chain boots",

                "tfda golden helmet", "tfda golden chestplate", "tfda golden leggings", "tfda golden boots",
                "tfda iron helmet", "tfda iron chestplate", "tfda iron leggings", "tfda iron boots",
                "tfda diamond helmet", "tfda diamond chestplate", "tfda diamond leggings", "tfda diamond boots",
                "unused", "tfda gold nugget", "tfda gold ingot", "tfda golden apple",

                "tfda nether brick", "tfda nether quartz", "tfda zombie head", "tfda purple dye",
                "unused", "unused", "unused", "unused", "old cocoa beans", "unused", "starfish", "seashell",
                "custom disc dream", "custom disc ezusht", "custom disc friend", "custom disc ran",

                "oaksapling", "sprucesapling", "birchsapling", "junglesapling", "deadbush", "shrub",
                "whitefern", "fern", "whitegrass", "grass", "whitestemattached", "stemattached",
                "whitestem", "stem", "whitevines", "vines",

                "whitelilypad", "lilypad", "sugarcaneblock", "netherwart3", "netherwart2", "netherwart1",
                "redmushroom", "brownmushroom", "rose", "cyanflower", "dandelion",
                "unused", "unused", "iron bars", "glasspane", "cobweb",

                "wheat8", "wheat7", "wheat6", "wheat5", "wheat4", "wheat3", "wheat2", "wheat1",
                "potatoes4", "potatoes3", "potatoes2", "potatoes1", "carrots4", "carrots3", "carrots2", "carrots1",

                "rail", "rail north-east", "powered rail", "powered powered rail", "detector rail", "powered detector rail",
                "activator rail", "powered activator rail", "rail horizontal", "rail east-south",
                "powered rail horizontal", "powered powered rail horizontal",
                "detector rail horizontal", "powered detector rail horizontal",
                "activator rail horizontal", "powered activator rail horizontal",

                "rail south-east", "raileast-north", "ladder", "ladderhorizontal",
                "redstone", "redstone horizontal", "redstonevertical",
                "tripwire", "tripwirehorizontal", "tripwirevertical", "tripwirecross", "tripwirehook",
                "lever", "torch", "redstonetorch", "redstone torchoff",

                "fire0", "fire1", "fire2", "fire3", "fire4", "fire5",
                "fireinside0", "fireinside1", "fireinside2", "fireinside3", "fireinside4",
                "stationarywater", "flowingwater", "stationarylava", "flowinglava", "netherportal",

                "stationary water solid", "flowing water solid", "nether portal solid",
                "break 0", "break 1", "break 2", "break 3", "break 4", "break 5", "break 6", "break 7", "break 8", "break 9",
                "unused", "unused", "unused",

                "coal ore tile", "iron ore tile", "gold ore tile", "redstone ore tile", "diamond ore tile",
                "lapis lazuli ore tile", "emerald ore tile", "cobblestone tile", "mossy cobblestone tile",
                "stone tile", "stone bricks tile", "mossy stone bricks tile", "cracked stone bricks tile",
                "chiseled stone bricks tile", "stone slab top tile", "stone slab side tile",

                "coal block tile", "iron block tile", "gold block tile", "redstone block tile", "diamond block tile",
                "lapis lazuli block tile", "emerald block tile", "bedrock tile", "furnace tile", "burning furnace tile",
                "furnace back tile", "furnace top tile", "dispenser tile", "dispenser top tile", "dropper tile", "dropper top tile",

                "piston tile", "piston head tile", "piston top tile", "repeater tile", "powered repeater tile",
                "comparator tile", "powered comparator tile", "redstone lamp tile", "powered redstone lamp tile",
                "daylight detector bottom tile", "daylight detector tile", "crafting table top tile",
                "crafting table side tile", "crafting table tile", "jukebox tile", "note block tile",

                "dirt tile", "grass block tile", "snowy grass block tile", "mycelium tile", "farmland tile", "wet farmland tile",
                "snow tile", "soul sand tile", "gravel tile", "sand tile", "sandstone tile", "smooth sandstone tile",
                "chiseled sandstone tile", "sandstone top tile", "sandstone bottom tile", "end stone tile",

                "obsidian tile", "glowstone tile", "nether brick block tile", "netherrack tile", "nether quartz ore tile",
                "quartz block tile", "quartz block bottom tile", "pillar quartz tile", "pillar quartz top tile",
                "chiseled quartz tile", "chiseled quartz top tile", "oak log tile", "oak log top tile", "oak planks tile",
                "bookshelf tile", "trapdoor tile",

                "pumpkin tile", "jack o' lantern tile", "pumkin side tile", "pumpkin top tile", "melon block tile",
                "melon block top tile", "cactus tile", "mushroom stem tile", "mushroom brown tile", "mushroom red tile",
                "mushroom pores tile", "hay bale tile", "sponge tile", "ice tile", "tnt tile", "spawner tile",

                "enchantment table top tile", "enchantment table side tile", "end portal frame tile", "end portal frame side tile",
                "cake top tile", "cake side tile", "cake side eaten tile", "command block tile", "unused", "unused",
                "unused", "unused", "beacon crystal tile", "item frame back tile", "painting back tile", "static tile",

                "tfda bedrock tile", "summoning brick tile", "corrupt brick side tile", "corrupt brick top tile",
                "tfda redstone ore tile", "corrupt runes tile", "tfda netherbrick tile", "tfda netherrack",
                "tfda obsidian tile", "air glyph tile", "water glyph tile", "earth glyph tile", "fire glyph tile",
                "tfda glowstone tile", "tfda cobblestone tile", "cracked icy bricks tile",

                "light blue cloth tile", "green cloth tile", "yellow cloth tile", "purple cloth tile",
                "glowing red cloth tile", "red cloth tile", "tfda coal block tile", "tfda redstone block tile",
                "tfda lapis lazuli block tile", "tfda hardened clay tile", "tfda snow tile", "tfda snowy grass block tile",
                "tfda mycelium tile", "tfda ice tile", "tfda tnt tile", "tfda sponge tile",

                "tfda chiseled sandstone tile", "tfda bricks tile", "tfda daylight detector bottom tile",
                "tfda daylight detector tile", "tfda bookshelf tile", "tfda cactus tile",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "creeper", "chargedcreeper", "skeleton", "wither skeleton", "spider", "zombie", "zombie villager", "slime",
                "ghast", "firingghast", "zombiepigman", "enderman", "cavespider", "silverfish", "blaze", "magma cube",

                "bat", "witch", "unused", "pig", "sheep", "cow", "chicken", "squid", "wolf", "dog", "angry wolf",
                "mooshroom", "ocelot", "black cat", "ginger cat", "siamese cat",

                "horse", "donkey", "mule", "skeletonhorse", "zombiehorse", "villager", "human", "snow golem",
                "irongolem", "unused", "enderdragon", "wither", "witherprotected", "unused", "witherskull", "bluewitherskull",

                "rideable minecart", "chest minecart", "furnace minecart", "hopper minecart", "tnt minecart",
                "spawner minecart", "boat entity", "sign front", "sign back", "chest tile", "trapped chest tile",
                "ender chest tile", "christmas chest tile", "doublechest", "trapped doublechest", "christmas doublechest",

                "ender crystal shell", "ender crystal gem 1", "ender crystal gem 2", "ender crystal gem 3",
                "ender crystal gem 4", "ender crystal gem 5", "ender crystal gem 6", "ender crystal 1",
                "ender crystal 2", "ender crystal 3", "ender crystal 4", "ender crystal 5", "ender crystal 6",
                "beacon beam", "arrow entity", "item frame entity",

                "painting kebab", "painting aztec", "painting alban", "painting aztec2", "painting bomb", "painting plant",
                "painting wasteland", "painting pool", "painting courbet", "painting sea", "painting sunset",
                "painting creebet", "painting wanderer", "painting graham", "painting skeleton", "painting donkeykong",

                "painting match", "painting bust", "painting stage", "painting void", "painting skullandroses",
                "painting wither", "painting fighters", "painting pointer", "painting pigscene", "painting flaming skull",
                "unused", "unused", "unused", "unused", "unused", "unused",

                "xp orb 1", "xp orb 2", "xp orb 3", "xp orb 4", "xp orb 5", "xp orb 6", "xp orb 7", "xp orb 8",
                "xp orb 9", "xp orb 10", "xp orb 11", "unused", "unused", "unused", "unused", "unused",

                "inventory slot", "button", "button disabled", "button highlighted", "delete slot",
                "gui arrow", "gui blocked arrow", "gui complete arrow", "gui burning", "gui burning full",
                "gui plus", "gui integral", "gui delete", "gui potion", "gui green check", "gui red x",

                "hammer", "hammer and anvil", "abacus", "broken shovel", "right button", "right button highlighted",
                "right button disabled", "left button", "left button highlighted", "left button disabled",
                "up button", "down button", "gui language", "realms off", "realms on", "realms none",

                "white gui arrow", "white gui burning", "white gui plus", "white gui integral", "white gui delete",
                "white gui potion", "white gui check", "white gui x", "gui circle", "gui cursor",
                "connection5", "connection4", "connection3", "connection2", "connection1", "connection0",

                "connecting1", "connecting2", "connecting3", "connecting4", "connecting5",
                "hotbar slot", "hotbar cursor", "selected hotbar slot", "gui 1", "gui 2", "gui 3", "gui 4", "gui 5",
                "gui !!!", "unused", "unused",

                "messages highlighted", "messages", "1 message highlighted", "2 messages highlighted",
                "3 messages highlighted", "4 messages highlighted", "5 messages highlighted", "!!! messages highlighted",
                "1 message", "2 messages", "3 messages", "4 messages", "5 messages", "!!! messages", "heart container", "healing heart container",

                "heart", "halfheart", "poisonheart", "halfpoisonheart", "witherheart", "halfwitherheart",
                "mob heart", "half mob heart", "hardcore heart", "hardcore half heart",
                "hardcore poison heart", "hardcore half poison heart", "hardcore wither heart", "hardcore half wither heart",
                "absorption heart", "half absorption heart",

                "light heart", "light half heart", "light poison heart", "light half poison heart",
                "light wither heart", "light half wither heart", "light mob heart", "light half mob heart",
                "light hardcore heart", "light hardcore half heart", "light hardcore poison heart",
                "light hardcore half poison heart", "light hardcore wither heart", "light hardcore half wither heart",
                "hardcore absorption heart", "hardcore half absorption heart",

                "healing heart", "healing half heart", "healing poison heart", "healing half poison heart",
                "healing wither heart", "healing half wither heart", "healing mob heart", "healing half mob heart",
                "healing hardcore heart", "healing hardcore half heart", "healing hardcore poison heart",
                "healing hardcore half poison heart", "healing hardcore wither heart", "healing hardcore half wither heart",
                "red heart container", "red wither heart",

                "armor point", "half armor point", "empty armor point", "air bubble", "half air bubble",
                "reversed hunger point", "hunger container", "healing hunger container", "red hunger container",
                "green hunger container", "brown hunger container", "unused", "unused", "unused", "unused", "unused",

                "hunger point", "half hunger point", "light hunger point", "light half hunger point",
                "poison hunger point", "half poison hunger point", "light poison hunger point",
                "light half poison hunger point", "healing hunger point", "healing half hunger point",
                "healing poison hunger point", "healing half poison hunger point", "hunger hunger point",
                "half hunger hunger point", "healing hunger hunger point", "healing half hunger hunger point",

                "swiftness", "slowness", "haste", "mining fatigue", "strength", "weakness", "poison", "regeneration",
                "invisibility", "hunger", "jump boost", "nausea", "night vision", "blindness", "resistance", "fire resistance",

                "water breathing", "wither status", "absorption", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "tfda zombie", "tfda woman", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "tfda trapped chest tile", "tfda trapped doublechest",

                "tfda swiftness", "tfda slowness", "tfda regeneration", "tfda jump boost", "tfda nausea",
                "tfda blindness", "tfda wither status", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused",

                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "map landmark", "map nether portal", "map end portal", "map portal", "map floating island",
                "map basin", "map lake", "map lava lake", "map river", "map waterfall", "map cave", "map ravine",
                "map dungeon", "map mineshaft", "map stronghold", "map village",

                "map desert well", "map desert temple", "map jungle temple", "map witch hut", "map nether fortress",
                "map obsidian pillar", "map ender crystal", "map end exit portal", "map teleport altar",
                "map estate agency", "map port", "map bank", "map bed", "map inn", "map chest", "map ender chest",

                "map crafting table", "map furnace", "map brewing stand", "map brewing stand and cauldron",
                "map cauldron", "map enchantment table", "map anvil", "map anvil damaged", "map cake", "map cake eaten",
                "map jukebox", "map map", "map beacon", "map fishing", "map wishing well", "map library",

                "map mine", "map stable", "map fountain", "map shop", "map trading", "map challenge", "map plot",
                "map monument", "map gravestone", "map house", "map large house", "map shack", "map tower",
                "map castle", "map treehouse", "map ruins",

                "map totem", "map danger", "map prohibited", "map farm", "map wheat", "map sugarcane",
                "map cocoa beans", "map netherwart", "map tree", "map melon", "map pumpkin", "map cactus",
                "map carrot", "map potato", "map animal farm", "map pig farm",

                "map sheep farm", "map cow farm", "map chicken farm", "map horse farm", "map mooshroom farm",
                "map squid farm", "map snow golem farm", "map forest", "map desert", "map plains", "map swamp",
                "map jungle", "map taiga", "map tundra", "map mountains", "map mushroom island",

                "mapspawner", "mapzombiespawner", "mapskeletonspawner", "mapspiderspawner", "mapcavespiderspawner",
                "mapblaze spawner", "mapsilverfishspawner", "mapendermanspawner", "mappigspawner",
                "pigmob", "sheepmob", "cowmob", "chickenmob", "horsemob", "donkeymob", "mulemob",

                "map grinder", "map zombie grinder", "map skeleton grinder", "map spider grinder", "map cave spider grinder",
                "map blaze grinder", "map silverfish grinder", "map enderman grinder", "map pig grinder",
                "skeleton horse mob", "zombie horse mob", "mooshroom mob", "squid mob", "wolf mob", "dog mob", "angry wolf mob",

                "map xp grinder", "map zombie xp grinder", "map skeleton xp grinder", "map spider xp grinder",
                "map cave spider xp grinder", "map blaze xp grinder", "map silverfish xp grinder", "map enderman xp grinder",
                "map pig xp grinder", "ocelotmob", "blackcatmob", "gingercatmob", "siamesecatmob", "batmob",
                "farmermob", "butchermob",

                "blacksmithmob", "librarianmob", "priestmob", "villagermob", "humanmob", "snowgolemmob",
                "iron golemmob", "enderdragonmob", "withermob", "witherprotectedmob", "creepermob",
                "chargedcreepermob", "skeletonmob", "witherskeletonmob", "spidermob", "zombiemob",

                "zombievillagermob", "slimemob", "ghastmob", "firingghastmob", "zombiepigmanmob", "endermanmob",
                "cavespidermob", "silverfishmob", "blazemob", "magmacubemob", "witchmob",
                "unused", "unused", "unused", "unused", "unused",

                "amenity hidden chest", "amenity multi chest", "amenity hidden trapped chest", "amenity return portal",
                "amenity floor plan map", "amenity map", "amenity secrets map", "amenity wastebasket",
                "amenity fountain of life", "amenity key portal", "amenity xp bank", "amenity alchemic circle",
                "amenity meeting chamber", "amenity item spawn", "amenity heaven's door", "amenity hell's door",

                "amenity buying trader", "amenity gear trader", "amenity food/material trader", "amenity food/mat/pot trader",
                "amenity omni trader", "map tfda zombie spawner", "map tfda zombie grinder", "map tfda zombie xp grinder",
                "unused", "unused", "unused", "unused", "unused", "unused", "map christmas chest", "map trapped chest",

                "tfda zombie mob", "tfda woman mob", "unused", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "white sword", "white pickaxe", "white shovel", "white axe", "white hoe", "white bow", "white arrow",
                "white helmet", "white chestplate", "white leggings", "white boots",
                "white fishing rod", "white lead", "white name tag", "white horse armor", "white saddle",

                "white minecart", "white minecart with chest", "white boat", "white book", "white empty map", "white map",
                "white bucket", "white egg", "white ender pearl", "white eye of ender", "white fire charge", "white disc",
                "white bottle o' enchanting", "white firework", "white firework star blank", "white firework star",

                "white orange dye", "white magenta dye", "white light blue dye", "white dandelion yellow",
                "white light gray dye", "white cyan dye", "white lapis lazuli", "white cocoa beans", "white cactus green",
                "white rose red", "white ink sac", "white sign", "white painting", "white item frame", "white door", "white hopper",

                "white bed", "white redstone dust", "white iron ingot", "white coal", "white charcoal", "white diamond",
                "white ruby", "white emerald", "white nether quartz", "white flint", "white gold nugget",
                "white ghast tear", "white nether star", "white clay ball", "white slimeball", "unused",

                "white skeleton skull", "white wither skeleton skull", "white zombie head", "white head", "white creeper head",
                "white stick", "white blaze rod", "white blaze powder", "white magma cream", "white leather",
                "white paper", "white fermented spider eye", "white spider eye", "unused", "unused", "unused",

                "white seeds", "white melon seeds", "white pumpkin seeds", "white netherwart", "white sugarcane",
                "white wheat", "white carrot", "white golden carrot", "white potato", "white baked potato", "white poisonous potato",
                "white melon", "white glistering melon", "white apple", "white golden apple", "white rotten flesh",

                "white raw porkchop", "white porkchop", "white raw beef", "white steak", "white raw fish",
                "white cooked fish", "white raw chicken", "white cooked chicken", "white bowl", "white mushroom stew",
                "white bread", "white cookie", "white cake", "white pumpkin pie", "unused", "unused",

                "tfda white helmet", "tfda white chestplate", "tfda white leggings", "tfda white boots",
                "tfda white nether quartz", "tfda white zombie head", "tfda white purple dye", "old white cocoa beans",
                "unused", "unused", "unused", "unused", "unused", "unused", "white starfish", "white seashell",

                "white oak sapling", "white spruce sapling", "white birch sapling", "white jungle sapling",
                "white sugarcane block", "white netherwart 3", "white netherwart 2", "white netherwart 1",
                "white red mushroom", "white brown mushroom", "white rose", "white dandelion",
                "white iron bars", "white wheat block", "white potatoes", "white carrots",

                "white rail", "white rail horizontal", "white ladder", "white ladder horizontal",
                "white lever", "white torch", "white shrub",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "white fire", "white stationary water", "white flowing water", "white stationary lava",
                "white flowing lava", "white nether portal", "white stationary water solid",
                "white flowing water solid", "white nether portal solid", "white ore tile", "white stone tile",
                "white stone bricks tile", "white cracked stone bricks tile", "white chiseled stone bricks tile",
                "white stone slab top tile", "white stone slab side tile",

                "white furnace top tile", "white coal block tile", "white iron block tile", "white gold block tile",
                "white redstone block tile", "white lapis lazuli block tile", "white emerald block tile",
                "white bedrock tile", "white redstone lamp tile", "white powered redstone lamp tile",
                "white daylight detector bottom tile", "white daylight detector tile",
                "white jukebox tile", "white note block tile", "white dirt tile", "white grass block tile",

                "white mycelium tile", "white farmland tile", "white snow tile", "white soul sand tile", "white gravel tile",
                "white sand tile", "white sandstone tile", "white smooth sandstone tile", "white chiseled sandstone tile",
                "white sandstone top tile", "white sandstone bottom tile", "white end stone tile",
                "white obsidian tile", "white glowstone tile", "white nether brick block tile", "white netherrack tile",

                "white oak log tile", "white oak log top tile", "white oak planks tile", "white pumpkin tile",
                "white jack o' lantern tile", "white pumkin side tile", "white pumpkin top tile", "white melon block tile",
                "white melon block top tile", "white tnt tile", "white spawner tile", "white command block tile",
                "white beacon crystal tile", "white item frame back tile", "white painting back tile", "white static tile",

                "tfda white coal block tile", "tfda white redstone block tile", "tfda white lapis lazuli block tile",
                "tfda white hardened clay tile", "unused", "unused", "unused", "tfda white snowy grass block tile",
                "tfda white snow tile", "unused", "unused", "unused", "unused", "unused",
                "tfda white chiseled sandstone tile", "tfda white bricks tile",

                "white boat entity", "white sign front", "white sign back", "white chest tile", "white trapped chest tile",
                "white ender chest tile", "white christmas chest tile", "white doublechest", "white trapped doublechest",
                "white christmas doublechest", "unused", "unused", "unused", "unused",
                "tfda white trapped chest tile", "tfda white trapped doublechest",

                "white ender crystal shell", "white ender crystal gem 1", "white ender crystal gem 2",
                "white ender crystal gem 3", "white ender crystal gem 4", "white ender crystal gem 5",
                "white ender crystal gem 6", "white ender crystal 1", "white ender crystal 2", "white ender crystal 3",
                "white ender crystal 4", "white ender crystal 5", "white ender crystal 6", "white arrow entity",
                "white wither skull", "white blue wither skull",

                "white zombie", "white zombie villager", "white witch", "white villager", "white human", "unused",
                "white enderman", "white blaze", "unused", "white wither", "unused", "unused", "unused", "unused",
                "tfda white zombie", "tfda white woman",

                "white gui blocked arrow", "white gui burning full", "white gui green check", "white gui red x",
                "unused", "unused", "unused", "unused",
                "white hammer", "white hammer and anvil", "white abacus", "white broken shovel",
                "unused", "white realms off", "white realms on", "white realms none",

                "white connection 5", "white connection 4", "white connection 3", "white connection 2", "white connection 1",
                "white connection 0", "white gui 1", "white gui 2", "white gui 3", "white gui 4", "white gui 5",
                "white gui !!!", "unused", "unused", "unused", "unused",

                "white heart container", "white heart", "white half heart", "white poison heart", "white half poison heart",
                "white mob heart", "white half mob heart", "white hardcore heart", "white hardcore half heart",
                "white hardcore poison heart", "white hardcore half poison heart", "white absorption heart",
                "white half absorption heart", "white light heart", "white light half heart", "unused",

                "white armor point", "white half armor point", "white empty armor point", "white air bubble",
                "white half air bubble", "white reversed hunger point", "white hunger container",
                "white hunger point", "white half hunger point", "white light hunger point", "white light half hunger point",
                "white poison hunger point", "white half poison hunger point", "unused", "unused", "unused",

                "white swiftness", "white slowness", "white haste", "white mining fatigue",
                "white strength", "white weakness", "white poison", "white regeneration",
                "white invisibility", "white hunger", "white jump boost", "white nausea",
                "white night vision", "white blindness", "white resistance", "white fire resistance",

                "white water breathing", "white wither status", "white absorption", "unused", "unused", "unused",
                "unused", "unused", "unused", "tfda white swiftness", "tfda white slowness", "tfda white regeneration",
                "tfda white jump boost", "tfda white nausea", "tfda white blindness", "tfda white wither status",

                "northarrow", "northeastarrow", "eastarrow", "southeastarrow",
                "southarrow", "southwestarrow", "westarrow", "northwestarrow",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "symbol cycloid emblem aa", "symbol snub diamond twist aa", "symbol diamond twist aa",
                "symbol astroid fractal aa", "symbol tight astroid twist aa", "symbol astroid twist aa", "unused", "unused",
                "symbol cycloid emblem", "symbol snub diamond twist", "symbol diamond twist", "symbol astroid fractal",
                "symbol tight astroid twist", "symbol astroid twist", "unused", "unused",

                "symbol astroid lace aa", "symbol astroid lace thin aa", "symbol astroid lace split aa",
                "symbol astroid lace small split aa", "symbol ashndar aa", "symbol quest aa", "unused", "unused",
                "symbol astroid lace", "symbol astroid lace thin", "symbol astroid lace split",
                "symbol astroid lace small split", "symbol ashndar", "symbol quest", "unused", "unused",

                "symbol x tight aa", "symbol x tight even-odd aa", "symbol adventure aa", "symbol x even-odd aa",
                "unused", "unused", "unused", "unused",
                "symbol x tight", "symbol x tight even-odd", "symbol adventure", "symbol x even-odd",
                "unused", "unused", "unused", "unused",

                "symbol corner pair aa", "symbol corner diamond aa", "symbol inverted corner diamond aa",
                "symbol overlay rune aa", "symbol minigame aa", "unused", "unused", "unused",
                "symbol corner pair", "symbol corner diamond", "symbol inverted corner diamond",
                "symbol overlay rune", "symbol minigame", "unused", "unused", "unused",

                "quest icon", "ashndar icon", "adventure icon", "minigame icon", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "white quest icon", "white ashndar icon", "white adventure icon", "white minigame icon", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "air", "stone", "bedrock", "obsidian", "netherrack", "glowstone", "endstone", "ice",
                "dirt", "grassblock", "mycelium", "sand", "gravel", "soulsand", "clay", "snowblock",

                "coalore", "ironore", "goldore", "redstoneore", "diamondore", "lapislazuliore", "emeraldore",
                "netherquartzore", "unused", "coalblock", "ironblock", "goldblock", "redstoneblock", "diamondblock",
                "lapislazuliblock", "emeraldblock",

                "cobblestone", "mossy cobblestone", "stone bricks", "mossy stone bricks", "cracked stone bricks",
                "chiseled stone bricks", "bricks", "nether brick block", "sandstone", "smooth sandstone",
                "chiseled sandstone", "quartz block", "pillar quartz", "chiseled quartz", "hardened clay", "glass",

                "whitestainedclay", "orangestainedclay", "magentastainedclay", "lightbluestainedclay",
                "yellowstainedclay", "limestainedclay", "pinkstainedclay", "graystainedclay",
                "lightgray stainedclay", "cyanstainedclay", "purplestainedclay", "bluestainedclay",
                "brownstainedclay", "greenstainedclay", "redstainedclay", "blackstainedclay",

                "whitewool", "orangewool", "magentawool", "lightbluewool",
                "yellowwool", "limewool", "pinkwool", "graywool",
                "lightgraywool", "cyanwool", "purplewool", "bluewool",
                "brownwool", "greenwool", "redwool", "blackwool",

                "whitecarpet", "orangecarpet", "magentacarpet", "lightbluecarpet",
                "yellowcarpet", "limecarpet", "pinkcarpet", "graycarpet",
                "lightgray carpet", "cyancarpet", "purplecarpet", "bluecarpet",
                "browncarpet", "greencarpet", "redcarpet", "blackcarpet",

                "oaklog", "sprucelog", "birchlog", "junglelog",
                "oakplanks", "spruceplanks", "birchplanks", "jungleplanks",
                "oakleaves", "spruceleaves", "birchleaves", "jungleleaves",
                "oakleaveshd", "spruceleaves hd", "birchleaveshd", "jungleleaveshd",

                "oakwoodstairs", "sprucewoodstairs", "birchwoodstairs", "junglewoodstairs",
                "cobblestonestairs", "stonebrickstairs", "sandstonestairs", "brickstairs", "netherbrickstairs",
                "quartzstairs", "unused", "pumpkin", "jackolantern", "melonblock", "cactus", "sponge",

                "oakwoodslab", "sprucewoodslab", "birchwoodslab", "junglewoodslab",
                "cobblestoneslab", "stonebrickslab", "sandstoneslab", "brickslab", "netherbrickslab", "quartzslab",
                "stoneslab", "cobblestonewall", "mossycobblestonewall", "fence", "netherbrickfence", "fencegate",

                "bookshelf", "crafting table", "furnace", "dispenser", "dropper", "piston", "sticky piston",
                "note block", "jukebox", "redstone lamp", "tnt", "hay bale", "stone pressure plate",
                "wooden pressure plate", "weighted pressure plate (light)", "weighted pressure plate (heavy)",

                "chest", "trapped chest", "ender chest", "enchantment table", "end portal frame",
                "anvil", "slightly damaged anvil", "very damaged anvil", "trapdoor", "snow", "daylight detector",
                "stone button", "wooden button", "enderdragonegg", "command block", "spawner",

                "doubleslab", "smooth stone doubleslab", "smooth sandstone doubleslab", "farmland", "wet farmland",
                "furnace back", "lit redstone lamp", "error block", "cake block", "eaten cake block",
                "nether portal block", "flipped nether portal block", "unused",
                "flipped pumpkin", "flipped jack o' lantern", "uncarved pumpkin",

                "left oak log", "left spruce log", "left birch log", "left jungle log",
                "right oak log", "right spruce log", "right birch log", "right jungle log",
                "full bark oak log", "full bark spruce log", "full bark birch log", "full bark jungle log",
                "left pillar quartz", "right pillar quartz", "left hay bale", "right hay bale",

                "mushroom pore", "mushroom stem", "mushroom full stem",
                "brown mushroom left", "brown mushroom center", "brown mushroom right", "brown mushroom full block",
                "red mushroom left", "red mushroom center", "red mushroom right", "red mushroom full block",
                "unused", "locked chest", "glowingobsidian", "nether reactor", "stonecutter",

                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "tfda bedrock", "tfda redstone ore", "corrupt bricks", "summoning brick",
                "tfda obsidian", "tfda glowstone", "air glyph", "water glyph", "earth glyph", "fire glyph",
                "unused", "tfda netherrack", "tfda ice", "tfda mycelium", "tfda snow block", "tfda snow",

                "tfda coal block", "tfda redstone block", "tfda lapis lazuli block", "cracked icy bricks",
                "tfda chiseled sandstone", "tfda hardened clay", "tfda bookshelf", "tfda tnt", "tfda locked chest",
                "tfda birch log", "tfda cactus", "tfda sponge", "icy brick fence", "tfda nether brick fence", "unused", "unused",

                "tfda white stained clay", "tfda light blue stained clay", "tfda lime stained clay",
                "tfda pink stained clay", "tfda gray stained clay", "tfda light gray stained clay", "tfda cyan stained clay",
                "tfda purple stained clay", "tfda blue stained clay", "tfda green stained clay", "tfda black stained clay",
                "unused", "tfda cobblestone", "tfda bricks", "tfda nether brick block", "tfda spruce planks",

                "icy brick stairs", "tfda brick stairs", "tfda nether brick stairs", "tfda spruce wood stairs",
                "icy brick slab", "tfda brick slab", "tfda nether brick slab", "tfda spruce wood slab",
                "unused", "tfda orange wool", "glowing red cloth", "light blue cloth", "yellow cloth",
                "purple cloth", "green cloth", "red cloth",

                "tfda orange carpet", "glowing red carpet", "tfda light blue carpet", "tfda yellow carptet",
                "tfda purple carpet", "tfda green carpet", "tfda red carpet", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "white stone", "white bedrock", "white obsidian", "white netherrack", "white glowstone",
                "white end stone", "white ice", "white dirt", "white sand", "white gravel", "white soul sand",
                "white clay", "white snow block", "white ore", "unused", "unused",

                "white coal block", "white iron block", "white gold block", "white redstone block",
                "white lapis lazuli block", "white emerald block", "white oak log", "white birch log",
                "white jungle log", "white planks", "white oak leaves", "white spruce leaves", "white jungle leaves",
                "white oak leaves hd", "white spruce leaves hd", "white jungle leaves hd",

                "white cobblestone", "white stone bricks", "white chiseled stone bricks", "white bricks",
                "white nether brick block", "white sandstone", "white smooth sandstone", "white chiseled sandstone",
                "white quartz block", "white pillar quartz", "white chiseled quartz", "white hardened clay",
                "white glass", "white chest", "white ender chest", "white end portal frame",

                "white wood stairs", "white cobblestone stairs", "white stone brick stairs", "white sandstone stairs",
                "white brick stairs", "white nether brick stairs", "white quartz stairs", "unused",
                "white pumpkin", "white jack o' lantern", "white melon", "white cactus", "white sponge",
                "white anvil", "white slightly damaged anvil", "white very damaged anvil",

                "white wood slab", "white cobblestone slab", "white stone brick slab", "white sandstone slab",
                "white brick slab", "white nether brick slab", "white quartz slab", "white stone slab",
                "white cobblestone wall", "white fence", "white netherbrick fence", "white fence gate",
                "white snow", "white daylight detector", "white stone button", "white wooden button",

                "white bookshelf", "white crafting table", "white furnace", "white dispenser", "white dropper",
                "white piston", "white note block", "white jukebox", "white redstone lamp", "white tnt", "white hay bale",
                "white stone pressure plate", "white wooden pressure plate", "white weighted pressure plate (light)",
                "white weighted pressure plate (heavy)", "white trapdoor",

                "white enderdragon egg", "white command block", "white spawner", "white doubleslab",
                "white smooth stone doubleslab", "white smooth sandstone doubleslab", "white farmland", "white wet farmland",
                "white furnace back", "white lit redstone lamp", "white error block", "white cake block",
                "white eaten cake block", "white nether portal block", "white flipped nether portal block", "unused",

                "white left oak log", "white left birch log", "white left jungle log",
                "white right oak log", "white right birch log", "white right jungle log",
                "white full bark oak log", "white full bark birch log", "white full bark jungle log",
                "white flipped pumpkin", "white flipped jack o' lantern", "white uncarved pumpkin",
                "white left pillar quartz", "white right pillar quartz", "white left hay bale", "white right hay bale",

                "white mushroom pore", "white mushroom stem", "white mushroom full stem", "white brown mushroom left",
                "white brown mushroom center", "white brown mushroom right", "white brown mushroom full block",
                "white red mushroom left", "white red mushroom center", "white red mushroom right",
                "white red mushroom full block", "unused", "white locked chest",
                "white glowing obsidian", "white nether reactor", "white stonecutter",

                "tfda white bedrock", "tfda white redstone ore", "tfda white obsidian", "tfda white glowstone",
                "white air glyph", "white water glyph", "white earth glyph", "white fire glyph",
                "unused", "tfda white ice", "tfda white mycelium", "white cracked icy bricks",
                "tfda white chiseled sandstone", "tfda white hardened clay", "tfda white cobblestone", "tfda white bricks",

                "white glowing red cloth", "white light blue cloth", "white yellow cloth",
                "white purple cloth", "white green cloth", "white red cloth",
                "white glowing red carpet", "tfda white light blue carpet", "tfda white yellow carptet",
                "tfda white purple carpet", "tfda white green carpet", "tfda white red carpet",
                "white icy brick fence", "tfda white trapped chest", "unused", "unused",

                "glowing nether star", "glowing bottle o' enchanting", "glowing golden apple", "glowing enchanted book",
                "glowing written book", "unused", "unused", "unused", "celestial magic powder", "arcane magic powder",
                "aetheric magic powder", "sagittan magic powder", "tenacious magic powder", "lithe magic powder",
                "unearthly magic powder", "divine magic powder",

                "tfda clock noon", "tfda clock sunset", "tfda clock sunrise", "tfda clock midnight",
                "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "large north arrow", "large northeast arrow", "large east arrow", "large southeast arrow",
                "large south arrow", "large southwest arrow", "large west arrow", "large northwest arrow",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "large symbol cycloid emblem aa", "large symbol snub diamond twist aa", "large symbol diamond twist aa",
                "large symbol astroid fractal aa", "large symbol tight astroid twist aa", "large symbol astroid twist aa",
                "unused", "unused", "large symbol cycloid emblem", "large symbol snub diamond twist",
                "large symbol diamond twist", "large symbol astroid fractal", "large symbol tight astroid twist",
                "large symbol astroid twist", "unused", "unused",

                "large symbol astroid lace aa", "large symbol astroid lace thin aa", "large symbol astroid lace split aa",
                "large symbol astroid lace small split aa", "large symbol ashndar aa", "large symbol quest aa", "unused",
                "unused", "large symbol astroid lace", "large symbol astroid lace thin", "large symbol astroid lace split",
                "large symbol astroid lace small split", "large symbol ashndar", "large symbol quest", "unused", "unused",

                "large symbol x tight aa", "large symbol x tight even-odd aa", "large symbol adventure aa", "large symbol x even-odd aa",
                "unused", "unused", "unused", "unused",
                "large symbol x tight", "large symbol x tight even-odd", "large symbol adventure", "large symbol x even-odd",
                "unused", "unused", "unused", "unused",

                "large symbol corner pair aa", "large symbol corner diamond aa", "large symbol inverted corner diamond aa",
                "large symbol overlay rune aa", "large symbol minigame aa", "unused", "unused", "unused",
                "large symbol corner pair", "large symbol corner diamond", "large symbol inverted corner diamond",
                "large symbol overlay rune", "large symbol minigame", "unused", "unused", "unused",

                "large quest icon", "large ashndar icon", "large adventure icon", "large minigame icon",
                "unused", "unused", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",

                "large white quest icon", "large white ashndar icon", "large white adventure icon",
                "large white minigame icon", "unused", "unused",
                "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused"};

        CHARACTER_NAME_MAP = new HashMap();
        for (int i = 0; i < CHARACTER_NAME_LIST.length; i++) {
            CHARACTER_NAME_MAP.put(CHARACTER_NAME_LIST[i], (char) (i + UNICODE_OFFSET));
        }
    }

    public static String convertInput(String inputText) {
        StringBuilder newString = new StringBuilder(inputText.length());
        String[] tokens = inputText.split("[\\:\\}]");
        for (int i = 0; i < tokens.length; i++) {
            if (i % 2 > 0) {
                Character translatedChar = CHARACTER_NAME_MAP.get(tokens[i].toLowerCase());
                if (translatedChar == null) {
                    if (translatedChar == null) {
                        newString.append(':').append(tokens[i]).append(':');
                    } else {
                        appendCharacter(newString, translatedChar);
                    }
                } else {
                    appendCharacter(newString, translatedChar);
                }
            } else {
                newString.append(tokens[i]);
            }
        }
        return newString.toString();
    }

    private static void appendCharacter(StringBuilder string, Character character) {
        if (escape) {
            string.append("��f");
        }
        string.append(character);
        if (escape) {
            string.append("��0");
        }
    }
}
