package com.example.notepadclone;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.*;
import java.util.Optional;
public class NotepadClone extends Application {
    private TextArea textArea;
    private Stage primaryStage;
    private File currentFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Notepad Clone");

        textArea = new TextArea();
        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

//        primaryStage.setOnCloseRequest(event -> {
//            if (unsavedChangesDialog()) {
//                event.consume();
//            }
//        });

        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem saveAsMenuItem = new MenuItem("Save As");
        MenuItem exitMenuItem = new MenuItem("Exit");

        newMenuItem.setOnAction(e -> newFile());
        openMenuItem.setOnAction(e -> openFile());
        saveMenuItem.setOnAction(e -> saveFile());
        saveAsMenuItem.setOnAction(e -> saveAsFile());
        exitMenuItem.setOnAction(e -> exit());

        fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, new SeparatorMenuItem(), exitMenuItem);

        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem cutMenuItem = new MenuItem("Cut");
        MenuItem copyMenuItem = new MenuItem("Copy");
        MenuItem pasteMenuItem = new MenuItem("Paste");
        MenuItem findMenuItem = new MenuItem("Find");
        MenuItem replaceMenuItem = new MenuItem("Replace");

        cutMenuItem.setOnAction(e -> cutText());
        copyMenuItem.setOnAction(e -> copyText());
        pasteMenuItem.setOnAction(e -> pasteText());
        findMenuItem.setOnAction(e -> findText());
        replaceMenuItem.setOnAction(e -> replaceText());

        editMenu.getItems().addAll(cutMenuItem, copyMenuItem,pasteMenuItem, new SeparatorMenuItem(), findMenuItem, replaceMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        return menuBar;
    }

    private void pasteText() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String content = clipboard.getString();
            int caretPosition = textArea.getCaretPosition();
            textArea.insertText(caretPosition, content);
        }
    }

    private void copyText() {
        String selectedText = textArea.getSelectedText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(selectedText);
        clipboard.setContent(content);
    }

    private void cutText() {
        String selectedText = textArea.getSelectedText();
        textArea.cut();
    }

    private void toggleBoldText() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();

        if (start >= 0 && end >= 0) {
            String selectedText = textArea.getText(start, end);

            // Check if the selected text is already bold
            if (selectedText.contains("-fx-font-weight: bold;")) {
                // Remove the bold style
                selectedText = selectedText.replace("-fx-font-weight: bold;", "");
            } else {
                // Add the bold style
                selectedText = "-fx-font-weight: bold;" + selectedText;
            }

            textArea.replaceText(start, end, selectedText);
        }
    }

    private void toggleItalicText() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();

        if (start >= 0 && end >= 0) {
            String selectedText = textArea.getText(start, end);
            Font currentFont = textArea.getFont();

            // Check if the selected text is already italic
            if (currentFont.getStyle().contains("Italic") || currentFont.getStyle().contains("ITALIC")) {
                Font newFont = Font.font(currentFont.getFamily(), FontPosture.REGULAR, currentFont.getSize());
                textArea.setFont(newFont);
            } else {
                Font newFont = Font.font(currentFont.getFamily(), FontPosture.ITALIC, currentFont.getSize());
                textArea.setFont(newFont);
            }
        }
    }
















    private void newFile() {
        if (unsavedChangesDialog()) {
            textArea.clear();
            currentFile = null;
        }
    }

    private void openFile() {
        if (unsavedChangesDialog()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    textArea.setText(content.toString());
                    currentFile = selectedFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveAsFile();
        }
    }

    private void saveAsFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(primaryStage);

        if (selectedFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(textArea.getText());
                currentFile = selectedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exit() {
        if (unsavedChangesDialog()) {
            primaryStage.close();
        }
    }

    private boolean unsavedChangesDialog() {
        if (textArea.getText().isEmpty()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes. Do you want to save them?");
        ButtonType saveButton = new ButtonType("Save");
        ButtonType discardButton = new ButtonType("Discard");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == saveButton) saveFile();
            else return result.get() == discardButton;
        }
        return true;
    }

    private void findText() {
        // Create a dialog to input the text to find
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Text");
        dialog.setHeaderText("Enter the text to find:");
        dialog.setContentText("Text:");

        // Get user input
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(textToFind -> {
            String content = textArea.getText();
            int index = content.indexOf(textToFind);

            if (index != -1) {
                // Select the found text in the TextArea
                textArea.selectRange(index, index + textToFind.length());
            } else {
                // If text is not found, show a message
                Alert notFoundAlert = new Alert(Alert.AlertType.INFORMATION);
                notFoundAlert.setTitle("Text Not Found");
                notFoundAlert.setHeaderText(null);
                notFoundAlert.setContentText("The specified text was not found.");
                notFoundAlert.showAndWait();
            }
        });
    }


    private void replaceText() {
        // Create a dialog to input the text to find and replace
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Replace Text");
        dialog.setHeaderText("Enter the text to find and the text to replace it with:");

        // Set the button types
        ButtonType replaceButtonType = new ButtonType("Replace", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(replaceButtonType, ButtonType.CANCEL);

        // Create input fields for find and replace text
        TextField findText = new TextField();
        findText.setPromptText("Text to Find");
        TextField replaceWithText = new TextField();
        replaceWithText.setPromptText("Replace with");

        // Add input fields to the dialog
        GridPane grid = new GridPane();
        grid.add(new Label("Find:"), 0, 0);
        grid.add(findText, 1, 0);
        grid.add(new Label("Replace with:"), 0, 1);
        grid.add(replaceWithText, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a pair of find and replace text when the replace button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == replaceButtonType) {
                return new Pair<>(findText.getText(), replaceWithText.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            String content = textArea.getText();
            String newText = content.replace(pair.getKey(), pair.getValue());
            textArea.setText(newText);
        });
    }


//    public static void main(String[] args) {
//        launch(args);
//    }
}