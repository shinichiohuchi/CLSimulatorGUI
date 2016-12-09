package application.clcode.tab;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * CLCodeを管理するクラス。
 * @author shinichi666
 */
public class Code {
  private IntegerProperty step;
  private IntegerProperty cltermCount;
  private StringProperty clcode;

  public Code(int s, int c, String cc) {
    step = new SimpleIntegerProperty(s);
    cltermCount = new SimpleIntegerProperty(c);
    clcode = new SimpleStringProperty(cc);
  }

  // **************************************************
  // Getter property methods.
  // **************************************************
  public IntegerProperty stepProperty() {
    return step;
  }

  public IntegerProperty cltermCountProperty() {
    return cltermCount;
  }

  public StringProperty clcodeProperty() {
    return clcode;
  }

  // **************************************************
  // Setter property methods.
  // **************************************************
  public void setStep(int s) {
    step = new SimpleIntegerProperty(s);
  }

  public void setCltermCount(int c) {
    cltermCount = new SimpleIntegerProperty(c);
  }

  public void setClcode(String cc) {
    clcode = new SimpleStringProperty(cc);
  }

}
