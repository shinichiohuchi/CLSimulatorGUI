package application.config.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * コンフィグ画面のコントローラクラス。
 * @author shinichi666
 *
 */
public class ConfigStageController {
  private Map<String, ConfigController> controllersMap;
  boolean configApply = false;

  // ************************************************************
  // コンポーネント
  // ************************************************************
  @FXML private ListView<String> listView;
  @FXML private Label titleLabel;
  @FXML private Label descriptionLabel;
  @FXML private Button okButton;
  @FXML private Button cancelButton;
  @FXML private BorderPane locationBorderPane;

  @FXML
  private void initialize() {
    listView.selectionModelProperty().addListener((obs, oldVal, newVal) -> {
      listViewOnMouseClicked();
    });
    okButton.setText(MainController.dictionary.getString("common-ok"));
    cancelButton.setText(MainController.dictionary.getString("common-cancel"));
  }

  @FXML
  private void okButtonOnAction() {
    configApply = true;
    okButton.getScene().getWindow().hide();
  }

  @FXML
  private void cancelButtonOnAction() {
    okButton.getScene().getWindow().hide();
  }

  @FXML
  private void listViewOnMouseClicked() {
    if (!listView.getSelectionModel().isEmpty()) {
      String item = listView.getSelectionModel().getSelectedItem();
      ConfigController controller = controllersMap.get(item);

      titleLabel.setText(controller.getTitle());
      descriptionLabel.setText(controller.getDescription());
      locationBorderPane.setCenter(controller.getRoot());
    }
  }

  void setControllersMap(Map<String, ConfigController> aMap) {
    controllersMap = aMap;

    List<String> keyList = new ArrayList<>(controllersMap.size());
    controllersMap.entrySet().stream()
        .forEach(set -> keyList.add(set.getKey()));
    listView.setItems(FXCollections.observableArrayList(keyList));
    listView.getSelectionModel().select(0);
    listViewOnMouseClicked();
  }

  Map<String, String> getConfigMap() {
    Map<String, String> map = new HashMap<>();
    controllersMap.entrySet().stream()
        .forEach(set -> {
          Map<String, String> tmpMap = set.getValue().getConfigMap();
          tmpMap.entrySet().stream()
              .forEach(s -> {
                map.put(s.getKey(), s.getValue());
              });
        });
    return map;
  }

  /**
   * 画面切り替えリストの横幅を変更する。
   * @param width 横幅
   */
  public void setListViewWidth(double width) {
    listView.setPrefWidth(width);
  }

  void select() {
    listView.getSelectionModel().select(0);
  }
}
