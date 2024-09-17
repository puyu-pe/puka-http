package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.Context;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import pe.puyu.pukahttp.infrastructure.config.AppConfig;

import java.util.Base64;

public class PukaController {
    private final BusinessLogoService businessLogoService;

    public PukaController() {
        this.businessLogoService = new BusinessLogoService(AppConfig.getLogoFilePath());
    }

    public void saveLogo(Context ctx) {
        ctx.async(() -> {
            String base64Logo = ctx.body();
            byte[] logoBytes = Base64.getDecoder().decode(base64Logo);
            businessLogoService.save(logoBytes);
        });
    }
}
