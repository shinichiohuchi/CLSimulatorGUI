package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.TextArea;
import lib.string.combinator.CombinatorLogic;
import util.UtilCombinatorLogic;

/**
 * 定義済みコンビネータ一覧を管理するクラス。
 * テキストエリアとそれに関連したメソッドを内包する。
 * @author shinichi666
 *
 */
class DefinitionViewer {
  /**
   * プレビューテキストエリア
   */
  private TextArea textArea;

  /**
   * 監視対象のテキストエリアを渡すコンストラクタ。
   * @param aTextArea テキストエリア
   */
  DefinitionViewer(TextArea aTextArea) {
    textArea = aTextArea;
    appendList(CombinatorLogic.getInitialMacroCombinatorsList());
  }

  /**
   * テキストエリアの表示を更新する。
   */
  void update(File definitionFile) {
    if (definitionFile.exists()) {
      textArea.setText("");
      Path path = definitionFile.toPath();
      try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
        List<String[]> list = br.lines().filter(l -> !l.startsWith("#") && l.length() != 0)
            .map(m -> m.replaceAll("[ ||　||\t]", "").split(",")).collect(Collectors.toList());

        // CombinatorLogicに初期定義リストの後に
        // ファイルから読み取った定義リストを追加する。
        List<String[]> definitionList = new ArrayList<>();
        definitionList.addAll(CombinatorLogic.getInitialMacroCombinatorsList());
        definitionList.addAll(list);
        appendList(definitionList);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 定義リストをテキストエリアに追加する。
   * @param list 定義リスト
   */
  private void appendList(List<String[]> definitionList) {
    definitionList.stream().forEach(line -> {
      Object[] checkedCode = UtilCombinatorLogic.getCheckedDefinition(line);
      textArea.appendText((String) checkedCode[0]);
    });
    textArea.positionCaret(0);
  }

  // ************************************************************
  // Getter
  // ************************************************************
  /**
   * テキストエリアのフォントサイズを返す。
   * @return フォントサイズ
   */
  double getFontSize() {
    return textArea.getFont().getSize();
  }

  // ************************************************************
  // Setter
  // ************************************************************
  /**
   * フォントサイズをセットする。
   * @param fontSize フォントサイズ
   */
  void setFontSize(int fontSize) {
    setFontSize("" + fontSize);
  }

  /**
   * フォントサイズをセットする。
   * @param fontSize フォントサイズ
   */
  void setFontSize(String fontSize) {
    textArea.setStyle("-fx-font-size:" + fontSize + "pt;");
  }
}
