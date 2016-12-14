package application.clcode.tab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import application.FileChooserManager;
import application.IOXml;
import application.MainController;
import application.clcode.tree.CLTermTree;
import application.clcode.tree.TreeViewStage;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lib.string.combinator.CombinatorLogic;
import util.PropertiesKeys;

/**
 * 拡張タブクラスのコントローラクラス。
 * @author shinichi666
 */
public class CLCodeTableTabController {
  private FileChooserManager manager = new FileChooserManager("Text Files", "*.xml");
  /**
   * CLCodeTableを管理するフィールド
   */
  private CLCodeTable clcodeTable;

  // ************************************************************
  // コンポーネント
  // ************************************************************
  @FXML private TableView<Code> tableView = new TableView<>();
  @FXML private TableColumn<Code, Integer> stepColumn = new TableColumn<>("Step");
  @FXML private TableColumn<Code, Integer> cltermCountColumn = new TableColumn<>("CLT");
  @FXML private TableColumn<Code, String> clcodeColumn = new TableColumn<>("Code");

  @FXML private ContextMenu contextMenu;
  @FXML private MenuItem copyMenuItem;
  @FXML private MenuItem treeMenuItem;
  @FXML private MenuItem saveAsXmlMenuItem;

  @FXML
  private void initialize() {
    clcodeTable = new CLCodeTable(tableView);

    stepColumn.setCellValueFactory(new PropertyValueFactory<Code, Integer>("step"));
    cltermCountColumn.setCellValueFactory(new PropertyValueFactory<Code, Integer>("cltermCount"));
    clcodeColumn.setCellValueFactory(new PropertyValueFactory<Code, String>("clcode"));
  }

  /**
   * テーブルが保持するCLCodeのデータをテキストファイルに出力する。
   * @param saveFile 保存するファイル
   */
  void outputCLCodeToFile(File saveFile) {
    clcodeTable.getItems().ifPresent(list -> {
      try (PrintWriter pw = new PrintWriter(
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8")))) {
        List<String> clcodeList = list.stream().map(m -> m.clcodeProperty().get())
            .collect(Collectors.toList());

        for (String clcode : clcodeList) {
          pw.println(clcode);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * テーブルの最後のレコードをXMLファイルとして出力する。
   * @param file 出力ファイル
   */
  void outputCLCodeAsXml(File file) {
    clcodeTable.getLastItem().ifPresent(item -> {
      IOXml ioXml = new IOXml(item.clcodeProperty().get());
      ioXml.outputXml(file);
    });
  }

  /**
   * TableViewのフォントサイズを変更する。
   * @param fontSize フォントサイズ
   */
  public void changeFontSize(int fontSize) {
    tableView.setStyle("-fx-font-size: " + fontSize + "pt;");
  }

  /**
   * クリックイベント。<br>
   * <ol>
   * <li>
   * <p>
   * ダブルクリックで選択したCLCodeのツリーを表示
   * </p>
   * </li>
   * </ol>
   * @param event マウスイベント
   */
  @FXML
  private void tableViewOnMouseClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      treeMenuItemOnAction();
    }
  }

  /**
   * エンターキーを押したときに、選択状態にあるレコードをツリー表示。
   * @param event キーイベント
   */
  @FXML
  private void tablViewOnKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      treeMenuItemOnAction();
    }

  }

  @FXML
  private void contextMenuOnShown() {
    copyMenuItem.setText(MainController.dictionary.getString("tableView-contextMenu-copy"));
    treeMenuItem.setText(MainController.dictionary.getString("tableView-contextMenu-showTreeView"));
    saveAsXmlMenuItem
        .setText(MainController.dictionary.getString("tableView-contextMenu-saveAsXml"));
  }

  /**
   * 選択中のレコードのCLCodeをクリップボードにコピー
   */
  @FXML
  private void copyMenuItemOnAction() {
    clcodeTable.getSelectedCLCode().ifPresent(clcode -> {
      Clipboard clipboard = Clipboard.getSystemClipboard();
      ClipboardContent content = new ClipboardContent();
      content.putString(clcode);
      clipboard.setContent(content);
    });
  }

  /**
   * 木構造ビューを表示する。
   */
  @FXML
  private void treeMenuItemOnAction() {
    clcodeTable.getSelectedCLCode().ifPresent(clcode -> {
      TreeViewStage stage = new TreeViewStage(clcode);
      stage.showAndWait();
    });
  }

  /**
   * ファイルチューザを使用して、XMLファイル形式でデータを保存する。
   */
  @FXML
  private void saveAsXmlOnAction() {
    clcodeTable.getSelectedCLCode().ifPresent(clcode -> {
      manager.saveFile().ifPresent(file -> {
        IOXml ioXml = new IOXml(clcode);
        ioXml.outputXml(file);
      });
    });
  }

  /**
   * 最後のレコードの括弧内を計算する。<br>
   * コンフィグ設定がオンの時だけ計算を実行する。
   */
  private void calculateBracket() {
    MainController.CONFIG.getProperty(PropertiesKeys.CALCULATION_BRACKET.TEXT)
        .map(Boolean::valueOf)
        .filter(b -> b)
        .ifPresent(calc -> {
          clcodeTable.getItems()
              .ifPresent(list -> {
                int listSize = list.size();
                Code code = list.get(listSize - 1);
                String clcode = code.clcodeProperty().get();
                CLTermTree tree = new CLTermTree(clcode);
                tree.stepAll();
                tree.removeBracket();

                // 最後のレコードに新しく追加
                String resultCLCode = tree.getRootValue();
                changeColumnWidth(resultCLCode);
                clcodeTable.addItem(resultCLCode);
              });
        });
  }

  /**
   * 文字列の長さに合わせてCLCodeカラム幅を変更する。
   * @param value 文字列
   */
  private void changeColumnWidth(String value) {
    double currentWidth = clcodeColumn.getPrefWidth();
    double newWidth = value.length() * 10.0;
    if (currentWidth < newWidth) {
      clcodeColumn.setPrefWidth(newWidth);
    }
  }

  /**
   * 渡されたCLCodeを計算し、計算結果をテーブルに格納する。
   * 計算回数に上限を指定でき、回数上限の指定が0以下だった場合、
   * 計算が引数不足で終了するまで計算を続ける。 上限を0以下に指定した場合は、無限ループが発生する可能性がある。
   * @param clcode CLCode
   * @param max 計算回数上限
   */
  public void setCLCode(String clcode, int max) {
    CombinatorLogic code = new CombinatorLogic(clcode, MainController.getCombinatorsList());
    int step = 0;
    while (code.canStep() && (max <= 0 || step <= max)) {
      step++;
      int cltermCount = code.getCLTermCount();
      String value = code.getValue();
      changeColumnWidth(value);
      tableView.getItems().add(new Code(step, cltermCount, value));
      code.step();
    }
    calculateBracket();
  }
}
