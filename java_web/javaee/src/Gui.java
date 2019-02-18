import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class Gui extends Application {
    /* Message area */
    private TextArea textArea;

    /* The primary stage of the JavaFX application */
    private Stage st;

    private Button search;

    private Image google;

    /* The input dialog to get the query */
    private TextInputDialog dialog;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Search Engine");
        this.st = stage;

        BorderPane border = new BorderPane(); // Layout of the search engine.

        google = new Image("file:img\\google.png");
        ImageView imageView = new ImageView();
        imageView.setImage(google);
        imageView.setFitHeight(100);
        imageView.setFitWidth(200);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        EventHandler<ActionEvent> eh = new ButtonDoer();

        textArea = new TextArea(); // get the information of web pages
        textArea.setPrefWidth(700);
        textArea.setPrefHeight(250);
        textArea.setEditable(false);

        dialog = new TextInputDialog();
        dialog.setContentText(null);
        dialog.setHeaderText(null);

        search = new Button("Search");
        search.setOnAction(eh);
        search.setPrefSize(100f, 50f);

        Text scenetitle = new Text("Please press the Search button to input your query");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        grid.add(imageView, 0, 1);
        grid.add(search, 1, 1);
        border.setTop(grid);
        border.setBottom(textArea);

        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Adds the event handler for the search button.
     */
    private class ButtonDoer implements EventHandler<ActionEvent> {
        /**
         * Sets the functions for the search button.
         */
        private void doSearch() throws Exception {
            dialog.setTitle("Search what?");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                LuceneSearch lucene  = new LuceneSearch();
                textArea.appendText(lucene.indexSearch(result.get()));
//                SearchEngine searchEngine = new SearchEngine();
//                String web = searchEngine.searchResult(result.get());
//                textArea.appendText(web);

            } else {
                textArea.appendText("Invalid query, please check your query");
            }
        }

        public void handle(ActionEvent e) {
            if (textArea.getText().trim().isEmpty()) {
                try {
                    doSearch();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                textArea.clear();
                try {
                    doSearch();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }
    }
}
