package fileBuddy;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class FileListRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component component = (Component) value;
		component.setFont((new Font("SansSerif", Font.PLAIN, 14)));
		component.setForeground(isSelected ? Color.CYAN : Color.BLACK);
		return component;
	}

}
