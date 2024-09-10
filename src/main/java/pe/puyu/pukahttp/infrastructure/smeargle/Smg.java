package pe.puyu.pukahttp.infrastructure.smeargle;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pe.puyu.pukahttp.infrastructure.smeargle.properties.SmgProperties;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgScale;
import pe.puyu.pukahttp.infrastructure.smeargle.styles.SmgStyle;

public class Smg {

    public static SmgProperties blockWidth(int charxels) {
        return SmgProperties.builder().setBlockWidth(charxels);
    }

    public static SmgStyle center(@Nullable Integer charxels) {
        return SmgStyle.builder().center().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle left(@Nullable Integer charxels) {
        return SmgStyle.builder().left().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle right(@Nullable Integer charxels) {
        return SmgStyle.builder().right().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle bold(@Nullable Boolean value) {
        return SmgStyle.builder().bold(value);
    }

    public static SmgStyle bold() {
        return Smg.bold(true);
    }

    public static SmgStyle normalize(@Nullable Boolean value) {
        return SmgStyle.builder().normalize(value);
    }

    public static SmgStyle normalize() {
        return Smg.normalize(true);
    }

    public static SmgStyle fontWidth(@Nullable Integer value) {
        return SmgStyle.builder().fontWidth(value);
    }

    public static SmgStyle fontHeight(@Nullable Integer value) {
        return SmgStyle.builder().fontHeight(value);
    }

    public static SmgStyle fontSize(@Nullable Integer value) {
        return SmgStyle.builder().fontSize(value);
    }

    public static SmgStyle bgInverted() {
        return Smg.bgInverted(true);
    }

    public static SmgStyle bgInverted(@Nullable Boolean value) {
        return SmgStyle.builder().bgInverted(value != null ? value : true);
    }

    public static SmgStyle pad(@Nullable Character character) {
        return SmgStyle.builder().pad(character);
    }

    public static SmgStyle charxels(@Nullable Integer value) {
        return SmgStyle.builder().charxels(value);
    }

    public static SmgStyle width(@Nullable Integer value) {
        return SmgStyle.builder().width(value);
    }

    public static SmgStyle height(@Nullable Integer value) {
        return SmgStyle.builder().height(value);
    }

    public static SmgStyle size(@Nullable Integer value) {
        return SmgStyle.builder().size(value);
    }

    public static SmgStyle scale(@Nullable SmgScale value) {
        return SmgStyle.builder().scale(value);
    }

    public static SmgStyle charCode(@Nullable String value) {
        return SmgStyle.builder().charCode(value);
    }

    public static SmgStyle leftBold(@Nullable Integer charxels) {
        return SmgStyle.builder().left().bold().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle rightBold(@Nullable Integer charxels) {
        return SmgStyle.builder().right().bold().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle centerBold(@Nullable Integer charxels) {
        return SmgStyle.builder().center().bold().ifThen(charxels != null, Smg.charxels(charxels));
    }

    public static SmgStyle ifThen(boolean flag, @NotNull SmgStyle trueStyle) {
        return SmgStyle.builder().ifThen(flag, trueStyle);
    }

    public static SmgStyle ifElse(boolean flag, @NotNull SmgStyle trueStyle, @NotNull SmgStyle falseStyle) {
        return SmgStyle.builder().ifElse(flag, trueStyle, falseStyle);
    }

    public static SmgStyle title() {
        return SmgStyle.builder().center().bold().fontSize(2).charxels(1000);
    }

    public static SmgStyle subtitle() {
        return SmgStyle.builder().left().bold().charxels(1000);
    }
}
