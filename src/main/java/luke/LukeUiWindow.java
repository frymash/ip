package luke;

import java.io.IOException;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import luke.command.Command;
import luke.env.Constants;

/**
 * The {@code LukeUiWindow} class represents the main user interface (UI) window for interacting with Luke.
 * This class is responsible for handling user inputs, displaying Luke's responses, and managing the dialog flow
 * between the user and the application.
 * <p>
 *
 * Key features:
 * <ul>
 *   <li>Displays welcome messages and instructions for the user</li>
 *   <li>Handles the user's input and provides responses from the {@link Luke} instance</li>
 *   <li>Loads previous task data from storage upon initialisation</li>
 *   <li>Allows the user to create a new save file if one is missing</li>
 * </ul>
 *
 * @see Luke
 * @see DialogBox
 * @see Storage
 */
public class LukeUiWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Luke luke;

    // Images were taken from the website AI Images (CC BY-ND 4.0).
    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/ewok.png"));
    private Image lukeImage = new Image(this.getClass().getResourceAsStream("/images/skywalker.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        String welcome = """
                hey, i'm luke, your glorified task manager.

                i can keep a list of your todos, events, and deadlines.

                tell me what you need to keep track of. i'll help you out :)
                """;
        dialogContainer.getChildren().add(DialogBox.getLukeDialog(welcome, lukeImage));
        try {
            List<String> lines = Storage.loadData();
            for (String line : lines) {
                Command command = Parser.parseSavedData(line);
                Ui.handleCommand(command, true);
            }
        } catch (IOException e) {
            String warning = "hmmm... i ran into an issue while setting up. try launching me again.";
            dialogContainer.getChildren().add(DialogBox.getLukeDialog(warning, lukeImage));
        }
    }

    /**
     * Injects the Duke instance
     */
    public void setLuke(Luke l) {
        luke = l;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Luke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = luke.getOutput(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLukeDialog(response, lukeImage)
        );
        userInput.clear();
        if (response.equals(Constants.BYE_MESSAGE)) {
            // Enforce a 1.5-second pause and exit after the pause
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
