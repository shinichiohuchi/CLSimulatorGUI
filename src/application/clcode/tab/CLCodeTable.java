package application.clcode.tab;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.TableView;

/**
 * テーブルビューのファーストクラスコレクション。
 * @author shinichi666
 *
 */
public class CLCodeTable {
  private TableView<Code> tableView;

  CLCodeTable(TableView<Code> aTableView) {
    tableView = aTableView;
  }

  /**
   * 選択中のレコードのCLCodeを返す。
   * @return CLCode
   */
  Optional<String> getSelectedCLCode() {
    String clcode = null;
    if (!tableView.getSelectionModel().isEmpty()) {
      clcode = tableView.getSelectionModel().getSelectedItem().clcodeProperty().get();
    }
    return Optional.ofNullable(clcode);
  }

  /**
   * テーブルのすべてのレコードを返す。
   * @return すべてのレコード
   */
  Optional<List<Code>> getItems() {
    List<Code> codeList = tableView.getItems();
    if (codeList.isEmpty()) {
      return Optional.ofNullable(null);
    }
    return Optional.ofNullable(codeList);
  }
}
