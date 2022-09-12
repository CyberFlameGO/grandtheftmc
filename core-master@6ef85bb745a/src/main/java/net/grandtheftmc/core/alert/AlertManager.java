package net.grandtheftmc.core.alert;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.alert.command.AlertCommand;
import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;
import net.grandtheftmc.core.database.dao.AlertsDAO;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public class AlertManager implements Component<AlertManager, Core> {

    private final List<Alert> alerts;
    private final List<Alert> polls;
//    private final MySQL mySQL;

    public AlertManager() {
//        this.mySQL = mySQL;
        this.alerts = Lists.newArrayList();
        this.polls = Lists.newArrayList();
        new AlertCommand(Core.getInstance(), this);
    }

    @Override
    public final AlertManager onEnable(Core plugin) {
        this.reload(this::log);
        return this;
    }

    @Override
    public final AlertManager onDisable(Core plugin) {
        if(this.alerts != null && !this.alerts.isEmpty())
            this.alerts.clear();

        return this;
    }

    public List<Alert> getAlerts() {
        return this.alerts;
    }

    public List<Alert> getAvailableAlerts() {
        return this.alerts.stream().filter(Alert::isInProgress).collect(Collectors.toList());
    }

    public Optional<Alert> getAvailableAlertById(int id) {
        return this.alerts.stream().filter(a -> a.isInProgress() && a.getUniqueIdentifier() == id).findFirst();
    }

    public Optional<Alert> getAlertById(int id) {
        return this.alerts.stream().filter(a -> a.getUniqueIdentifier() == id).findFirst();
    }

    public Optional<Alert> getAlertById(String image) {
        return this.alerts.stream().filter(a -> a.getImageUrl().equals(image)).findFirst();
    }

    public final AlertManager reload(Callback<Boolean> callback) {
        this.alerts.clear();
        this.polls.clear();

        ServerUtil.runTaskAsync(() -> {
            createTable();

            Optional<List<Alert>[]> optional = AlertsDAO.fetchAllAlerts();
            if(!optional.isPresent()) return;
            this.alerts.addAll(optional.get()[0]);
            this.polls.addAll(optional.get()[1]);

            callback.call(true);
        });

        return this;
    }

    private void createTable() {
//        String query = "CREATE TABLE IF NOT EXISTS `alerts` " +
//                "(" +
//                "`id` int NOT NULL AUTO_INCREMENT," +
//                "`name` varchar(255) NOT NULL," +
//                "`description` varchar(255) DEFAULT 'none'," +
//                "`image` varchar(255)," +
//                "`link` varchar(255)," +
//                "`showType` varchar(32) NOT NULL," +
//                "`type` varchar(32) NOT NULL," +
//                "`disabled` varchar(6)," +
//                "`start` timestamp NOT NULL," +
//                "`end` timestamp NOT NULL," +
//                "`player` varchar(32) NOT NULL," +
//                "`addon` longtext," +
//                "PRIMARY KEY (`id`)" +
//                ");";
//        PreparedStatement statement = this.mySQL.prepareStatement(query);
//        statement.execute();
//        statement.close();
        AlertsDAO.createAlertsTable();

//        String query2 = "CREATE TABLE IF NOT EXISTS `alert_users` " +
//                "(" +
//                "`uuid` varchar(36) NOT NULL," +
//                "`id` int NOT NULL," +
//                "`complete` varchar(6) NOT NULL," +
//                "`input` longtext" +
//                ");";
//        PreparedStatement statement2 = this.mySQL.prepareStatement(query2);
//        statement2.execute();
//        statement2.close();
        AlertsDAO.createAlertUserTable();
    }

    public void insertAlert(Alert alert, Callback<Boolean> callback) {
        if(alert.getAlertType() == AlertType.POLL) return;
        ServerUtil.runTaskAsync(() -> callback.call(AlertsDAO.insertAlert(alert)));
    }

    public void updateAlert(Alert alert, Callback<Boolean> callback) {
        if(alert.getAlertType() == AlertType.POLL) return;

        ServerUtil.runTaskAsync(() -> callback.call(AlertsDAO.updateAlert(alert)));
    }

    public void alertShown(Player player, Alert alert, Callback<Boolean> callback) {
        if(player == null) return;
        if(alert == null) return;
        if(alert.getAlertType() == AlertType.POLL) return;
        if(alert.getShowType() == AlertShowType.REPEAT) return;

        ServerUtil.runTaskAsync(() -> callback.call(AlertsDAO.insertAlertUser(player, alert)));
    }

    public void deleteAlert(Player player, Alert alert, Callback<Boolean> callback) {
        if(player == null || alert == null) {
            callback.call(false);
            return;
        }

        ServerUtil.runTaskAsync(() -> callback.call(AlertsDAO.deleteAlert(alert)));
    }

    /**
     * This is ran on no thread, handle that yourself!
     *
     * @param uuid
     * @param callback
     */
    public void getAvailableAlertsForPlayer(UUID uuid, Callback<List<Alert>> callback) {
        callback.call(AlertsDAO.fetchAlertsForPlayer(this, uuid).orElse(Lists.newArrayList()));
    }
}
