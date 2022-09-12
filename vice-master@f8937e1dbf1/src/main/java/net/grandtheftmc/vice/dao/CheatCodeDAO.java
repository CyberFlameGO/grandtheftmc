package net.grandtheftmc.vice.dao;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.CheatCodeState;

import java.sql.*;
import java.util.HashMap;
import java.util.Optional;

public class CheatCodeDAO {

    public static Optional<HashMap<CheatCode, CheatCodeState>> getCheatCodes(String name) {
        //"Select * from " + Core.name() + " where  name='" + args[1] + "'"
        HashMap<CheatCode, CheatCodeState> map = Maps.newHashMap();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `cheatcodes` FROM " + Core.name() + " WHERE `name`=?;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Blob b = result.getBlob("cheatcodes");
                        if(b!=null) {
                            String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
                            for (String serializedCheatCode : cheatCodesBlob.split("-")) {
                                String[] split = serializedCheatCode.split("#");
                                map.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(map);
    }
}
