package net.grandtheftmc.gtm.gangs;

public class GangManager {
//    private final Map<Integer, Gang> mappedGangs = new HashMap<>();
//
//    public static Map<Gang, Integer> sort(Map<Gang, Integer> unsortMap) {
//        List<Map.Entry<Gang, Integer>> list = new LinkedList<>(unsortMap.entrySet());
//        list.sort(Comparator.comparing(Map.Entry::getValue));
//        Map<Gang, Integer> sortedMap = new LinkedHashMap<>();
//        for (Map.Entry<Gang, Integer> entry : list) {
//            sortedMap.put(entry.getKey(), entry.getValue());
//        }
//        return sortedMap;
//    }
//
//    public Collection<Gang> getLoadedGangs() {
//        return mappedGangs.values();
//    }
//
//    public boolean unloadGang(int id) {
//        return mappedGangs.remove(id) != null;
//    }
//
//    public boolean isLoaded(int id) {
//        return mappedGangs.containsKey(id);
//    }
//
//    public Gang getAlreadyLoadedGang(int id) {
//        return mappedGangs.get(id);
//    }
//
//    public Gang getLoadedGang(int id) {
//        Gang gang = mappedGangs.get(id);
//
//        if (gang == null) {
//            gang = new Gang(id);
//            mappedGangs.put(id, gang);
//        }
//
//        return gang;
//    }
//
//    public void addLoadedGang(Gang gang) {
//        mappedGangs.put(gang.getUniqueId(), gang);
//    }
//
//    public Set<Gang> getGangsByOnlineMembers() {
//        Map<Gang, Integer> unsortMap = new HashMap<>();
//        for (Gang g : this.getLoadedGangs()) {
//            unsortMap.put(g, g.getOnlineMembers());
//        }
//        return sort(unsortMap).keySet();
//    }
//
//    public boolean isValid(String gangName) {
//        boolean b = false;
//
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_gangs where name='" + gangName + "';")) {
//                try (ResultSet result = statement.executeQuery()) {
//                    b = result.next();
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return b;
//    }

}
