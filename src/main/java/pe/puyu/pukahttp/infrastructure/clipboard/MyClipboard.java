package pe.puyu.pukahttp.infrastructure.clipboard;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MyClipboard {

    public static void copy(String text){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
