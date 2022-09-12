package net.grandtheftmc.core.anticheat.report.data;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public final class PlayerReportData {

    private final List<ReportData> reports;

    private UUID uniqueId;
    private long lastReport = -1;

    public PlayerReportData(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.reports = Lists.newArrayList();
    }

    public List<ReportData> getReports() {
        return reports;
    }

    public void addReport(ReportData reportData) {
        reports.add(reportData);
        lastReport = System.currentTimeMillis();
    }

    public boolean canReport() {
        return (System.currentTimeMillis() - lastReport) > (1000 * 60 * 5);
    }

    public long getIssuedReports() {
        return reports.stream().filter(r -> !r.isPersonal(uniqueId)).count();
    }

    public long getPersonalReports() {
        return reports.stream().filter(r -> r.isPersonal(uniqueId)).count();
    }
}
