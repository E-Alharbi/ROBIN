package table.draw;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class tester {

    
    public static void main(String[] args) throws IOException {
    	List<String> headersList = Arrays.asList("Tool Name", "File Name", "File Num", "Current Step", "Current Step Output");
        List<List<String>> rowsList = new ArrayList<List<String>>();
       
       // Board board = new Board(150);
       
        ArrayList<String> list = new ArrayList<String>();
		 list.add("1");
		 list.add("2");
		 list.add("3");
		 list.add("4");
		 list.add("4");
		 rowsList.add(list);
       
		 List<Integer> colWidthsListEdited = Arrays.asList(15, 15, 15, 35,35);
		 List<Integer> colAlignList = Arrays.asList(
				 Block.DATA_CENTER,
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER);
				
		 Board board = new Board(250);
		
		 Table table = new Table(board, 200, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	System.out.println(tableString);
		// return tableString;
    }
    
}

