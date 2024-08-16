package pe.puyu.pukahttp.infrastructure.smeargle.block;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public interface SmgBlock {

    @Nullable
    JsonObject toJson();

}
