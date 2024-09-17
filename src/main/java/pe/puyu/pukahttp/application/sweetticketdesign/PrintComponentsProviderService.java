package pe.puyu.pukahttp.application.sweetticketdesign;

import org.jetbrains.annotations.NotNull;
import pe.puyu.SweetTicketDesign.application.components.DefaultComponentsProvider;
import pe.puyu.pukahttp.application.services.BusinessLogoService;

import java.net.MalformedURLException;

public class PrintComponentsProviderService extends DefaultComponentsProvider {
    private final BusinessLogoService businessLogoService;

    public PrintComponentsProviderService(BusinessLogoService businessLogoService) {
        this.businessLogoService = businessLogoService;
    }

    @Override
    public @NotNull String getImagePath() {
        try {
            return businessLogoService.getLogoUrl().getPath();
        } catch (MalformedURLException e) {
            return "";
        }
    }
}
