package by.mark.table;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ConditionalOperatorTableContent {

    private final SimpleStringProperty condOperator;
    private final SimpleIntegerProperty condOperCount;
    private final SimpleIntegerProperty ifEquivalent;

    public ConditionalOperatorTableContent(String condOperator, int condOperCount, int ifEquivalent) {
        this.condOperator = new SimpleStringProperty(condOperator);
        this.condOperCount = new SimpleIntegerProperty(condOperCount);
        this.ifEquivalent = new SimpleIntegerProperty(ifEquivalent);
    }

    public void setCondOperator(String condOperator) {
        this.condOperator.set(condOperator);
    }

    public void setCondOperCount(int condOperCount) {
        this.condOperCount.set(condOperCount);
    }

    public void setIfEquivalent(int ifEquivalent) {
        this.ifEquivalent.set(ifEquivalent);
    }

    public int getCondOperCount() {
        return condOperCount.get();
    }

    public int getIfEquivalent() {
        return ifEquivalent.get();
    }

    public String getCondOperator() {
        return condOperator.get();
    }

    public SimpleIntegerProperty ifEquivalentProperty() {
        return ifEquivalent;
    }

    public SimpleIntegerProperty condOperCountProperty() {
        return condOperCount;
    }

    public SimpleStringProperty condOperatorProperty() {
        return condOperator;
    }
}
