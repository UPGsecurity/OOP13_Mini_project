import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;

public class ExpenseTracker extends Application {

    private TextField categoryField = new TextField();
    private TextField amountField   = new TextField();
    private TextField noteField     = new TextField();
    private Label statusLabel       = new Label();
    private TextArea summaryArea    = new TextArea();

    @Override
    public void start(Stage stage) {
  
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(new Label("Category:"), 0, 0);
        form.add(categoryField,          1, 0);
        form.add(new Label("Amount:"),   0, 1);
        form.add(amountField,            1, 1);
        form.add(new Label("Note:"),     0, 2);
        form.add(noteField,              1, 2);
        Button addBtn     = new Button("Add Expense");
        Button summaryBtn = new Button("Show Summary");
        Button clearBtn   = new Button("Clear Fields");

        addBtn.setOnAction(e -> addExpense());
        summaryBtn.setOnAction(e -> showSummary());
        clearBtn.setOnAction(e -> clearFields());

        HBox buttons = new HBox(10, addBtn, summaryBtn, clearBtn);
        summaryArea.setEditable(false);
        summaryArea.setPrefHeight(150);

        VBox root = new VBox(12, form, buttons, statusLabel, summaryArea);
        root.setPadding(new Insets(20));

        stage.setTitle("Expense Tracker");
        stage.setScene(new Scene(root, 460, 420));
        stage.show();
    }

    private void addExpense() {
        String category = categoryField.getText().trim();
        String amountStr = amountField.getText().trim();
        String note = noteField.getText().trim();

        // Validate
        if (category.isEmpty() || amountStr.isEmpty()) {
            showStatus("Category and Amount cannot be empty!", false);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            BufferedWriter bw = new BufferedWriter(new FileWriter("expenses.txt", true));
            bw.write(category + "|" + amount + "|" + note);
            bw.newLine();
            bw.close();

            showStatus("Saved!", true);
            clearFields();

        } catch (NumberFormatException e) {
            showStatus("Amount must be a valid number!", false);
        } catch (IOException e) {
            showStatus("Error saving file: " + e.getMessage(), false);
        }
    }

    private void showSummary() {
        StringBuilder sb = new StringBuilder();
        double total = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("expenses.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String cat    = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String note   = parts.length >= 3 ? parts[2] : "";
                    total += amount;
                    sb.append(cat).append(" --- $").append(String.format("%.2f", amount));
                    if (!note.isEmpty()) sb.append(" (").append(note).append(")");
                    sb.append("\n");
                }
            }
            br.close();

            sb.append("\nTotal: $").append(String.format("%.2f", total));
            summaryArea.setText(sb.toString());

        } catch (FileNotFoundException e) {
            summaryArea.setText("No expenses recorded yet.");
        } catch (IOException e) {
            summaryArea.setText("Error reading file.");
        }
    }

    private void clearFields() {
        categoryField.clear();
        amountField.clear();
        noteField.clear();
        statusLabel.setText("");
    }
    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(success ? Color.GREEN : Color.RED);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
