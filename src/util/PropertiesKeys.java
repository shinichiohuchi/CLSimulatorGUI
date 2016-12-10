package util;

/**
 * プロパティファイルで利用するキーをまとめた定数クラス。
 * @author shinichi666
 */
public enum PropertiesKeys {
    FILE_PATH("filePath"),
    DEFINITION_PATH("definitionPath"),

    CLTERM_COUNT("cltermCount"),
    CALCULATION_MAX("calculationMax"),

    LANGUAGE("languages"),
    RESULT("resultFontSize"),
    PREVIEW("previewFontSize"),
    EDITOR("editorFontSize"),
    AUTO_OPEN("autoOpen"),
    DIVIDER("divider");

  /**
   * キー
   */
  public final String TEXT;

  private PropertiesKeys(String aText) {
    this.TEXT = aText;
  }
}
