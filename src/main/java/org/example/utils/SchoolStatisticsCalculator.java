package org.example.utils;

public class SchoolStatisticsCalculator {

    public static class SchoolStats {
        public final int total;
        public final int approved;
        public final int pending;
        public final int rejected;

        public SchoolStats(int total, int approved, int pending, int rejected) {
            this.total = total;
            this.approved = approved;
            this.pending = pending;
            this.rejected = rejected;
        }


        public String getFormattedSummary() {
            return String.format(
                "Total: %d schools | Approved: %d (clickable) | Pending: %d | Rejected: %d",
                total, approved, pending, rejected
            );
        }
    }


    public static SchoolStats calculateStats(Object[][] data) {
        int approvedCount = 0, pendingCount = 0, rejectedCount = 0;

        for (Object[] row : data) {
            String status = row[5] != null ? row[5].toString() : "";
            switch (status.toLowerCase()) {
                case "approved":
                    approvedCount++;
                    break;
                case "pending":
                    pendingCount++;
                    break;
                case "rejected":
                    rejectedCount++;
                    break;
            }
        }

        return new SchoolStats(data.length, approvedCount, pendingCount, rejectedCount);
    }
}
