package co.insecurity.springref.web.domain;

import java.util.HashMap;
import java.util.Map;

public class Alerts {

    public enum AlertType {
        SUCCESS("alert-success"), INFO("alert-info"), WARNING("alert-warning"), DANGER("alert-danger");

        private String alertClass;

        AlertType(String alertClass) {
            this.alertClass = alertClass;
        }

        public String toString() {
            return alertClass;
        }
    }

    private Map<String, AlertType> alerts;

    public Alerts() {
        alerts = new HashMap<String, AlertType>();
    }

    public void addAlert(String alert) {
        addAlert(alert, AlertType.INFO);
    }

    public void addAlert(String alert, AlertType type) {
        alerts.put(alert, type);
    }

    public Map<String, AlertType> getAlerts() {
        return this.alerts;
    }
}