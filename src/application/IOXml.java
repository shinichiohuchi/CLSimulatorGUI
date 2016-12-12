package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lib.string.combinator.CombinatorLogic;

/**
 * XMLファイルの入出力を管理するクラス。
 * @author shinichi666
 */
public class IOXml {

  private Document doc;
  private Element root;
  private int depth = 1;
  private int maxDepth = 0;

  private static final String NODE = "node";
  private static final String CLTERM = "clterm";
  private static final String LAYER = "layer";

  /**
   * 引数のCLCodeからXML形式の木構造を生成する。
   * @param aClcode CLCode
   */
  public IOXml(String aClcode) {
    try {
      doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      root = doc.createElement(NODE);
      root.setAttribute(CLTERM, aClcode);
      root.setAttribute(LAYER, "" + depth);
      doc.appendChild(root);
    } catch (ParserConfigurationException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }

    setNode(root, aClcode);
  }

  /**
   * aClcodeのテキストを保持するノードをセットする。<br>
   * この時、セットしたテキストが括弧で括られた文字列であった場合、
   * そのノードの子にさらにCLTermで分割したノードをセットする。
   * @param parent aCLcodeを登録する親ノード
   * @param aClcode 登録するCLCode
   */
  private void setNode(Element parent, String aClcode) {
    maxDepth = Math.max(depth, maxDepth);
    depth++;

    CombinatorLogic code = new CombinatorLogic(aClcode);
    while (code.getValue().length() != 0) {
      String clterm = code.pollCLTerm();

      Element node = doc.createElement(NODE);
      node.setAttribute(CLTERM, clterm);
      parent.appendChild(node);

      if (clterm.startsWith("(")) {
        node.setAttribute(LAYER, "" + depth);
        StringBuilder sb = new StringBuilder(clterm);
        // 括弧の除去
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        setNode(node, sb.toString());
      }
    }

    depth--;
  }

  /**
   * XMLファイルを出力する。
   * @param filePath 出力ファイルパス
   */
  public void outputXml(String filePath) {
    File xml = new File(filePath);
    outputXml(xml);
  }

  /**
   * XMLファイルを出力する。
   * @param xml XMLファイル
   */
  public void outputXml(File xml) {
    DOMSource source = new DOMSource(root);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

      FileOutputStream os = new FileOutputStream(xml);
      StreamResult result = new StreamResult(os);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    } catch (TransformerException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }
  }
}
