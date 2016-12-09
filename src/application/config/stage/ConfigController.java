package application.config.stage;

import java.util.Map;

import javafx.scene.Node;

/**
 * コンフィグステージ内に配置するパネルのコントローラが
 * 実装しないといけないインタフェース。
 * @author shinichi666
 */
public interface ConfigController {
  /**
   * ヘッダーのタイトルに描画するテキストを返す。
   * @return
   */
  String getTitle();

  /**
   * ヘッダーの説明に描画するテキストを返す。
   * @return
   */
  String getDescription();

  /**
   * コントローラのrootを返す。
   * @return
   */
  Node getRoot();

  /**
   * コントローラ内で管理する値のマップを返す。
   * @return
   */
  Map<String, String> getConfigMap();
}
