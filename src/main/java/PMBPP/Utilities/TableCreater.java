package PMBPP.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import table.draw.Block;
import table.draw.Board;
import table.draw.Table;


public class TableCreater {

	
	public String CreateTable(List<String> headersList,List<List<String>> rowsList ) {
		
		
		 List<Integer> colWidthsListEdited = new  ArrayList<Integer>();
		 List<Integer> colAlignList = new  ArrayList<Integer>();
		 
		 
		int TotalTableWidth=0;
		for(int i=0 ; i < headersList.size();++i) {
			int max=headersList.get(i).length();
			 // Find optimal width
			for(int r= 0 ;  r < rowsList.size() ; ++r) {
				if(max<rowsList.get(r).get(i).length()) {
					max=rowsList.get(r).get(i).length();
					
				}
				if(TotalTableWidth<rowsList.get(r).get(i).length())
					TotalTableWidth=rowsList.get(r).get(i).length();
			}
			
			colWidthsListEdited.add(max+5);
			colAlignList.add(Block.DATA_CENTER);
		}
		 
				
		 Board board = new Board(TotalTableWidth*headersList.size()+500);
		
		 Table table = new Table(board, TotalTableWidth*headersList.size()+500, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
		 return  board.getPreview();
	}
}
