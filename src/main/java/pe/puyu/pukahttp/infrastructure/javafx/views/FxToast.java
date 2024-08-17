package pe.puyu.pukahttp.infrastructure.javafx.views;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class FxToast {
    private final Popup toast;
    private final PauseTransition transition;

    public FxToast(int seconds) {
        this.toast = new Popup();
        this.transition = new PauseTransition(Duration.seconds(seconds));
    }

    public void setDuration(int seconds) {
        this.transition.setDuration(Duration.seconds(seconds));
    }

    public void show(@NotNull Stage parentStage, @NotNull String text) {
        if (!transition.getStatus().equals(Animation.Status.RUNNING)) {
            toast.getContent().removeAll();
            Label message = new Label(text);
            message.setStyle("-fx-text-fill: #03f4fc; -fx-font-weight: bold; -fx-font-size: 16px");
            message.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.0), CornerRadii.EMPTY, Insets.EMPTY)));
            StackPane pane = new StackPane(message);
            pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.98); -fx-padding: 10px; -fx-background-radius: 5px;");
            pane.setOnMouseClicked(e -> {
                toast.hide();
                transition.stop();
            });
            toast.getContent().add(pane);
            toast.show(parentStage);
            transition.setOnFinished(e -> toast.hide());
            transition.play();
        }
    }

}
