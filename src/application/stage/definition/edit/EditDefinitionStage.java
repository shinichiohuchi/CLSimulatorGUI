package application.stage.definition.edit;

import java.io.IOException;

import application.MainController;
import application.config.Config;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.PropertiesKeys;

public class EditDefinitionStage extends Stage {
  private static final Config CONFIG = new Config("edit-stage.xml");

  private EditDefinitionStageController controller;

  public EditDefinitionStage() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EditDefinitionStage.fxml"));
    try {
      BorderPane root = (BorderPane) loader.load();
      Scene scene = new Scene(root, 800, 600);
      scene.getStylesheets()
          .add(getClass().getResource("/application/application.css").toExternalForm());
      setScene(scene);
      setTitle("Edit Definitions");
      initStyle(StageStyle.UTILITY);
      initModality(Modality.APPLICATION_MODAL);
      controller = (EditDefinitionStageController) loader.getController();
      controller.setStage(this);
      setOnCloseRequest(e -> controller.cancel());

      setTitle(MainController.dictionary.getString("editStage-title"));
      CONFIG.changeStageAxisAndPosition(this);
      CONFIG.getProperty("divider").map(Double::parseDouble)
          .ifPresent(controller::setDividerPosition);
      MainController.CONFIG.getProperty(PropertiesKeys.EDITOR.TEXT)
          .map(Integer::parseInt)
          .ifPresent(controller::changeFontSize);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void closeAction() {
    CONFIG.setProperties(this);
    CONFIG.setProperty("divider", "" + controller.getDividerPosition());
    CONFIG.write();
  }
}
