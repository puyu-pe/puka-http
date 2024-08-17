package pe.puyu.pukahttp.infrastructure.smeargle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pe.puyu.pukahttp.infrastructure.smeargle.block.SmgBlock;
import pe.puyu.pukahttp.infrastructure.smeargle.drawer.SmgDrawer;
import pe.puyu.pukahttp.infrastructure.smeargle.properties.SmgProperties;

import java.util.Optional;

public class SmgPrintObject {

    private final @NotNull JsonArray data;
    private final @NotNull JsonObject printObject;

    private SmgPrintObject() {
        this.data = new JsonArray();
        this.printObject = new JsonObject();
    }

    public SmgPrintObject block(@NotNull SmgBlock block) {
        Optional.ofNullable(block.toJson()).ifPresent(data::add);
        return this;
    }

    public SmgPrintObject text(@NotNull String text) {
        data.add(text);
        return this;
    }

    public JsonObject toJson() {
        printObject.add("data", data);
        return printObject;
    }

    public String toJsonString() {
        return toJson().toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SmgPrintObject build(){
        return builder().build();
    }

    public static SmgPrintObject properties(@NotNull SmgProperties properties) {
        return builder().properties(properties).build();
    }

    public static SmgPrintObject openDrawer(@NotNull SmgDrawer drawer) {
        return builder().openDrawer(drawer).build();
    }

    public static SmgPrintObject openDrawer() {
        return builder().openDrawer(SmgDrawer.pin(SmgDrawer.Pin._2)).build();
    }

    public static class Builder {
        private final SmgPrintObject value;

        public Builder() {
            this.value = new SmgPrintObject();
        }

        public SmgPrintObject build() {
            return this.value;
        }

        public Builder properties(@NotNull SmgProperties properties) {
            this.value.printObject.add("properties", SmgProperties.builder(properties).build().toJson());
            return this;
        }

        public Builder openDrawer(@NotNull SmgDrawer drawer) {
            Optional.ofNullable(drawer.toJson()).ifPresent(json -> this.value.printObject.add("openDrawer", json));
            return this;
        }
    }

}
