package application;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FileChooserを開いて取得したファイルを保持するクラス。
 * @author shinichi
 * @version 1.1
 */
public class FileChooserManager {
  /**
   * 開いたファイル
   */
  private File file;
  private final FileChooser fc;
  /**
   * FileChooserにセットするステージ。
   */
  private static final Stage UTIL_STAGE = new Stage(StageStyle.UTILITY);

  /**
   * フィルタのみ定義するコンストラクタ
   * @param aDescription 説明文
   * @param aExtension 対象拡張子
   */
  public FileChooserManager(String aDescription, String aExtension) {
    this((File) null, aDescription, aExtension);
  }

  /**
   * 初期参照ファイルパスを渡すコンストラクタ。
   * @param aFilePath 初期ファイル名
   * @param aDescription 説明文
   * @param aExtension 対象拡張子
   */
  public FileChooserManager(String aFilePath, String aDescription, String aExtension) {
    this(new File(aFilePath), aDescription, aExtension);
  }

  /**
   * 初期参照ファイルを渡すコンストラクタ。
   * @param aFile 初期ファイル
   * @param aDescription 説明文
   * @param aExtension 対象拡張子
   */
  public FileChooserManager(File aFile, String aDescription, String aExtension) {
    file = aFile;
    fc = new FileChooser();
    fc.getExtensionFilters().add(new ExtensionFilter(aDescription, aExtension));

    // @formatter:off
    File dir =
        (file != null && file.exists()) ?
            file.isDirectory() ? file : file.getParentFile()
        : new File(".");
    // @formatter:on
    fc.setInitialDirectory(dir);
  }

  /**
   * 単一選択ダイアログを開く。
   * @return 取得したファイル
   */
  public Optional<File> openFile() {
    File openedFile = fc.showOpenDialog(UTIL_STAGE);
    Optional<File> fileOpt = Optional.ofNullable(openedFile);
    fileOpt.ifPresent(f -> {
      file = f;
      fc.setInitialDirectory(file.getParentFile());
    });
    return fileOpt;
  }

  /**
   * 複数選択ダイアログを開く。
   * @return 取得したファイル
   */
  public Optional<List<File>> openFiles() {
    List<File> openedFiles = fc.showOpenMultipleDialog(UTIL_STAGE);
    Optional<List<File>> filesOpt = Optional.ofNullable(openedFiles);
    filesOpt.ifPresent(f -> {
      file = f.get(0);
      fc.setInitialDirectory(file.getParentFile());
    });
    return filesOpt;
  }

  /**
   * 保存ダイアログを開く。
   * @return 保存したファイル
   */
  public Optional<File> saveFile() {
    File savedFile = fc.showSaveDialog(UTIL_STAGE);
    Optional<File> fileOpt = Optional.ofNullable(savedFile);
    fileOpt.ifPresent(f -> {
      file = f;
      fc.setInitialDirectory(file.getParentFile());
    });
    return fileOpt;
  }

  /**
   * 保持しているファイルを返す。
   * @return 保持しているファイル
   */
  public Optional<File> getFile() {
    return Optional.ofNullable(file);
  }

  @Override
  public String toString() {
    return String.format("FileChooser: %s, File: %s.", fc, file);
  }
}
