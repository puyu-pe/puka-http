package pe.puyu.pukahttp.application.sweetticketdesign;

import org.jetbrains.annotations.NotNull;
import pe.puyu.SweetTicketDesign.application.components.DefaultComponentsProvider;
import pe.puyu.pukahttp.application.services.BusinessLogoService;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URLDecoder;

public class PrintComponentsProviderService extends DefaultComponentsProvider {
    private final BusinessLogoService businessLogoService;

    public PrintComponentsProviderService(BusinessLogoService businessLogoService) {
        this.businessLogoService = businessLogoService;
    }

    @Override
    public @NotNull String getImagePath() {
        try {
            String encodedPath = businessLogoService.getLogoUrl().getPath();
            return URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            return "";
        }
    }
}
