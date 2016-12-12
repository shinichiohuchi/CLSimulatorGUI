package application.clcode.tree;

import java.io.File;
import java.util.Optional;

import application.FileChooserManager;
import application.IOXml;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;

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
}
