import java.awt.*;

public class Themes extends Gui{
    static void themeFontColors(String theme) {
        switch (theme) {
            case "Black":
                lblWhere.setForeground(new Color(254, 60, 85));
                JStatement.setForeground(contrastColor);
                //JStatement_2.setForeground(contrastColor);
                statusLbl.setForeground(contrastColor);
                analysisLabel.setForeground(new Color(254, 60, 85));
                iataLbl.setForeground(new Color(254, 60, 85));
                iataResultLbl.setForeground(new Color(248, 250, 1));
                chbxAutoUpdateLbl.setForeground(contrastColor);
                //likeTextField.setBackground(contrastColor);
                textWhereClause.setBackground(contrastColor);
                guiTheme.setBackground(new Color(248, 250, 1));
                color_btn.setBackground(new Color(248, 250, 1));
                devProd.setBackground(new Color(248, 250, 1));
                break;
            case "Dos":
                lblWhere.setForeground(new Color(254, 60, 85));
                JStatement.setForeground(new Color(152, 219, 255));
                //JStatement_2.setForeground(new Color(152, 219, 255));
                analysisLabel.setForeground(new Color(254, 60, 85));
                iataLbl.setForeground(new Color(254, 60, 85));
                iataResultLbl.setForeground(new Color(248, 250, 1));
                statusLbl.setForeground(new Color(152, 219, 255));
                chbxAutoUpdateLbl.setForeground(new Color(152, 219, 255));
                //likeTextField.setBackground(new Color(173, 170, 173));
                textWhereClause.setBackground(new Color(224, 224, 224));
                guiTheme.setBackground(new Color(5,171,172));
                color_btn.setBackground(new Color(5,171,172));
                devProd.setBackground(new Color(5,171,172));
                break;
            case "Orange":
            case "Green":
            case "Brown":
            case "Blue":
            case "Pink":
            case "Gray":
            case "Default":
                lblWhere.setForeground(Gui.fontColor);
                JStatement.setForeground(Gui.fontColor);
                //JStatement_2.setForeground(Gui.fontColor);
                iataLbl.setForeground(Gui.fontColor);
                iataResultLbl.setForeground(Gui.fontColor);
                statusLbl.setForeground(Gui.fontColor);
                chbxAutoUpdateLbl.setForeground(Gui.fontColor);
                analysisLabel.setForeground(Gui.fontColor);
                //likeTextField.setBackground(new Color(255, 255, 255));
                textWhereClause.setBackground(new Color(224, 224, 224));
                guiTheme.setBackground(new Color(238, 238, 238));
                color_btn.setBackground(new Color(238, 238, 238));
                devProd.setBackground(new Color(236, 250, 232));
                break;
        }
    }
}
