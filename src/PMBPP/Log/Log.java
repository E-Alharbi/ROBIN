package PMBPP.Log;

import java.util.List;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Label;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.indvd00m.ascii.render.elements.Rectangle;
import com.jakewharton.fliptables.FlipTable;

import PMBPP.ML.Model.Parameters;

/*
 * Printing log statements  
 */
public class Log {

	public void PseudoText(String Txt) {
		IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(Txt.length() * 10).height(20);
		builder.element(new PseudoText(Txt, false));
		ICanvas canvas = render.render(builder.build());
		String s = canvas.getText();

		System.out.println(s);
	}

	public void TxtInRectangle(String Txt) {
		IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(Txt.length() + 5).height(5);
		builder.element(new Rectangle());
		builder.element(new Label(Txt, 1, 2, Txt.length()));
		ICanvas canvas = render.render(builder.build());
		String s = canvas.getText();
		if (Parameters.Log.equals("T"))
			System.out.println(s);
	}

	public String CreateTable(List<String> headersList, List<List<String>> rowsList) {

		String[] headers = new String[headersList.size()];
		headers = headersList.toArray(headers);
		String[][] data = new String[rowsList.size()][];
		String[] blankArray = new String[0];
		for (int i = 0; i < rowsList.size(); i++) {
			data[i] = rowsList.get(i).toArray(blankArray);
		}

		return FlipTable.of(headers, data);

	}

	public void Error(Object obj, String msg) {
		if (Parameters.Log.equals("T"))
			System.out.println("ERROR: " + msg + " (" + obj.getClass().getName() + ")");

		System.exit(-1); // even if log is F when need to stop the running
	}

	public void Info(Object obj, String msg) {
		if (Parameters.Log.equals("T"))
			System.out.println("INFO: " + msg + " (" + obj.getClass().getName() + ")");
	}

	public void Warning(Object obj, String msg) {
		if (Parameters.Log.equals("T"))
			System.out.println("WARNING: " + msg + " (" + obj.getClass().getName() + ")");
	}
}
