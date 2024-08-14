package pe.puyu.pukahttp.infrastructure.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckMenuItem;
import pe.puyu.pukahttp.application.services.LaunchApplicationService;
import pe.puyu.pukahttp.infrastructure.javafx.views.FxAlert;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;
import pe.puyu.pukahttp.infrastructure.properties.AppPropertyKey;
import pe.puyu.pukahttp.infrastructure.properties.ApplicationProperties;

public class TrayIconController {
    private final LaunchApplicationService launchApplicationService;

    public TrayIconController(LaunchApplicationService launchApplicationService) {
        this.launchApplicationService = launchApplicationService;
    }

    public void onAbout(ActionEvent ignored) {
        FxAlert.showInfo("PUYU S.R.L", "Print service for tickets, versión: " + AppConfig.getAppVersion());
    }

    public void onEnabledNotifications(ActionEvent ignored, CheckMenuItem node) {
        var isSelected = node.isSelected();
        node.setSelected(!isSelected);
        ApplicationProperties.setBoolean(AppPropertyKey.TRAY_NOTIFICATIONS, node.isSelected());
    }

    public void onClose(ActionEvent ignored) {
        boolean response = FxAlert.showConfirmation("Shutdown service", "Are you sure you want to exit?");
        if (response) {
            launchApplicationService.stopApplication();
        }
    }


}
