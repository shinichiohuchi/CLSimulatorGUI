package application.clcode.tree;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TreeViewStage extends Stage {
  private TreeViewController controller;

  public TreeViewStage(String clcode) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("TreeView.fxml"));
    try {
      BorderPane root = (BorderPane) loader.load();
      Scene scene = new Scene(root, 800, 600);
      scene.getStylesheets()
          .add(getClass().getResource("/application/application.css").toExternalForm());
      setScene(scene);
      initStyle(StageStyle.UTILITY);
      initModality(Modality.APPLICATION_MODAL);
      setTitle(clcode + " - Tree Viewer");
      controller = (TreeViewController) loader.getController();
      controller.setCLCode(clcode);
      // setOnCloseRequest(e -> controller.cancel());

      // setTitle(MainController.dictionary.getString("editStage-title"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
