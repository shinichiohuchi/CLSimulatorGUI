package application.config.stage;

import java.util.Map;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 設定変更画面のrootクラス。
 * @author shinichi666
 */
public class ConfigStage extends Stage {
  private ConfigStageController controller;

  /**
   * リストに格納するテキストとConfigControllerを実装したクラスを渡すコンストラクタ。
   * @param aControllerMap
   */
  public ConfigStage(Map<String, ConfigController> aControllerMap) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigStage.fxml"));
      BorderPane root = (BorderPane) loader.load();
      controller = loader.getController();
      controller.setControllersMap(aControllerMap);
      controller.select();

      Scene scene = new Scene(root, 600, 400);
      setScene(scene);
      initStyle(StageStyle.UTILITY);
      initModality(Modality.APPLICATION_MODAL);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 結果を取得する。
   * OKボタンを押された場合、マップが返り、
   * OKボタンが押されなかった場合、nullが返る。
   * @return map or null
   */
  public Optional<Map<String, String>> showAndWaitAndGetConfigs() {
    showAndWait();
    return Optional.ofNullable(controller.configApply ? controller.getConfigMap() : null);
  }

  /**
   * リストビューの幅を変更する。
   * @param width
   */
  public void setListViewWidth(double width) {
    controller.setListViewWidth(width);
  }
}
