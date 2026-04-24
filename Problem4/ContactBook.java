import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
import java.io.*;
import java.util.ArrayList;
 
public class ContactBook extends Application {
 
    private Stage stage;
    private ListView<String> contactList = new ListView<>();
    private Label scene1Error = new Label();
 
    private TextField nameField  = new TextField();
    private TextField phoneField = new TextField();
    private TextField emailField = new TextField();
    private Label scene2Error    = new Label();
 
    private Scene scene1, scene2;
 
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
 
        scene1 = buildScene1();
        scene2 = buildScene2();
 
        loadContacts();
 
        stage.setTitle("Contact Book");
        stage.setScene(scene1);
        stage.show();
    }
 
    private Scene buildScene1() {
        Button addBtn     = new Button("Add New");
        Button deleteBtn  = new Button("Delete Selected");
        Button refreshBtn = new Button("Refresh");
 
        addBtn.setOnAction(e -> stage.setScene(scene2));
        deleteBtn.setOnAction(e -> deleteSelected());
        refreshBtn.setOnAction(e -> loadContacts());
 
        HBox buttons = new HBox(10, addBtn, deleteBtn, refreshBtn);
        scene1Error.setTextFill(Color.RED);
 
        VBox root = new VBox(12, new Label("Contacts:"), contactList, buttons, scene1Error);
        root.setPadding(new Insets(20));
 
        return new Scene(root, 480, 380);
    }
 
    private void loadContacts() {
        contactList.getItems().clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader("contacts.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length >= 3) {
                    contactList.getItems().add(p[0] + " -- " + p[1] + "@" + p[2]);
                }
            }
            br.close();
            scene1Error.setText("");
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            scene1Error.setText("Error loading contacts.");
        }
    }
 
    private void deleteSelected() {
        int selectedIndex = contactList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            scene1Error.setText("Please select a contact to delete.");
            return;
        }
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("contacts.txt"));
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
            br.close();
        } catch (IOException e) {
            scene1Error.setText("Error reading file.");
            return;
        }
 
        lines.remove(selectedIndex);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("contacts.txt", false));
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.close();
            scene1Error.setText("");
            loadContacts();
        } catch (IOException e) {
            scene1Error.setText("Error deleting contact.");
        }
    }
 
    private Scene buildScene2() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(20));
 
        form.add(new Label("Name:"),  0, 0); form.add(nameField,  1, 0);
        form.add(new Label("Phone:"), 0, 1); form.add(phoneField, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(emailField, 1, 2);
 
        Button saveBtn   = new Button("Save");
        Button cancelBtn = new Button("Cancel");
 
        saveBtn.setOnAction(e -> saveContact());
        cancelBtn.setOnAction(e -> {
            clearScene2Fields();
            stage.setScene(scene1);
        });
 
        scene2Error.setTextFill(Color.RED);
        HBox buttons = new HBox(10, saveBtn, cancelBtn);
 
        VBox root = new VBox(12, new Label("Add New Contact"), form, buttons, scene2Error);
        root.setPadding(new Insets(20));
 
        return new Scene(root, 480, 380);
    }
 
    private void saveContact() {
        String name  = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
 
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            scene2Error.setText("All fields are required!");
            return;
        }
 
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("contacts.txt", true));
            bw.write(name + "|" + phone + "|" + email);
            bw.newLine();
            bw.close();
 
            clearScene2Fields();
            loadContacts(); 
            stage.setScene(scene1);
 
        } catch (IOException e) {
            scene2Error.setText("error saving contact.");
        }
    }
 
    private void clearScene2Fields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        scene2Error.setText("");
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
