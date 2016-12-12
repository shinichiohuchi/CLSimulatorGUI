package application.clcode.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lib.string.combinator.CombinatorLogic;

/**
 * CLTermを管理するTreeViewのマネージャクラス。
 * ファーストクラスコレクションまがい。
 * @author shinichi666
 *
 */
public class CLTermTree {
  private TreeView<String> treeView;
  private int maxDepth;
  private int depth;

  /**
   * CLCodeから木構造を生成し、aTreeViewに登録するコンストラクタ。
   * @param aTreeView
   * @param clcode
   */
  public CLTermTree(TreeView<String> aTreeView, String clcode) {
    maxDepth = 0;
    depth = 0;
    treeView = aTreeView;

    TreeItem<String> root = new TreeItem<>(clcode);
    setItem(root, clcode);
    treeView.setRoot(root);
  }

  private void setItem(TreeItem<String> parent, String aClcode) {
    maxDepth = Math.max(depth, maxDepth);
    depth++;

    CombinatorLogic code = new CombinatorLogic(aClcode);
    while (code.getValue().length() != 0) {
      String clterm = code.pollCLTerm();

      TreeItem<String> node = new TreeItem<>(clterm);
      parent.getChildren().add(node);

      if (clterm.startsWith("(")) {
        StringBuilder sb = new StringBuilder(clterm);
        // 括弧の除去
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        setItem(node, sb.toString());
      }
    }

    depth--;
  }

  String getRootValue() {
    return treeView.getRoot().getValue();
  }
}
