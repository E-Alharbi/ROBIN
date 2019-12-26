package PMBPP.Log;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Label;
import com.indvd00m.ascii.render.elements.PseudoText;
import com.indvd00m.ascii.render.elements.Rectangle;
import com.indvd00m.ascii.render.elements.Text;

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
		System.out.println(s);
	}
	
	public void Error(Object obj, String msg) {
		System.out.println("ERROR: "+msg+" ("+obj.getClass().getName()+")");
	}
	public void Info(Object obj, String msg) {
		System.out.println("INFO: "+msg+" ("+obj.getClass().getName()+")");
	}
	public void Warning(Object obj, String msg) {
		System.out.println("WARNING: "+msg+" ("+obj.getClass().getName()+")");
	}
}
