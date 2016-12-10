package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 一番最初に実行されるアプリケーションクラス。
 * @author Shinichi Ouchi
 * @version 2.0
 */
public class Main extends Application {
  private MainController controller;
  static final String TITLE = "CLSimulator";

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
      BorderPane root = (BorderPane) loader.load();
      Scene scene = new Scene(root, 662, 720);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

      primaryStage.setScene(scene);
      primaryStage.setOnCloseRequest(e -> controller.closeAction());
      primaryStage.setTitle(TITLE);
      primaryStage.getIcons().add(new Image(getClass().getResource("resources/images/CLSIcon.png").toExternalForm()));

      MainController.STAGE_CONFIG.changeStageAxisAndPosition(primaryStage);
      controller = (MainController) loader.getController();

      primaryStage.show();
      controller.updateTitle();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
