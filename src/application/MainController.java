package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import application.clcode.tab.CLCodeTableTab;
import application.config.Config;
import application.config.ConfigOperator;
import application.stage.definition.edit.EditDefinitionStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lib.string.combinator.CombinatorLogic;
import util.Languages;
import util.PropertiesKeys;
import util.ResourceBundleControlUTF8;

/**
 * 一番最初に実行されるアプリケーションクラスのコントローラー。
 * @author Shinichi Ouchi
 */
public class MainController {
  /**
   * コンビネータのフォーマットを保持するリスト
   */
  private List<String[]> combinatorsList;

  /**
   * 言語環境によって変化するテキストを格納したプロパティ
   */
  public static ResourceBundle dictionary;

  /**
   * 開いたCLCodeが記述されたテキストファイルの情報を保持するクラス
   */
  private static FileChooserManager clcodeFileManager;

  /**
   * コンビネータが定義されたファイルの情報を保持するクラス。
   */
  private static FileChooserManager definitionFileManager;

  /**
   * 定義済みコンビネータのプレビュークラス
   */
  private DefinitionViewer definitionViewer;

  /**
   * ランダムコードを生成するクラス。
   */
  private RandomCode randomCode;
  /**
   * アプリケーション全般のプロパティを保持するクラス。
   */
  public static final Config CONFIG = new Config("config.xml");

  /**
   * メインウィンドウの座標とサイズのプロパティを保持するクラス。
   * Main.javaからのみ呼び出されることを想定している。
   */
  static final Config STAGE_CONFIG = new Config("main-stage.xml");

  // **************************************************
  // メニューバー
  // **************************************************
  @FXML private Menu fileMenu;
  @FXML private MenuItem openMenuItem;
  @FXML private MenuItem saveMenuItem;
  @FXML private MenuItem newDefinitionFileMenuItem;
  @FXML private MenuItem openDefinitionFileMenuItem;
  @FXML private MenuItem editDefinitionFileMenuItem;
  @FXML private MenuItem configMenuItem;
  @FXML private MenuItem closeMenuItem;

  // **************************************************
  // ランダムコード生成メニュー
  // **************************************************
  @FXML private Menu editMenu;
  @FXML private MenuItem createRandomCodeMenuItem;
  @FXML private Menu cltermCountMenu;
  @FXML private ToggleGroup cltermCountGroup;
  @FXML private Menu calculationMaxMenu;
  @FXML private ToggleGroup calculationMaxGroup;

  @FXML private Menu tabMenu;
  @FXML private MenuItem deleteTabMenuItem;
  @FXML private MenuItem clearTabsMenuItem;
  @FXML private MenuItem moveLeftTabMenuItem;
  @FXML private MenuItem moveRightTabMenuItem;

  @FXML private Menu viewMenu;
  @FXML private CheckMenuItem showCombinatorsCheckMenuItem;

  // **************************************************
  // コンビネータ入力
  // **************************************************
  @FXML private TextField clcodeTextField;
  @FXML private Button addButton;

  // **************************************************
  // メインコンポーネント
  // **************************************************
  @FXML private SplitPane splitPane;
  @FXML private AnchorPane anchorPane;

  // **************************************************
  // 計算結果表示タブペイン
  // **************************************************
  @FXML private TitledPane resultTitledPane;
  @FXML private TabPane tabPane;
  private LinkedList<CLCodeTableTab> clCodeTabList = new LinkedList<>();

  // **************************************************
  // コンビネータプレビュー
  // **************************************************
  @FXML private TitledPane previewTitledPane;
  @FXML private TextArea previewTextArea;

  static {
    changeLanguage();
  }

  @FXML
  private void initialize() {
    setLanguages();
    definitionViewer = new DefinitionViewer(previewTextArea);
    clcodeFileManager = new FileChooserManager("Text Files", "*.txt");
    randomCode = new RandomCode(cltermCountGroup);

    // **************************************************
    // 設定ファイルから読み取った値をセット
    // **************************************************
    changeDividerPosition();
    changePreviewFontSize();

    boolean autoOpen = CONFIG.getProperty(PropertiesKeys.AUTO_OPEN.TEXT)
        .map(Boolean::valueOf).orElse(true);
    showCombinatorsCheckMenuItem.setSelected(autoOpen);
    if (!autoOpen) {
      splitPane.getItems().remove(anchorPane);
    }

    String defPath = CONFIG.getProperty(PropertiesKeys.DEFINITION_PATH.TEXT).orElse("");
    definitionFileManager = new FileChooserManager(defPath, "Text Files", "*.csv");
    // **************************************************

    File defFile = new File(defPath);
    if (defFile.exists()) {
      combinatorsList = CombinatorLogic.makeMacroCombinatorsList(defFile);
      editDefinitionFileMenuItem.setDisable(false);
    }

    addInitialTab();
    updateDefinitionPreview();
    changeResultFontSize();
    CONFIG.getProperty(PropertiesKeys.CLTERM_COUNT.TEXT)
        .ifPresent(text -> {
          changeSelectionOfToggle(cltermCountGroup, text);
        });
    CONFIG.getProperty(PropertiesKeys.CALCULATION_MAX.TEXT)
        .ifPresent(text -> {
          changeSelectionOfToggle(calculationMaxGroup, text);
        });
  }

  /**
   * 定義ビューのディバイダ―位置を変更する。
   */
  private void changeDividerPosition() {
    CONFIG.getProperty(PropertiesKeys.DIVIDER.TEXT)
        .map(Double::parseDouble)
        .ifPresent(d -> splitPane.setDividerPosition(0, d));
  }

  public void distableCheckMenuItem() {
    showCombinatorsCheckMenuItem.setSelected(false);
  }

  /**
   * 計算結果のフォントサイズを変更する。
   */
  private void changeResultFontSize() {
    int resultFontSize = CONFIG.getProperty(PropertiesKeys.RESULT.TEXT)
        .map(Integer::parseInt).orElse(10);
    changeResultFontSize(resultFontSize);
  }

  /**
   * 計算結果のフォントサイズを変更する。
   * @param fontSize フォントサイズ
   */
  private void changeResultFontSize(int fontSize) {
    clCodeTabList.stream().forEach(t -> t.changeFontSize(fontSize));
  }

  private void changePreviewFontSize() {
    int fontSize = CONFIG.getProperty(PropertiesKeys.PREVIEW.TEXT)
        .map(Integer::parseInt).orElse(12);
    changePreviewFontSize(fontSize);
  }

  private void changePreviewFontSize(int fontSize) {
    definitionViewer.setFontSize(fontSize);
  }

  void closeAction() {
    // アプリケーション全般の設定を出力
    clcodeFileManager.getFile().ifPresent(f -> {
      CONFIG.setProperty(PropertiesKeys.FILE_PATH.TEXT, f.getPath());
    });
    definitionFileManager.getFile().ifPresent(f -> {
      CONFIG.setProperty(PropertiesKeys.DEFINITION_PATH.TEXT, f.getPath());
    });
    String autoOpen = String.valueOf(showCombinatorsCheckMenuItem.isSelected());
    CONFIG.setProperty(PropertiesKeys.AUTO_OPEN.TEXT, autoOpen);
    CONFIG.setProperty(PropertiesKeys.CLTERM_COUNT.TEXT, "" + randomCode.getCLTermCount());
    CONFIG.setProperty(PropertiesKeys.CALCULATION_MAX.TEXT, "" + (getMaxCalculationCount() + 1));
    double[] pos = splitPane.getDividerPositions();
    if (pos.length != 0) {
      CONFIG.setProperty(PropertiesKeys.DIVIDER.TEXT, "" + pos[0]);
    }
    CONFIG.write();

    // メインウィンドウの設定を出力
    Stage stage = (Stage) addButton.getScene().getWindow();
    STAGE_CONFIG.setProperties(stage);
    STAGE_CONFIG.write();

    Platform.exit();
  }

  // **************************************************
  // Resultテーブルに計算結果を追加する操作。
  // **************************************************
  /**
   * テキストフィールドから文字列を取得し、テーブルに追加する。
   */
  private void addClcode() {
    int max = getMaxCalculationCount();
    addCLCode(clcodeTextField.getText(), max);
  }

  /**
   * パスのファイルからテキストを取り出し、計算結果をテーブルに追加する。
   * @param path
   *          FilePath
   */
  private void addCLCode(Path path) {
    try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
      br.lines().filter(Objects::nonNull).filter(l -> !l.startsWith("#") && l.length() != 0)
          .forEach(l -> addCLCode(l));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 文字列を計算し、テーブルに追加する。 この処理は計算が引数不足で終了するまで継続する。
   * 無限ループが発生する可能性がある。
   * @param clcode
   *          CLCode
   */
  private void addCLCode(String clcode) {
    int max = getMaxCalculationCount();
    addCLCode(clcode, max);
  }

  /**
   * 文字列を計算し、テーブルに追加する。 計算回数の上限を指定することができ、
   * 0を指定した場合は、コンビネータが引数不足になるまで 計算を継続する。
   * 0を指定した場合は無限ループが発生する可能性がある。
   * @param clcode
   *          CLCode
   * @param max
   *          計算回数の上限
   */
  private void addCLCode(String clcode, int max) {
    Optional<String> clcodeOpt = Optional.ofNullable(clcode);

    clcodeOpt.map(s -> s.replaceAll("[ ||　||\t]", "")).filter(s -> !"".equals(s)).ifPresent(s -> {
      int size = tabPane.getTabs().size();
      clCodeTabList.get(size - 1).setCLCode(s, max);
      CLCodeTableTab tab = new CLCodeTableTab("Code " + (size + 1), this);

      clCodeTabList.add(tab);
      tabPane.getTabs().add(tab);

      clcodeTextField.setText("");
      tabPane.getSelectionModel().select(size - 1);
    });
    changeResultFontSize();
  }

  /**
   * 定義ウィンドウの定義を更新する。
   */
  private void updateDefinitionPreview() {
    definitionFileManager.getFile().ifPresent(definitionViewer::update);
  }

  private void textFieldRequestFocus() {
    clcodeTextField.getScene().getWindow().requestFocus();
    clcodeTextField.requestFocus();
  }

  /**
   * 必ず配置する初期のタブ
   */
  private void addInitialTab() {
    CLCodeTableTab tab = new CLCodeTableTab("Code 1", this);
    clCodeTabList.add(tab);
    tabPane.getTabs().add(tab);
  }

  // **************************************************
  // メニュー操作。
  // **************************************************
  /**
   * FileChooserからテキストファイルを一つ選択し、 計算結果をテーブルに追加する。
   */
  @FXML
  private void openMenuItemOnClicked() {
    Optional<File> fileOpt = clcodeFileManager.openFile();
    fileOpt.ifPresent(f -> {
      Path path = f.toPath();
      addCLCode(path);
    });
    clcodeTextField.requestFocus();
  }

  /**
   * ファイル保存ダイアログを表示して、選択中のテーブルタブをテキストファイルに出力する。
   */
  @FXML
  private void saveAsMenuItemOnClicked() {
    Optional<File> saveFileOpt = clcodeFileManager.saveFile();
    saveFileOpt.ifPresent(f -> {
      int index = tabPane.getSelectionModel().getSelectedIndex();
      clCodeTabList.get(index).outputCLCodeToFile(f);
    });
    clcodeTextField.requestFocus();
  }

  @FXML
  private void newDefinitionFileMenuItemOnClicked() {
    Optional<File> saveFileOpt = definitionFileManager.saveFile();
    saveFileOpt.ifPresent(f -> {
      updateTitle(f);
      try (PrintWriter pw = new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8")))) {
        pw.println();
      } catch (IOException e) {
        e.printStackTrace();
      }
      editDefinitionFileMenuItemOnClicked();
    });
    clcodeTextField.requestFocus();
  }

  @FXML
  private void openDefinitionFileMenuItemOnClicked() {
    Optional<File> fileOpt = definitionFileManager.openFile();
    fileOpt.ifPresent(f -> {
      updateTitle(f);
      updateDefinitionPreview();
      combinatorsList = CombinatorLogic.makeMacroCombinatorsList(f);
      editDefinitionFileMenuItem.setDisable(false);
    });
    clcodeTextField.requestFocus();
  }

  @FXML
  private void editDefinitionFileMenuItemOnClicked() {
    EditDefinitionStage definitionStage = new EditDefinitionStage();
    definitionStage.showAndWait();
    definitionStage = null;
    updateDefinitionPreview();

    definitionFileManager.getFile().ifPresent(f -> {
      combinatorsList = makeDefinitionList(f);
    });
    textFieldRequestFocus();
  }

  private List<String[]> makeDefinitionList(File file) {
    Path path = file.toPath();
    try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
      List<String> list = br.lines().collect(Collectors.toList());
      return CombinatorLogic.splitList(list);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @FXML
  private void closeMenuItemOnClicked() {
    closeAction();
  }

  /**
   * 定義済みコンビネータのみでランダムに生成した CLCodeをタブに追加する。
   * この時、メニューから文字列の上限と計算回数の上限を設定する。
   */
  @FXML
  private void createRandomCodeMenuItemOnClicked() {
    String randCode = randomCode.makeRandomCode(combinatorsList);
    int max = getMaxCalculationCount();
    addCLCode(randCode, max);
  }

  /**
   * 選択中のラジオメニューから上限値を取得する。
   * @return max
   */
  private int getMaxCalculationCount() {
    RadioMenuItem maxRadio = (RadioMenuItem) calculationMaxGroup.getSelectedToggle();
    int max = Integer.valueOf(maxRadio.getText());
    return max - 1;
  }

  // **************************************************
  // タブ操作。
  // **************************************************
  /**
   * 選択中のタブを削除する。
   */
  @FXML
  private void deleteTabMenuItemOnClicked() {
    int index = tabPane.getSelectionModel().getSelectedIndex();
    int size = clCodeTabList.size();
    if (1 < size && index != size - 1) {
      tabPane.getTabs().remove(index);
      clCodeTabList.remove(index);

      IntStream.range(0, size - 1)
          .forEach(i -> tabPane.getTabs().get(i).setText("Code " + (i + 1)));
    }
  }

  /**
   * すべてのタブを消去する。
   */
  @FXML
  private void clearTabsMenuItemOnClicked() {
    tabPane.getTabs().clear();
    clCodeTabList.clear();
    addInitialTab();
    changeResultFontSize();
  }

  /**
   * 選択中のタブを左に移動する。
   */
  @FXML
  private void moveLeftTabMenuItemOnClicked() {
    int index = tabPane.getSelectionModel().getSelectedIndex();
    int max = clCodeTabList.size() - 1;
    // 選択中のタブが左端だった場合、右端に移動する
    index = index == 0 ? max : index - 1;
    tabPane.getSelectionModel().select(index);
  }

  /**
   * 選択中のタブを右に移動する。
   */
  @FXML
  private void moveRightTabMenuItemOnClicked() {
    int index = tabPane.getSelectionModel().getSelectedIndex();
    int max = clCodeTabList.size() - 1;
    // 選択中のタブが右端だった場合、左端に移動する
    index = index == max ? 0 : index + 1;
    tabPane.getSelectionModel().select(index);
  }

  /**
   * 定義ウィンドウを表示する。
   */
  @FXML
  private void showDefinitionStageMenuItemOnClicked() {
    if (showCombinatorsCheckMenuItem.isSelected()) {
      splitPane.getItems().add(anchorPane);
      changeDividerPosition();
      return;
    }
    CONFIG.setProperty(PropertiesKeys.DIVIDER.TEXT, "" + splitPane.getDividerPositions()[0]);
    splitPane.getItems().remove(anchorPane);
  }

  @FXML
  private void clcodeTextFieldOnKeyPressed(KeyEvent key) {
    if (key.getCode() == KeyCode.ENTER) {
      addClcode();
    }
  }

  @FXML
  private void addButtonOnClicked() {
    addClcode();
  }

  // **************************************************
  // ドラッグアンドドロップ。
  // **************************************************
  /**
   * ドラッグオーバー。
   * @param e
   *          DragEvent
   */
  @FXML
  private void tabPaneDragOver(DragEvent e) {
    Dragboard board = e.getDragboard();
    if (board.hasFiles()) {
      e.acceptTransferModes(TransferMode.COPY);
    }
  }

  /**
   * ドロップしたファイルを計算する。
   * @param e
   *          DragEvent
   */
  @FXML
  private void tabPaneDragDrop(DragEvent e) {
    Dragboard board = e.getDragboard();
    if (board.hasFiles()) {
      Path path = board.getFiles().get(0).toPath();
      addCLCode(path);
    }
  }

  /**
   * 開いている定義ファイルのコンビネータの定義リストを返す。
   * @return 定義配列リスト
   */
  public List<String[]> getCombinatorsList() {
    return combinatorsList;
  }

  /**
   * 定義ファイルを監視するFileChooserManagerクラスのインスタンスを返す。
   * @return
   */
  public static FileChooserManager getDefinitionFileManager() {
    return definitionFileManager;
  }

  /**
   * ウィンドウのタイトルにファイル名を表示。
   * definitionManagerからファイル情報を取得して登録。
   */
  void updateTitle() {
    definitionFileManager.getFile().ifPresent(this::updateTitle);
  }

  /**
   * ウィンドウのタイトルにファイル名を表示。
   * @param file 定義ファイル
   */
  private void updateTitle(File file) {
    if (file.exists()) {
      Stage stage = (Stage) addButton.getScene().getWindow();
      String name = file.getName();
      String path = file.getPath();
      stage.setTitle(String.format("%s (%s) - %s", name, path, Main.TITLE));
    }
  }

  @FXML
  private void configMenuItemOnAction() {
    ConfigOperator operator = new ConfigOperator();
    Optional<Map<String, String>> configsMap = operator.showAndWaitAndGetConfigs();
    configsMap.ifPresent(map -> CONFIG.setProperties(map));

    changeLanguage();
    setLanguages();
    changeResultFontSize();
    changePreviewFontSize();
  }

  /**
   * プロパティファイルに記録されている言語設定から言語環境を変更
   * プロパティファイルが存在しなかった場合は英語が設定される。
   */
  private static void changeLanguage() {
    String text = CONFIG.getProperty(PropertiesKeys.LANGUAGE.TEXT)
        .orElse(Languages.convertLocaleToText(Locale.getDefault()));
    Locale.setDefault(Languages.convertTextToLocale(text));
    dictionary = ResourceBundle
        .getBundle("application.resources.languages.dictionary", Locale.getDefault(),
            new ResourceBundleControlUTF8());
  }

  /**
   * 各種コンポーネントにテキストをセットする。
   */
  private void setLanguages() {
    fileMenu.setText(dictionary.getString("menu-file-title"));
    openMenuItem.setText(dictionary.getString("menu-file-open"));
    saveMenuItem.setText(dictionary.getString("menu-file-saveAs"));
    newDefinitionFileMenuItem.setText(dictionary.getString("menu-file-newDefinition"));
    openDefinitionFileMenuItem.setText(dictionary.getString("menu-file-openDefinition"));
    editDefinitionFileMenuItem.setText(dictionary.getString("menu-file-editDefinition"));
    configMenuItem.setText(dictionary.getString("menu-file-config"));
    closeMenuItem.setText(dictionary.getString("menu-file-quit"));

    editMenu.setText(dictionary.getString("menu-edit-title"));
    createRandomCodeMenuItem.setText(dictionary.getString("menu-edit-createRandom"));
    cltermCountMenu.setText(dictionary.getString("menu-edit-cltermCount"));
    calculationMaxMenu.setText(dictionary.getString("menu-edit-calculationMax"));

    tabMenu.setText(dictionary.getString("menu-tab-title"));
    moveLeftTabMenuItem.setText(dictionary.getString("menu-tab-moveLeft"));
    moveRightTabMenuItem.setText(dictionary.getString("menu-tab-moveRight"));
    deleteTabMenuItem.setText(dictionary.getString("menu-tab-delete"));
    clearTabsMenuItem.setText(dictionary.getString("menu-tab-clear"));

    viewMenu.setText(dictionary.getString("menu-view-title"));
    showCombinatorsCheckMenuItem.setText(dictionary.getString("menu-view-combinators"));

    clcodeTextField.setPromptText(dictionary.getString("mainStage-clcodeTextField"));
    addButton.setText(dictionary.getString("mainStage-addButton"));
    resultTitledPane.setText(dictionary.getString("mainStage-resultTitledPane"));
    previewTitledPane.setText(dictionary.getString("mainStage-previewTitledPane"));
  }

  /**
   * 渡したテキストとトグルのテキストが一致するものを選択する。
   * @param group 対象トグルグループ
   * @param text 対象テキスト
   */
  private void changeSelectionOfToggle(ToggleGroup group, String text) {
    group.getToggles().stream()
        .map(t -> (RadioMenuItem) t)
        .filter(t -> t.getText().equals(text))
        .findFirst()
        .ifPresent(t -> t.setSelected(true));
  }
}
