package application.clcode.tree;

import application.FileChooserManager;
import application.IOXml;
import application.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TreeViewController {
  private FileChooserManager manager = new FileChooserManager("Text Files", "*.xml");
  private CLTermTree clTermTree;

  // ************************************************************
  // コンポーネント
  // ************************************************************
  @FXML private TreeView<String> treeView;
  @FXML private Button outputButton;
  @FXML private Button closeButton;

  @FXML
  private void initialize() {
    setLanguages();
  }

  void setCLCode(String clcode) {
    clTermTree = new CLTermTree(treeView, clcode);
  }

  @FXML
  private void outputButtonOnAction() {
    manager.saveFile().ifPresent(f -> {
      IOXml ioXml = new IOXml(clTermTree.getRootValue());
      ioXml.outputXml(f);
    });
  }

  @FXML
  private void closeButtonOnAction() {
    closeButton.getScene().getWindow().hide();
  }

  @FXML
  private void treeViewOnKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ESCAPE) {
      closeButtonOnAction();
    }
  }

  /**
   * 言語環境に合わせたテキストをセット
   */
  private void setLanguages() {
    outputButton.setText(MainController.dictionary.getString("menu-file-saveXml"));
    closeButton.setText(MainController.dictionary.getString("common-close"));
  }
}
