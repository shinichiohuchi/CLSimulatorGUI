package application.config;

import static util.PropertiesKeys.EDITOR;
import static util.PropertiesKeys.LANGUAGE;
import static util.PropertiesKeys.PREVIEW;
import static util.PropertiesKeys.RESULT;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import application.MainController;
import application.config.stage.ConfigController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.Languages;

/**
 * アプリケーションの基本設定を管理するクラス。
 * @author shinichi666
 */
public class BasicConfigController implements ConfigController {
  private static final String TITLE = MainController.dictionary
      .getString("configStage-basic-headerTitle");
  private static final String DESCRIPTION = MainController.dictionary
      .getString("configStage-basic-headerDescription");

  // ************************************************************
  // コンポーネント
  // ************************************************************
  @FXML private VBox rootVBox;

  @FXML private Label languageLabel;
  @FXML private ComboBox<String> languageComboBox;

  @FXML private Label tableViewLabel;
  @FXML private Label previewLabel;
  @FXML private Label editLabel;

  /**
   * フォントサイズのリスト。
   */
  private static final ObservableList<String> fontSizeList = FXCollections.observableArrayList(
      "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16");
  @FXML private ComboBox<String> tableViewComboBox;
  @FXML private ComboBox<String> previewComboBox;
  @FXML private ComboBox<String> editComboBox;

  @FXML
  private void initialize() {
    // コンボボックスへのアイテム登録
    languageComboBox.setItems(Languages.getLanguageList());
    tableViewComboBox.setItems(fontSizeList);
    previewComboBox.setItems(fontSizeList);
    editComboBox.setItems(fontSizeList);

    // プロパティファイルが存在しなかった時のためのデフォルト値をセット
    languageComboBox.setValue(Languages.convertLocaleToText(Locale.getDefault()));

    Config config = MainController.CONFIG;
    config.getProperty(LANGUAGE.TEXT).ifPresent(languageComboBox::setValue);
    config.getProperty(RESULT.TEXT).ifPresent(tableViewComboBox::setValue);
    config.getProperty(PREVIEW.TEXT).ifPresent(previewComboBox::setValue);
    config.getProperty(EDITOR.TEXT).ifPresent(editComboBox::setValue);

    setLanguages();
  }

  // ************************************************************
  // Getter
  // ************************************************************
  @Override
  public Map<String, String> getConfigMap() {
    Map<String, String> propertiesMap = new HashMap<>(6);
    propertiesMap.put(LANGUAGE.TEXT, languageComboBox.getValue());
    propertiesMap.put(RESULT.TEXT, tableViewComboBox.getValue());
    propertiesMap.put(PREVIEW.TEXT, previewComboBox.getValue());
    propertiesMap.put(EDITOR.TEXT, editComboBox.getValue());
    return propertiesMap;
  }

  @Override
  public Node getRoot() {
    return rootVBox;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;

  }

  @Override
  public String getTitle() {
    return TITLE;
  }

  // ************************************************************
  // Setter
  // ************************************************************
  /**
   * 各種コンポーネントにテキストをセットする。
   */
  private void setLanguages() {
    ResourceBundle dictionary = MainController.dictionary;
    languageLabel.setText(dictionary.getString("configStage-basic-language"));
    tableViewLabel.setText(dictionary.getString("configStage-basic-resultLabel"));
    previewLabel.setText(dictionary.getString("configStage-basic-previewLabel"));
    editLabel.setText(dictionary.getString("configStage-basic-editorLabel"));
  }
}
