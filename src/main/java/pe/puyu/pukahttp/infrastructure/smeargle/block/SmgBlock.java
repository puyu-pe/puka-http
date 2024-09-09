package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface SmgBlock {

    @NotNull
    JsonObject toJson();

}
