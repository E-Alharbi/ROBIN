package table.draw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogFile {

	
	public void Log(String ToolName , String FileName, String FileNum, String CurrentS, String CurrentStepOut,List<String> headersList) {
		
	//	List<String> headersList = Arrays.asList("Tool Name", "File Name", "File Num", "Current Step", "Current Step Output");
       
		List<List<String>> rowsList = new ArrayList<List<String>>();
       
       // Board board = new Board(150);
       
        ArrayList<String> list = new ArrayList<String>();
		 list.add(ToolName);
		 list.add(FileName);
		 list.add(FileNum);
		 list.add(CurrentS);
		 list.add(CurrentStepOut);
		
		 rowsList.add(list);
       
		 List<Integer> colWidthsListEdited = Arrays.asList(15, 25, 50, 35,35);
		 List<Integer> colAlignList = Arrays.asList(
				 Block.DATA_CENTER,
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER);
				
		 Board board = new Board(250);
		
		 Table table = new Table(board, 300, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	System.out.println(tableString);
	}
	public String NumberofModelsBuiltTable( ArrayList <ArrayList <String>> ListOfList) {
		List<String> headersList = Arrays.asList("Pipeline", "Total of Dataset", "Models Built", "Models Failed");
        List<List<String>> rowsList = new ArrayList<List<String>>();
       
       // Board board = new Board(150);
       
        
        for (int i=0 ; i < ListOfList.size() ; ++i) {
        	ArrayList<String> List = new ArrayList<String>();
        	for(int m=0 ; m < ListOfList.get(i).size() ; ++m) {
        		List.add(ListOfList.get(i).get(m));
       		 
        	}
        	rowsList.add(List);
        }
        
		 List<Integer> colWidthsListEdited = Arrays.asList(15, 15, 15, 35);
		 List<Integer> colAlignList = Arrays.asList(
				 Block.DATA_CENTER,
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER
				   );
				
		 Board board = new Board(250);
		
		 Table table = new Table(board, 200, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	System.out.println(tableString);
	return tableString;
		
	}
	public String NumberofModelsBuiltByResoTable(List<String> Headers, ArrayList <ArrayList <String>> ListOfList) {
		//List<String> headersList = Arrays.asList("Pipeline", "Total of Dataset", "Models Built", "Models Failed");
        List<List<String>> rowsList = new ArrayList<List<String>>();
        
       // Board board = new Board(150);
       
        
        for (int i=0 ; i < ListOfList.size() ; ++i) {
        	ArrayList<String> List = new ArrayList<String>();
        	for(int m=0 ; m < ListOfList.get(i).size() ; ++m) {
        		List.add(ListOfList.get(i).get(m));
       		 
        	}
        	rowsList.add(List);
        }
        
		 List<Integer> colWidthsListEdited = Arrays.asList(15, 30, 30, 35);
		 List<Integer> colAlignList = Arrays.asList(
				 Block.DATA_CENTER,
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER
				   );
				
		 Board board = new Board(250);
		
		 Table table = new Table(board, 200, Headers, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	System.out.println(tableString);
	return tableString;
		
	}
}
