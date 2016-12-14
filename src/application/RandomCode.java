package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import lib.string.combinator.CombinatorLogic;

/**
 * 乱数でCLCodeを生成するクラス。
 * @author shinichi666
 */
public class RandomCode {
  /**
   * CLTermRadioMenuItemのみを保持するグループ。
   */
  private final ToggleGroup cltermCountGroup;

  /**
   * CLTermRadioMenuItemを保持するグループを渡すコンストラクタ。
   * @param cltermCountGroup グループ。
   */
  RandomCode(ToggleGroup cltermCountGroup) {
    this.cltermCountGroup = cltermCountGroup;
  }

  /**
   * ランダムなCLCodeを生成する。
   * @param combinatorsList リスト or nullの場合は初期コンビネータのみ
   * @return CLCode
   */
  String makeRandomCode() {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    // フィールドのコンビネータリストは初期コンビネータ5種を保持していないので、
    // 新しくリストを生成して、先頭に初期コンビネータを追加する。
    List<String[]> newCombinatorsList = new ArrayList<>();
    newCombinatorsList.addAll(CombinatorLogic.getInitialMacroCombinatorsList());
    List<String[]> combinatorsList = MainController.getCombinatorsList();
    if (combinatorsList != null) {
      newCombinatorsList.addAll(combinatorsList);
    }
    int randomMax = newCombinatorsList.size();
    int cltermCount = getCLTermCount();

    // 文字列を連結し乱数コードを生成
    AtomicInteger bracketCount = new AtomicInteger(0);
    IntStream.range(0, cltermCount)
        .forEach(i -> {
          insertBracket(sb, bracketCount);
          int index = random.nextInt(randomMax);
          String[] array = newCombinatorsList.get(index);
          sb.append(array[0]);
        });

    // 閉じきれなかった括弧を閉じる処理
    while (0 < bracketCount.getAndDecrement()) {
      sb.append(')');
    }

    return sb.toString();
  }

  /**
   * 10分の1の確率で括弧を挿入する。
   * @param sb 対象StringBuilder
   * @param bracketCount 括弧の数
   */
  private void insertBracket(StringBuilder sb, AtomicInteger bracketCount) {
    Random random = new Random();
    int randomInt = random.nextInt(10);
    if (randomInt == 0) {
      sb.append('(');
      bracketCount.getAndIncrement();
    } else if (randomInt == 1
        && 1 < bracketCount.get()
        && !sb.toString().endsWith("(")) {
      sb.append(')');
      bracketCount.getAndDecrement();
    }
  }

  /**
   * 乱数生成に使用するCLTermの数を返す。
   * @return CLtermの数
   */
  int getCLTermCount() {
    RadioMenuItem countRadio = (RadioMenuItem) cltermCountGroup.getSelectedToggle();
    return Integer.parseInt(countRadio.getText());
  }
}
