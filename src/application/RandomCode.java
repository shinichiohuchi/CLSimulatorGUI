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
  private final ToggleGroup cltermCountGroup;

  public RandomCode(ToggleGroup cltermCountGroup) {
    this.cltermCountGroup = cltermCountGroup;
  }

  /**
   * ランダムなCLCodeを生成する。
   * @param combinatorsList リスト or nullの場合は初期コンビネータのみ
   * @return CLCode
   */
  String makeRandomCode(final List<String[]> combinatorsList) {
    StringBuffer buffer = new StringBuffer();
    Random random = new Random();

    // フィールドのコンビネータリストは初期コンビネータ5種を保持していないので、
    // 新しくリストを生成して、先頭に初期コンビネータを追加する。
    List<String[]> newCombinatorsList = new ArrayList<>();
    newCombinatorsList.addAll(CombinatorLogic.getInitialMacroCombinatorsList());
    if (combinatorsList != null) {
      newCombinatorsList.addAll(combinatorsList);
    }
    int size = newCombinatorsList.size();

    int count = getCLTermCount();

    // 文字列を連結し乱数コードを生成
    AtomicInteger bracketCount = new AtomicInteger(0);
    IntStream.range(0, count)
        .parallel()
        .forEach(i -> {
          int index = random.nextInt(size);

          // 括弧を追加して入れ子構造を実装
          if (index == 0) {
            buffer.append('(');
            bracketCount.getAndIncrement();
          } else if (index == 1 && 1 < bracketCount.get()) {
            buffer.append(')');
            bracketCount.set(0);
          }

          String[] array = newCombinatorsList.get(index);
          buffer.append(array[0]);
        });

    // 閉じきれなかった括弧を閉じる処理
    while (0 < bracketCount.getAndDecrement()) {
      buffer.append(')');
    }

    return buffer.toString();
  }

  /**
   * 乱数生成に使用するCLTermの数を返す。
   * @return
   */
  int getCLTermCount() {
    RadioMenuItem countRadio = (RadioMenuItem) cltermCountGroup.getSelectedToggle();
    return Integer.valueOf(countRadio.getText());
  }
}
