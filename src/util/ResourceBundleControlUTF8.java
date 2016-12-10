package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * リソースバンドルを他のUTF8で読み取るためのクラス。
 *
 */
public class ResourceBundleControlUTF8 extends ResourceBundle.Control {
  /**
   * UTF-8 エンコーディングのプロパティファイルから ResourceBundle
   * オブジェクトを生成します。
   * <p>
   * 参考 :
   * <a href=
   * "http://jgloss.sourceforge.net/jgloss-core/jacoco/jgloss.util/UTF8ResourceBundleControl.java.html">
   * http://jgloss.sourceforge.net/jgloss-core/jacoco/jgloss.util/UTF8ResourceBundleControl.java.html
   * </a>
   * </p>
   * @throws IllegalAccessException 例外
   * @throws InstantiationException 例外
   * @throws IOException 例外
   */
  @Override
  public ResourceBundle newBundle(String baseName, Locale locale, String format,
      ClassLoader loader, boolean reload)
      throws IllegalAccessException, InstantiationException, IOException {
    String bundleName = toBundleName(baseName, locale);
    String resourceName = toResourceName(bundleName, "properties");
    try (InputStream is = loader.getResourceAsStream(resourceName);
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader reader = new BufferedReader(isr)) {
      return new PropertyResourceBundle(reader);
    }
  }
}
