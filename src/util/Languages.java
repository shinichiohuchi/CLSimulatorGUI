package util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 言語環境の切り替えを担当する列挙型。
 * @author shinichi666
 *
 */
public enum Languages {
    JP("日本語(Japanese)", Locale.JAPAN),
    EN("英語(English)", Locale.US);

  /**
   * コンボボックスなどに登録するテキスト。
   */
  public final String TEXT;
  /**
   * 対応する国
   */
  public final Locale LOCALE;

  private Languages(String aText, Locale aLocale) {
    this.TEXT = aText;
    this.LOCALE = aLocale;
  }

  /**
   * 言語選択コンボボックスで使用するリストを返す。
   * @return ObservableArrayList
   */
  public static ObservableList<String> getLanguageList() {
    List<String> list = Arrays.stream(Languages.values())
        .map(lang -> lang.TEXT)
        .collect(Collectors.toList());
    return FXCollections.observableArrayList(list);
  }

  /**
   * Localeを言語選択のテキストに変換する。
   * @param locale Locale
   * @return 選択テキスト or 不正なLocaleの場合はEN.TEXT
   */
  public static String convertLocaleToText(Locale locale) {
    for (Languages value : Languages.values()) {
      if (value.LOCALE.equals(locale)) {
        return value.TEXT;
      }
    }
    return EN.TEXT;
  }

  /**
   * 選択したテキストをLocaleに変換する。
   * @param text 選択テキスト
   * @return Locale or 不正なテキストの場合はLocale.EN
   */
  public static Locale convertTextToLocale(String text) {
    for (Languages value : Languages.values()) {
      if (value.TEXT.equals(text)) {
        return value.LOCALE;
      }
    }
    return EN.LOCALE;
  }
}
