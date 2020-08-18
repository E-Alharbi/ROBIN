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
		if (Parameters.getLog().equals("T"))
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
public String CreateHTMLTable(List<String> headersList, List<List<String>> rowsList) {
	String Table="<tr>";
	for(int i=0 ; i < headersList.size() ; ++i) {
		Table+="<th>"+headersList.get(i)+"</th>";
	}
	Table+="</tr>";
	for(int i=0 ; i < rowsList.size(); ++i) {
		Table+="<tr>";
		for(int r=0 ; r < rowsList.get(i).size();++r) {
			Table+="<td>"+rowsList.get(i).get(r)+"</td>";
		}
		Table+="</tr>";
	}
	
	return Table;
}
	public void Error(Object obj, String msg) {
		if (Parameters.getLog().equals("T"))
			System.out.println("ERROR: " + msg + " (" + obj.getClass().getName() + ")");

		System.exit(-1); // even if log is F when need to stop the running
	}

	public void Info(Object obj, String msg) {
		if (Parameters.getLog().equals("T"))
			System.out.println("INFO: " + msg + " (" + obj.getClass().getName() + ")");
	}
	public void Info(Object obj, String msg, boolean RemoveLastLine) {
		if(RemoveLastLine==true)
		System.out.print("\rINFO: " + msg + " (" + obj.getClass().getName() + ")");
		else
		System.out.print("\nINFO: " + msg + " (" + obj.getClass().getName() + ")");
	}
	public void Warning(Object obj, String msg) {
		if (Parameters.getLog().equals("T"))
			System.out.println("WARNING: " + msg + " (" + obj.getClass().getName() + ")");
	}
	
}
