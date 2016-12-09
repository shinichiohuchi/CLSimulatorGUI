package application.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import application.MainController;
import application.config.stage.ConfigController;
import application.config.stage.ConfigStage;
import javafx.fxml.FXMLLoader;

public class ConfigOperator {
  private ConfigStage stage;

  public ConfigOperator() {
    FXMLLoader basicLoader = new FXMLLoader(getClass().getResource("BasicConfig.fxml"));
    try {
      basicLoader.load();
      BasicConfigController basicConfigController = (BasicConfigController) basicLoader
          .getController();

      Map<String, ConfigController> map = new HashMap<>();
      ResourceBundle dictionary = MainController.dictionary;
      String headerTitle = dictionary.getString("configStage-basic-headerTitle");
      map.put(headerTitle, basicConfigController);

      stage = new ConfigStage(map);
      stage.setTitle(MainController.dictionary.getString("configStage-stageTitle"));
      stage.getScene().getStylesheets()
          .add(getClass().getResource("/application/application.css").toExternalForm());
      stage.setWidth(800.0);
      stage.setResizable(false);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 設定変更画面を開き、設定したオプションを返す。
   * @return
   */
  public Optional<Map<String, String>> showAndWaitAndGetConfigs() {
    return stage.showAndWaitAndGetConfigs();
  }
}
