package application.clcode.tab;

import java.io.File;
import java.io.IOException;

import application.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

/**
 * CLCodeの計算結果を表示するテーブルを保持する拡張タブクラス。
 * @author shinichi666
 */
public class CLCodeTableTab extends Tab {
  private CLCodeTableTabController controller;

  public CLCodeTableTab(String tabTitle, MainController aMainController) {
    super(tabTitle);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("CLCodeTableTab.fxml"));
    try {
      AnchorPane anchorPane = (AnchorPane) loader.load();
      controller = loader.getController();
      setContent(anchorPane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * テーブルが保持するCLCodeのデータをテキストファイルに出力する。
   * @param saveFile
   *          保存するファイル
   */
  public void outputCLCodeToFile(File saveFile) {
    controller.outputCLCodeToFile(saveFile);
  }

  /**
   * 最後のレコードの計算結果をXMLファイルとして出力する。
   * @param saveFile 保存ファイル
   */
  public void outputCLCodeAsXml(File saveFile) {
    controller.outputCLCodeAsXml(saveFile);
  }

  /**
   * TableViewのフォントサイズを変更する。
   * @param fontSize
   *          フォントサイズ
   */
  public void changeFontSize(int fontSize) {
    controller.changeFontSize(fontSize);
  }

  /**
   * 渡されたCLCodeを計算し、計算結果をテーブルに格納する。
   * @param clcode
   *          CLCode
   * @param max
   *          計算回数上限
   */
  public void setCLCode(String clcode, int max) {
    controller.setCLCode(clcode, max);
  }
}
