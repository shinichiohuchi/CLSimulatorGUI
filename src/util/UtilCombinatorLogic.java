package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lib.string.combinator.CombinatorLogic;

/**
 * コンビネータ論理のユーティリティクラス。
 * @author shinichi666
 *
 */
public class UtilCombinatorLogic {
  private static final char[] ALPHABETS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  /**
   * 渡された配列がコンビネータの書式の条件を満たしているかを調べ、<br>
   * 満たしていた場合は整形後の文字列を返し、<br>
   * 満たしていなかった場合はエラー文字列を返す。<br>
   * また、正しい文字列かどうかを配列1に格納して返す。
   * @param line 定義配列
   * @return
   *       String [0] = 整形後の文字列<br>
   *       Boolean [1] = 正しい文字列かどうか<br>
   */
  public static Object[] getCheckedDefinition(String[] line) {
    Object[] definition = new Object[2];

    if (line.length < 2 || 3 < line.length) {
      definition[0] = "',' count is not correct." + LINE_SEPARATOR;
      definition[1] = false;
      return definition;
    }

    String name = line[0];
    String number = line[1];
    CombinatorLogic code = null;
    if (number.matches("[0-9]*")) {
      // 数値が定義されている配列だった場合
      // 引数の数だけ小文字のアルファベットを
      // コンビネータ名の後ろに結合
      int argsCount = Integer.valueOf(number);
      char[] alphabetArray = Arrays.copyOf(ALPHABETS, argsCount);
      name = name + String.valueOf(alphabetArray);
      code = new CombinatorLogic(name, line);
    } else {
      // 数値未定義(配列長が2の場合、
      // コンビネータ名のみを渡す
      code = new CombinatorLogic(name, line);
    }
    CombinatorLogic.addMacroCombinatorList(line);
    code.step();
    if (!code.isCorrectCode()) {
      definition[0] = name + " -> Format has ERROR!" + LINE_SEPARATOR;
      definition[1] = false;
      return definition;
    }
    definition[0] = name + " -> " + code.getValue() + LINE_SEPARATOR;
    definition[1] = true;
    return definition;
  }

  /**
   * コンビネータの定義を配列のリストして取り出す。
   * @param file コンビネータ定義ファイル
   * @return #で始まる行の無視した、カンマ区切りの配列
   */
  public static List<String[]> makeCombinatorsDefinitionList(File file) {
    Path path = file.toPath();
    try (BufferedReader br = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
      return br.lines().filter(l -> !l.startsWith("#") && l.length() != 0)
          .map(m -> m.replaceAll("[ ||　||\t]", "").split(",")).collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
