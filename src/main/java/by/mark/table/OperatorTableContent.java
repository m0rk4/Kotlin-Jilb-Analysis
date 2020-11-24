package by.mark.table;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OperatorTableContent {

    private final SimpleStringProperty operator;
    private final SimpleIntegerProperty operatorCount;

    public OperatorTableContent(String operator, int operatorCount) {
        this.operator = new SimpleStringProperty(operator);
        this.operatorCount = new SimpleIntegerProperty(operatorCount);
    }

    public SimpleIntegerProperty operatorCountProperty() {
        return operatorCount;
    }

    public SimpleStringProperty operatorProperty() {
        return operator;
    }

    public int getOperatorCount() {
        return operatorCount.get();
    }

    public String getOperator() {
        return operator.get();
    }

    public void setOperator(String operator) {
        this.operator.set(operator);
    }

    public void setOperatorCount(int operatorCount) {
        this.operatorCount.set(operatorCount);
    }
}
