package PMBPP.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Label;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.indvd00m.ascii.render.elements.Rectangle;
import com.indvd00m.ascii.render.elements.Table;
import com.indvd00m.ascii.render.elements.Text;

import PMBPP.ML.Model.Parameters;

public class Log {

	public void PseudoText(String Txt) {
		IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(Txt.length()*10).height(20);
		builder.element(new PseudoText(Txt,false));
		ICanvas canvas = render.render(builder.build());
		String s = canvas.getText();
		
		System.out.println(s);
	}
	
	public void TxtInRectangle(String Txt) {
		IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(Txt.length()+5).height(5);
		builder.element(new Rectangle());
		builder.element(new Label(Txt,  1, 2, Txt.length()));
		ICanvas canvas = render.render(builder.build());
		String s = canvas.getText();
		if(Parameters.Log.equals("T"))
		System.out.println(s);
	}
	
	
public String CreateTable(List<String> headersList,List<List<String>> rowsList ) {
		
		int Totalwidth=0;
		for(String c : headersList) {
			if(c.length()>Totalwidth)
			Totalwidth=c.length();
		}
		
		HashMap<Integer,Vector<Integer>> ColAndWidth= new HashMap<Integer,Vector<Integer>>();
for(int h=0 ; h < headersList.size() ; ++h) {
	
	for(List<String> L : rowsList) {
		
		if(ColAndWidth.containsKey(h)) {
			Vector<Integer> temp = ColAndWidth.get(h);
			temp.add(L.get(h).length());
			ColAndWidth.put(h, temp);
		}
		else {
			Vector<Integer> temp = new Vector<Integer>();
			temp.add(L.get(h).length());
			ColAndWidth.put(h, temp);
		}
		
	}
	if(ColAndWidth.containsKey(h)) {
		Vector<Integer> temp = ColAndWidth.get(h);
		temp.add(headersList.get(h).length());
		ColAndWidth.put(h, temp);
	}
	else {
		Vector<Integer> temp = new Vector<Integer>();
		temp.add(headersList.get(h).length());
		ColAndWidth.put(h, temp);
	}
	
}
Totalwidth=0;
for(Integer h : ColAndWidth.keySet()) {
	Totalwidth+=Collections.max(ColAndWidth.get(h));
	
}
		IRender render = new Render();
		IContextBuilder builder = render.newBuilder();
		builder.width(Totalwidth+50).height(rowsList.size()*3); // increase the height if you got java.lang.IllegalArgumentException
		Table table = new Table(headersList.size(), rowsList.size()+1);// +1 for column headers
		//Adding columns
		int col=1;
		int row=1;
		for(String c : headersList) {
			
			table.setElement(col, row, new Label(c));
			++col;
		}
		col=1;
		row=2;
		
		for(List<String> L : rowsList) {
			
			for(String r : L) {
				
				table.setElement(col, row, new Label(r));
				col++;
			}
			row++;
			col=1;
		}
	
		builder.element(table);
		ICanvas canvas = render.render(builder.build());
		String s = canvas.getText();
		
		return s;
	
	}
	public void Error(Object obj, String msg) {
		if(Parameters.Log.equals("T"))
		System.out.println("ERROR: "+msg+" ("+obj.getClass().getName()+")");
		
		System.exit(-1); // even if log is F when need to stop the running 
	}
	public void Info(Object obj, String msg) {
		if(Parameters.Log.equals("T"))
		System.out.println("INFO: "+msg+" ("+obj.getClass().getName()+")");
	}
	public void Warning(Object obj, String msg) {
		if(Parameters.Log.equals("T"))
		System.out.println("WARNING: "+msg+" ("+obj.getClass().getName()+")");
	}
}
