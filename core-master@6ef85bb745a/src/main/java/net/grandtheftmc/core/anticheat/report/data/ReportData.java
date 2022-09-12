package net.grandtheftmc.core.anticheat.report.data;

import net.grandtheftmc.core.servers.ServerType;

import java.util.UUID;

public final class ReportData {

    private final UUID reporterUniqueId, victimUniqueId;
    private final String reporterName, victimName, reason;
    private final long timeReported;
    private final ServerType serverType;
    private final int serverId;

    public ReportData(UUID reporterUniqueId, UUID victimUniqueId, String reporterName, String victimName, String reason, long timeReported, ServerType serverType, int serverId) {
        this.reporterUniqueId = reporterUniqueId;
        this.victimUniqueId = victimUniqueId;
        this.reporterName = reporterName;
        this.victimName = victimName;
        this.reason = reason;
        this.timeReported = timeReported;
        this.serverType = serverType;
        this.serverId = serverId;
    }

    public boolean isPersonal(UUID uuid) {
        return this.victimUniqueId.equals(uuid);
    }

    public UUID getReporterUniqueId() {
        return reporterUniqueId;
    }

    public UUID getVictimUniqueId() {
        return victimUniqueId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getVictimName() {
        return victimName;
    }

    public String getReason() {
        return reason;
    }

    public long getTimeReported() {
        return timeReported;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getServerId() {
        return serverId;
    }
}
