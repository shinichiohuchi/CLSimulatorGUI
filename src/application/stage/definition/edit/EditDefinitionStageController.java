package application.stage.definition.edit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import lib.string.combinator.CombinatorLogic;
import util.PropertiesKeys;
import util.UtilCombinatorLogic;

/**
 * 定義ファイルを編集するクラス。
 * @author shinichi666
 *
 */
public class EditDefinitionStageController {
  private EditDefinitionStage stage;
  private String lineSeparator;
  /**
   * コンビネータを定義したファイルを開いたときに 記述されていた初期のテキストのリスト
   */
  private List<String> initialTextList;

  // **************************************************
  // メニューバー
  // **************************************************
  @FXML private Menu fileMenu;
  @FXML private MenuItem okMenuItem;
  @FXML private MenuItem cancelMenuItem;

  @FXML private Menu definitionMenu;
  @FXML private MenuItem restoreMenuItem;
  @FXML private MenuItem initializeMenuItem;

  @FXML private Menu previewMenu;
  @FXML private MenuItem updateMenuItem;

  @FXML private SplitPane splitPane;

  // **************************************************
  // 定義ファイル編集
  // **************************************************
  @FXML private Label definitionlabel;
  @FXML private TextArea definitionTextArea;
  @FXML private Button restoreButton;
  @FXML private Button initializeButton;

  // **************************************************
  // プレビュー
  // **************************************************
  @FXML private Label previewLabel;
  @FXML private TextArea previewTextArea;
  @FXML private Button updateButton;

  @FXML private Button helpButton;

  // **************************************************
  // 終了処理
  // **************************************************
  @FXML private Button okButton;
  @FXML private Button cancelButton;

  @FXML
  private void initialize() {
    setLanguage();

    okMenuItem.setOnAction(e -> ok());
    cancelMenuItem.setOnAction(e -> cancel());
    restoreMenuItem.setOnAction(e -> updateDefinitionTextArea(initialTextList));
    initializeMenuItem.setOnAction(e -> initializeDefinition());
    updateMenuItem.setOnAction(e -> updatePreviewTextArea());

    definitionTextArea.textProperty()
        .addListener((observableValue, oldValue, newValue) -> okButton.setDisable(true));
    restoreButton.setOnAction(e -> updateDefinitionTextArea(initialTextList));
    initializeButton.setOnAction(e -> initializeDefinition());
    updateButton.setOnAction(e -> updatePreviewTextArea());
    okButton.setOnAction(e -> ok());
    cancelButton.setOnAction(e -> cancel());
    // **************************************************
    // ここまでアクションイベント登録
    // **************************************************

    lineSeparator = System.getProperty("line.separator");

    File definitionFile = MainController.getDefinitionFileManager().getFile().get();
    initialTextList = makeInitialDefinitionList(definitionFile);

    updateDefinitionTextArea(initialTextList);

    MainController.CONFIG.getProperty(PropertiesKeys.EDITOR.TEXT).map(Double::parseDouble)
        .ifPresent(this::setDividerPosition);
  }

  void changeFontSize(int fontSize) {
    String style = "-fx-font-size: " + fontSize + "pt;";
    definitionTextArea.setStyle(style);
    previewTextArea.setStyle(style);
  }

  /**
   * コントローラロード時に生成されたファイルから読み取った
   * 初期定義リストをCombinatorLogicにセットしなおすことで、
   * コンビネータの定義をステージ制政治の者に復元する。
   */
  void cancel() {
    hide();
  }

  /**
   * definitionTextAreaをクリアし、 SKIコンビネータのみを追加する。
   */
  private void initializeDefinition() {
    definitionTextArea.setText("");
    updatePreviewTextArea();
  }

  /**
   * リストをdefinitionTextAreaにセットする。
   * @param list
   */
  private void updateDefinitionTextArea(List<String> list) {
    definitionTextArea.setText("");
    list.stream().forEach(line -> definitionTextArea.appendText(line + lineSeparator));
    definitionTextArea.positionCaret(0);
    updatePreviewTextArea();
  }

  /**
   * definitionListのテキストから定義リストを生成し、
   * 定義リストをセットしたCombinatorLogicで1ステップ計算する。
   * 計算結果をプレビューとしてpreviewTextAreaにセットする。
   */
  private void updatePreviewTextArea() {
    previewTextArea.setText("");
    List<Boolean> errorList = new ArrayList<>();

    List<String[]> definitionList = new ArrayList<>();
    definitionList.addAll(CombinatorLogic.getInitialMacroCombinatorsList());
    definitionList.addAll(makeDefinitionList());
    definitionList.stream().forEach(line -> {
      Object[] checkedCode = UtilCombinatorLogic.getCheckedDefinition(line);
      previewTextArea.appendText((String) checkedCode[0]);
      errorList.add((Boolean) checkedCode[1]);
    });

    // false(Error)が一つでもあった場合はOKボタンをクリック不可にする。
    boolean value = errorList.stream().anyMatch(e -> e == false);
    okButton.setDisable(value);

    int position = definitionTextArea.getCaretPosition();
    previewTextArea.positionCaret(position);
  }

  /**
   * definitionTextAreaのテキストから定義リストを生成し、
   * CombinatorLogicのコンビネータの定義として確定する。
   */
  private void ok() {
    if (!okButton.isDisable()) {
      try (PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(MainController.getDefinitionFileManager().getFile().get()),
          "UTF-8")))) {
        makeUnfilteredDefinitionList().stream().forEach(line -> pw.println(line));
      } catch (IOException e) {
        e.printStackTrace();
      }
      hide();
    }
  }

  /**
   * ウインドウを閉じる。
   */
  private void hide() {
    stage.closeAction();
    okButton.getScene().getWindow().hide();
  }

  /**
   * ファイルからコンビネータの定義を読み取り、 文字列を保持するリストとして生成する。
   * @param file
   * @return
   */
  private List<String> makeInitialDefinitionList(File file) {
    Path path = file.toPath();
    try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
      return br.lines().collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * コメント行の無視や空白の削除などを行わず、
   * definitionTextAreaのテキストをそのままリストとして取得する。
   * @return list
   */
  private List<String> makeUnfilteredDefinitionList() {
    String definition = definitionTextArea.getText();
    BufferedReader br = new BufferedReader(new StringReader(definition));
    return br.lines().collect(Collectors.toList());
  }

  /**
   * definitionTextAreaのテキストから コンビネータの定義を配列を持つリストとして生成する。
   * @return
   */
  private List<String[]> makeDefinitionList() {
    String definition = definitionTextArea.getText();
    BufferedReader br = new BufferedReader(new StringReader(definition));
    List<String[]> list = CombinatorLogic.splitList(br.lines().collect(Collectors.toList()));
    return list;
  }

  // ************************************************************
  // Getter
  // ************************************************************
  String getDividerPosition() {
    double[] position = splitPane.getDividerPositions();
    return String.valueOf(position[0]);
  }

  // ************************************************************
  // Setter
  // ************************************************************
  void setStage(EditDefinitionStage aStage) {
    stage = aStage;
  }

  void setDividerPosition(Double position) {
    splitPane.setDividerPosition(0, position);
  }

  private void setLanguage() {
    // メニュー
    fileMenu.setText(MainController.dictionary.getString("editStage-menu-file"));
    okMenuItem.setText(MainController.dictionary.getString("common-ok"));
    cancelMenuItem.setText(MainController.dictionary.getString("common-cancel"));

    definitionMenu.setText(MainController.dictionary.getString("editStage-menu-definition"));
    restoreMenuItem.setText(MainController.dictionary.getString("editStage-restoreButton"));
    initializeMenuItem.setText(MainController.dictionary.getString("editStage-initializeButton"));

    previewMenu.setText(MainController.dictionary.getString("editStage-menu-preview"));
    updateMenuItem.setText(MainController.dictionary.getString("editStage-updateButton"));

    // コンポーネント
    definitionlabel.setText(MainController.dictionary.getString("editStage-header-definition"));
    restoreButton.setText(MainController.dictionary.getString("editStage-restoreButton"));
    initializeButton.setText(MainController.dictionary.getString("editStage-initializeButton"));

    previewLabel.setText(MainController.dictionary.getString("editStage-header-preview"));
    updateButton.setText(MainController.dictionary.getString("editStage-updateButton"));

    okButton.setText(MainController.dictionary.getString("common-ok"));
    cancelButton.setText(MainController.dictionary.getString("common-cancel"));
  }
}
