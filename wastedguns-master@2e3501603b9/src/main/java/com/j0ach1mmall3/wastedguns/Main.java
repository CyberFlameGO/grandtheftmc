package com.j0ach1mmall3.wastedguns;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class Main {
//    private final Map<LivingEntity, List<Integer>> shootingLivingEntities = new HashMap<>();
//    private final Map<LivingEntity, LivingEntity> homingTargets = new HashMap<>();
//    private final Map<LivingEntity, Set<Entity>> stickyBombs = new HashMap<>();
//    private final Set<LivingEntity> reloadingLivingEntities = new HashSet<>();
//    private final Map<String, Long> cooldownLivingEntities = new HashMap<>();
//    private final Map<LivingEntity, Integer> burstShots = new HashMap<>();
//    private final Map<LivingEntity, Integer> burstTicks = new HashMap<>();

//    private final HashMap<UUID, PlayerCache> playerCacheMap = Maps.newHashMap();
//
//    private final Map<Block, Material> blockQueue = new HashMap<>();
//    private final Set<Entity> entityQueue = new HashSet<>();
//
//    private Collection<Material> ignoreBlocks = new ArrayList<>();
//
//    private Attachments attachments;
//    private Explosives explosives;
//    private Melee melee;
//    private Ranged ranged;
//    private Airstrikes airstrikes;
//
//    private EffectManager manager;
//    private static Main wastedGuns;
//
//    @Override
//    public void onEnable() {
//        this.reload();
//        new PlayerListener(this);
//        new EntityListener(this);
//        new WeaponsListener(this);
//        new GiveWeaponCommandHandler(this).registerCommand(new Command(this, "GiveWeapon", "wg.giveweapon", ChatColor.RED + "Usage: /giveweapon <identifier> <player>"));
//        new WGReloadCommandHandler(this).registerCommand(new Command(this, "WGReload", "wg.reload", ChatColor.RED + "Usage: /wgreload"));
//        this.manager = new EffectManager(this);
//        wastedGuns = this;
//    }
//
//    @Override
//    public void onDisable() {
//        this.blockQueue.entrySet().forEach(e -> e.getKey().setType(e.getValue()));
//        this.entityQueue.forEach(Entity::remove);
////        this.stickyBombs.entrySet().forEach(e -> e.getValue().stream().filter(ent -> !(ent instanceof Player)).forEach(Entity::remove));
//        manager.dispose();
//    }
//
//    public void reload() {
//        this.config = new Config(this);
//        this.attachments = new Attachments(this);
//        this.explosives = new Explosives(this);
//        this.melee = new Melee(this);
//        this.ranged = new Ranged(this);
//        this.airstrikes = new Airstrikes(this);
//    }
//
//    public static Main getWastedGuns() {
//        return wastedGuns;
//    }
//
//    public EffectManager getEffectManager() {
//        return this.manager;
//    }
//
//    public HashMap<UUID, PlayerCache> getPlayerCacheMap() {
//        return this.playerCacheMap;
//    }
//
//    public PlayerCache getPlayerCache(UUID uniqueId) {
//        return this.playerCacheMap.containsKey(uniqueId) ? this.getPlayerCache(uniqueId) : null;
//    }
//
//    public Map<Block, Material> getBlockQueue() {
//        return this.blockQueue;
//    }
//
//    public Set<Entity> getEntityQueue() {
//        return this.entityQueue;
//    }
//
//    public Attachments getAttachments() {
//        return this.attachments;
//    }
//
//    public Explosives getExplosives() {
//        return this.explosives;
//    }
//
//    public Melee getMelee() {
//        return this.melee;
//    }
//
//    public Ranged getRanged() {
//        return this.ranged;
//    }
//
//    public Set<Weapon<?>> getWeapons() {
//        return null;
//    }

//    public Optional<Weapon> getWeapon(ItemStack itemStack) {
//        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return Optional.empty();
//
//        Optional<Weapon> weapon = WeaponType.getWeaponsFromType(WeaponType.Type.THROWABLE).stream().filter(w -> General.areSimilar(w.getItemStack(), itemStack)).findFirst();
//        if (!weapon.isPresent())
//            weapon = WeaponType.getWeaponsFromType(WeaponType.Type.DROPPABLE).stream().filter(w -> General.areSimilar(w.getItemStack(), itemStack)).findFirst();
//        if (!weapon.isPresent())
//            weapon = WeaponType.getWeaponsFromType(WeaponType.Type.MELEE).stream().filter(w -> w.getItemStack().getType() == itemStack.getType() && w.getItemStack().getData().equals(itemStack.getData())).findFirst();
//        if (!weapon.isPresent())
//            weapon = WeaponType.getWeaponsFromType(WeaponType.Type.RANGED).stream().filter(w -> w.getItemStack().getType() == itemStack.getType() && itemStack.getItemMeta().getDisplayName().contains("«") && itemStack.getItemMeta().getDisplayName().contains("»")).findFirst();
//
//        if (weapon.isPresent()) {
//            Weapon w = weapon.get().clone();
//            Attachment.getAttachments(this, itemStack).forEach(a -> a.apply(w));
//            return Optional.of(w);
//        }
//
//        return weapon;
//    }
//
//    public Optional<Weapon> getWeapon(String identifier) {
//        if (identifier == null) return Optional.empty();
//        Optional<Weapon> weapon = this.getWeapons().stream().filter(w -> w.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
//        weapon.ifPresent(w -> {
//            if (w instanceof ThrowableWeapon) return;
//            try {
//                w.setItemStack(NBTTags.setNbtTag(w.getItemStack(), "_", UUID.randomUUID().toString()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        return weapon;
//    }
//
//    public Optional<Attachment> getAttachment(String identifier) {
//        if (identifier == null) return Optional.empty();
//        return this.attachments.getAttachments().stream().filter(a -> a.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
//    }
//
//    public Collection<Material> getIgnoreBlocks() {
//        if (this.ignoreBlocks == null || this.ignoreBlocks.isEmpty()) {
//            this.ignoreBlocks = Arrays.asList(Material.SIGN, Material.SIGN_POST,
//                    Material.WALL_SIGN, Material.BREWING_STAND, Material.DROPPER,
//                    Material.CHEST, Material.ENDER_CHEST, Material.TRAPPED_CHEST,
//                    Material.IRON_DOOR_BLOCK, Material.IRON_DOOR, Material.ANVIL,
//                    Material.CARPET);
//        }
//        return this.ignoreBlocks;
//    }
}
