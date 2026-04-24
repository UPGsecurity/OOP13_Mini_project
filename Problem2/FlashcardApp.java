import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
 
import java.io.*;
import java.util.ArrayList;
 
public class FlashcardApp extends Application {
    private ArrayList<String[]> cards = new ArrayList<>();
    private int currentIndex = 0;
    private boolean showingQuestion = true;
 
    private Label indexLabel    = new Label();
    private Label cardLabel     = new Label();
    private Button flipBtn      = new Button("Flip");
    private Button nextBtn      = new Button("Next >");
    private Button prevBtn      = new Button("< Previous");
 
    @Override
    public void start(Stage stage) {
        loadCards();
 
        if (cards.isEmpty()) {
            Label msg = new Label("No cards found. Add cards to cards.txt and restart.");
            VBox root = new VBox(msg);
            root.setPadding(new Insets(30));
            stage.setScene(new Scene(root, 480, 300));
            stage.setTitle("Flashcard App");
            stage.show();
            return;
        }
 
        cardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        cardLabel.setWrapText(true);
        cardLabel.setAlignment(Pos.CENTER);
        cardLabel.setMaxWidth(400);
        cardLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 20;");
 
        flipBtn.setOnAction(e -> flipCard());
        nextBtn.setOnAction(e -> nextCard());
        prevBtn.setOnAction(e -> prevCard());
 
        HBox btnBox = new HBox(16, prevBtn, flipBtn, nextBtn);
        btnBox.setAlignment(Pos.CENTER);
 
        VBox root = new VBox(16, indexLabel, cardLabel, btnBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
 
        showCard(); 
 
        stage.setTitle("Flashcard Study App");
        stage.setScene(new Scene(root, 480, 300));
        stage.show();
    }
 
    private void loadCards() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("cards.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    cards.add(parts);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
 
    private void showCard() {
        showingQuestion = true;
        indexLabel.setText("Card " + (currentIndex + 1) + " / " + cards.size());
        cardLabel.setText(cards.get(currentIndex)[0]); // question side
        cardLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 20;");
    }
 
    private void flipCard() {
        if (showingQuestion) {
            cardLabel.setText(cards.get(currentIndex)[1]); // answer side
            cardLabel.setStyle("-fx-background-color: lightgreen; -fx-padding: 20;");
            showingQuestion = false;
        } else {
            cardLabel.setText(cards.get(currentIndex)[0]); // back to question
            cardLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 20;");
            showingQuestion = true;
        }
    }
 
    private void nextCard() {
        currentIndex = (currentIndex + 1) % cards.size();
        showCard();
    }
 
    private void prevCard() {
        currentIndex = (currentIndex - 1 + cards.size()) % cards.size();
        showCard();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
