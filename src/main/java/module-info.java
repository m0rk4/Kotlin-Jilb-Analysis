module by.mark {
    requires javafx.controls;
    requires javafx.fxml;

    opens by.mark to javafx.fxml;
    opens by.mark.table to javafx.base;
    exports by.mark;
}