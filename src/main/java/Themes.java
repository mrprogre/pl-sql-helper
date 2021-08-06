import java.awt.*;

public class Themes{
    void themeFontColors(String theme) {
        switch (theme) {
            case "Black":
                Gui.lblWhere.setForeground(new Color(254, 60, 85));
                Gui.JStatement.setForeground(Gui.contrastColor);
                //JStatement_2.setForeground(contrastColor);
                Gui.statusLbl.setForeground(Gui.contrastColor);
                Gui.analysisLabel.setForeground(new Color(254, 60, 85));
                Gui.iataLbl.setForeground(new Color(254, 60, 85));
                Gui.iataResultLbl.setForeground(new Color(248, 250, 1));
                Gui.chbxAutoUpdateLbl.setForeground(Gui.contrastColor);
                //likeTextField.setBackground(contrastColor);
                Gui.textWhereClause.setBackground(Gui.contrastColor);
                Gui.guiTheme.setBackground(new Color(248, 250, 1));
                Gui.color_btn.setBackground(new Color(248, 250, 1));
                Gui.devProd.setBackground(new Color(248, 250, 1));
                break;
            case "Dos":
                Gui.lblWhere.setForeground(new Color(254, 60, 85));
                Gui.JStatement.setForeground(new Color(152, 219, 255));
                //JStatement_2.setForeground(new Color(152, 219, 255));
                Gui.analysisLabel.setForeground(new Color(254, 60, 85));
                Gui.iataLbl.setForeground(new Color(254, 60, 85));
                Gui.iataResultLbl.setForeground(new Color(248, 250, 1));
                Gui.statusLbl.setForeground(new Color(152, 219, 255));
                Gui.chbxAutoUpdateLbl.setForeground(new Color(152, 219, 255));
                //likeTextField.setBackground(new Color(173, 170, 173));
                Gui.textWhereClause.setBackground(new Color(224, 224, 224));
                Gui.guiTheme.setBackground(new Color(5,171,172));
                Gui.color_btn.setBackground(new Color(5,171,172));
                Gui.devProd.setBackground(new Color(5,171,172));
                break;
            case "Orange":
            case "Green":
            case "Brown":
            case "Blue":
            case "Pink":
            case "Gray":
            case "Default":
                Gui.lblWhere.setForeground(Gui.fontColor);
                Gui.JStatement.setForeground(Gui.fontColor);
                //JStatement_2.setForeground(Gui.fontColor);
                Gui.iataLbl.setForeground(Gui.fontColor);
                Gui.iataResultLbl.setForeground(Gui.fontColor);
                Gui.statusLbl.setForeground(Gui.fontColor);
                Gui.chbxAutoUpdateLbl.setForeground(Gui.fontColor);
                Gui.analysisLabel.setForeground(Gui.fontColor);
                //likeTextField.setBackground(new Color(255, 255, 255));
                Gui.textWhereClause.setBackground(new Color(224, 224, 224));
                Gui.guiTheme.setBackground(new Color(238, 238, 238));
                Gui.color_btn.setBackground(new Color(238, 238, 238));
                Gui.devProd.setBackground(new Color(236, 250, 232));
                break;
        }
    }
}
