package pe.puyu.pukahttp.infrastructure.javalin.controllers;

import io.javalin.http.Context;
import pe.puyu.pukahttp.application.services.BusinessLogoService;

import java.io.IOException;
import java.util.Base64;

public class PukaController {
    private final BusinessLogoService businessLogoService;

    public PukaController(BusinessLogoService businessLogoService) {
        this.businessLogoService = businessLogoService;
    }

    public void saveLogo(Context ctx) {
        ctx.async(() -> {
            try {
                String base64Logo = ctx.body();
                byte[] logoBytes = Base64.getDecoder().decode(base64Logo);
                businessLogoService.save(logoBytes);
            } catch (IOException e) {
                throw new RuntimeException("Error on save logo: " + e.getMessage());
            }
        });
    }
}
