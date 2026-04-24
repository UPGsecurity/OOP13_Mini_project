import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CountdownTimer extends Application {

    private TextField minutesField  = new TextField();
    private Label timeLabel         = new Label("00:00");
    private Label errorLabel        = new Label();
    private Button pauseResumeBtn   = new Button("Pause");

    private Timeline timeline;
    private int remainingSeconds = 0;

    @Override
    public void start(Stage stage) {
    
        timeLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 52));
        timeLabel.setAlignment(Pos.CENTER);

        minutesField.setPromptText("Enter minutes");
        minutesField.setMaxWidth(150);

        Button startBtn = new Button("Start");
        Button resetBtn = new Button("Reset");

        startBtn.setOnAction(e -> startTimer());
        pauseResumeBtn.setOnAction(e -> pauseResume());
        resetBtn.setOnAction(e -> resetTimer());

        HBox controls = new HBox(10, minutesField, startBtn, pauseResumeBtn, resetBtn);
        controls.setAlignment(Pos.CENTER);

        errorLabel.setTextFill(Color.RED);

        VBox root = new VBox(16, timeLabel, controls, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setTitle("Countdown Timer");
        stage.setScene(new Scene(root, 340, 260));
        stage.show();
    }

    private void startTimer() {
        String input = minutesField.getText().trim();
        int minutes;

        try {
            minutes = Integer.parseInt(input);
            if (minutes <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid positive number!");
            return;
        }

        errorLabel.setText("");
        remainingSeconds = minutes * 60;
        updateDisplay();

        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingSeconds--;
            updateDisplay();

            if (remainingSeconds <= 0) {
                timeline.stop();
                timeExpired();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        pauseResumeBtn.setText("Pause");
        timeLabel.setTextFill(Color.BLACK); 
    }

    private void pauseResume() {
        if (timeline == null) return;

        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            pauseResumeBtn.setText("Resume");
        } else {
            timeline.play();
            pauseResumeBtn.setText("Pause");
        }
    }

    private void resetTimer() {
        if (timeline != null) timeline.stop();
        remainingSeconds = 0;
        timeLabel.setText("00:00");
        timeLabel.setTextFill(Color.BLACK);
        timeLabel.setOpacity(1.0);
        pauseResumeBtn.setText("Pause");
        errorLabel.setText("");
    }

    private void updateDisplay() {
        int mins = remainingSeconds / 60;
        int secs = remainingSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", mins, secs));
    }

    private void timeExpired() {
        timeLabel.setTextFill(Color.RED);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), timeLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.1);
        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
