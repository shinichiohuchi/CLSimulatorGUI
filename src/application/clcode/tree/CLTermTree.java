package application.clcode.tree;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lib.string.combinator.CombinatorLogic;

/**
 * CLTermを管理するTreeViewのマネージャクラス。
 * ファーストクラスコレクションまがい。
 * @author shinichi666
 */
public class CLTermTree {
  private TreeView<String> treeView;
  private int maxDepth;
  private int depth;

  /**
   * 木構造クラスとして代用するコンストラクタ。<br>
   * TreeViewをGUIに配置する予定がないならこのコンストラクタを利用する。
   * @param clcode CLCode
   */
  public CLTermTree(String clcode) {
    this(new TreeView<>(), clcode);
  }

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

  /**
   * 括弧をたどっていき、一番深い階層から計算を進める。
   * @param parent
   */
  void step(TreeItem<String> parent) {
    ObservableList<TreeItem<String>> children = parent.getChildren();
    if (!children.isEmpty()) {
      if (children.stream().allMatch(c -> c.isLeaf())) {
        StringBuilder sb = new StringBuilder();
        children.stream()
            .map(child -> child.getValue())
            .forEach(sb::append);

        CombinatorLogic clcode = new CombinatorLogic(sb.toString());
        while (clcode.canStep()) {
          clcode.step();
        }
        String value = clcode.getValue();
        if (1 < clcode.getCLTermCount()) {
          value = "(" + value + ")";
        }
        parent.setValue(value);
        parent.getChildren().removeAll(children);
        return;
      }

      children.stream()
          .filter(c -> !c.isLeaf())
          .forEach(this::step);
    }
  }

  /**
   * 括弧内のすべての計算を行う。
   */
  public void stepAll() {
    TreeItem<String> root = treeView.getRoot();
    while (!root.isLeaf()) {
      step(root);
    }
  }

  /**
   * rootのテキストを返す。
   * @return rootText
   */
  public String getRootValue() {
    return treeView.getRoot().getValue();
  }

  /**
   * ルートの一番外の括弧を外す。
   */
  public void removeBracket() {
    String root = getRootValue();
    if (root.startsWith("(") && root.endsWith(")")) {
      StringBuilder sb = new StringBuilder(root);
      sb.deleteCharAt(0);
      sb.deleteCharAt(sb.length()-1);
      treeView.getRoot().setValue(sb.toString());
    }
  }
}
