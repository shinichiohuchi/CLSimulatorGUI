package application.clcode.tab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import application.MainController;
import application.clcode.tree.TreeViewStage;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lib.string.combinator.CombinatorLogic;

/**
 * 拡張タブクラスのコントローラクラス。
 * @author shinichi666
 */
public class CLCodeTableTabController {
  private MainController mainController;
  @FXML private TableView<Code> tableView = new TableView<>();
  @FXML private TableColumn<Code, Integer> stepColumn = new TableColumn<>("Step");
  @FXML private TableColumn<Code, Integer> cltermCountColumn = new TableColumn<>("CLT");
  @FXML private TableColumn<Code, String> clcodeColumn = new TableColumn<>("Code");

  @FXML private ContextMenu contextMenu;
  @FXML private MenuItem copyMenuItem;
  @FXML private MenuItem treeMenuItem;

  @FXML
  private void initialize() {
    stepColumn.setCellValueFactory(new PropertyValueFactory<Code, Integer>("step"));
    cltermCountColumn.setCellValueFactory(new PropertyValueFactory<Code, Integer>("cltermCount"));
    clcodeColumn.setCellValueFactory(new PropertyValueFactory<Code, String>("clcode"));
  }

  /**
   * テーブルが保持するCLCodeのデータをテキストファイルに出力する。
   * @param saveFile 保存するファイル
   */
  public void outputCLCodeToFile(File saveFile) {
    List<Code> list = tableView.getItems();
    if (!list.isEmpty()) {
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
    }
  }

  /**
   * TableViewのフォントサイズを変更する。
   * @param fontSize フォントサイズ
   */
  public void changeFontSize(int fontSize) {
    tableView.setStyle("-fx-font-size: " + fontSize + "pt;");
  }

  /**
   * 選択中のレコードのCLCodeをクリップボードにコピー
   */
  @FXML
  private void copyMenuItemOnClicked() {
    if (!tableView.getSelectionModel().isEmpty()) {
      Clipboard clipboard = Clipboard.getSystemClipboard();
      ClipboardContent content = new ClipboardContent();
      int index = tableView.getSelectionModel().getSelectedIndex();
      String clcode = tableView.getItems().get(index).clcodeProperty().get();
      content.putString(clcode);
      clipboard.setContent(content);
    }
  }

  /**
   * 木構造ビューを表示する。
   */
  @FXML
  private void treeMenuItemOnAction() {
    if (!tableView.getSelectionModel().isEmpty()) {
      String clcode = tableView.getSelectionModel().getSelectedItem().clcodeProperty().get();
      TreeViewStage stage = new TreeViewStage(clcode);
      stage.showAndWait();
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
    CombinatorLogic code = new CombinatorLogic(clcode, mainController.getCombinatorsList());
    int step = 0;
    int width = 0;
    while (code.canStep() && (max <= 0 || step <= max)) {
      step++;
      int cltermCount = code.getCLTermCount();
      String value = code.getValue();

      // 最長文字列の長さに合わせてタブの幅を変更する。
      int newWidth = value.length() * 10;
      width = Math.max(width, newWidth);
      width = Math.max(width, 480);
      clcodeColumn.setPrefWidth(width);

      tableView.getItems().add(new Code(step, cltermCount, value));
      code.step();
    }
  }

  void setMainController(MainController aMainController) {
    mainController = aMainController;
  }
}
