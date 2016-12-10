package application.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.stage.Stage;

import java.util.Optional;
import java.util.Properties;

/**
 * オプション設定の入出力を管理するクラス。
 * @author shinichi666
 *
 */
public class Config {
  private Properties properties;
  private Map<String, String> propertiesMap = new HashMap<String, String>();

  private static final String DIR_PATH = "./properties";
  private final File CONFIG_FILE;

  /**
   * ファイル名のプロパティファイルをロードするコンストラクタ。
   * @param fileName ファイル名
   */
  public Config(String fileName) {
    properties = new Properties();

    File dir = new File(DIR_PATH);
    dir.mkdirs();

    CONFIG_FILE = new File(DIR_PATH + "/" + fileName);
    if (CONFIG_FILE.exists()) {
      load();
      return;
    }
  }

  private void load() {
    try (InputStream is = new FileInputStream(CONFIG_FILE)) {
      properties.loadFromXML(is);
    } catch (FileNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  /**
   * プロパティファイルを出力する。
   */
  public void write() {
    for (Entry<String, String> entry : propertiesMap.entrySet()) {
      properties.setProperty(entry.getKey(), entry.getValue());
    }

    try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
      properties.storeToXML(os, "すべての設定を保持するプロパティ");
    } catch (FileNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }

  /**
   * 値を取り出す。
   * @param key キー
   * @return 値
   */
  public Optional<String> getProperty(String key) {
    return Optional.ofNullable(properties.getProperty(key));
  }

  /**
   * プロパティをセットする。
   * @param key キー
   * @param value 値
   */
  public void setProperty(String key, String value) {
    properties.setProperty(key, value);
  }

  /**
   * 引数に渡したマップのキーと値からプロパティをセットする。
   * @param map
   *          key = プロパティファイルのキー<br>
   *          value = プロパティファイルの値
   */
  public void setProperties(Map<String, String> map) {
    map.entrySet().stream()
        .forEach(entry -> {
          properties.setProperty(entry.getKey(), entry.getValue());
        });
  }

  /**
   * Stageのx, y, width, heightをセットする。
   * @param stage 対象stage
   */
  public void setProperties(Stage stage) {
    properties.setProperty("x", "" + stage.getX());
    properties.setProperty("y", "" + stage.getY());
    properties.setProperty("width", "" + stage.getWidth());
    properties.setProperty("height", "" + stage.getHeight());
  }

  /**
   * Stageの座標と幅の変更させる。
   * @param stage 対象stage
   */
  public void changeStageAxisAndPosition(Stage stage) {
    this.getProperty("x").map(Double::parseDouble).ifPresent(stage::setX);
    this.getProperty("y").map(Double::parseDouble).ifPresent(stage::setY);
    this.getProperty("width").map(Double::parseDouble).ifPresent(stage::setWidth);
    this.getProperty("height").map(Double::parseDouble).ifPresent(stage::setHeight);
  }
}
