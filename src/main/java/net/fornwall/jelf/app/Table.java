package net.fornwall.jelf.app;

import java.util.ArrayList;
import java.util.List;

public class Table {
	public enum Align {
		RIGHT,
		LEFT
	}
	
	private String title;
	private ArrayList<ArrayList<String>> table;
	private ArrayList<Align> align;
	private String seperator;
	
	/**
	 * Creates a new table with no title
	 */
	public Table() {
		this(null);
	}
	
	/**
	 * Creates a new table with the provided title
	 * 
	 * @param title table title
	 */
	public Table(String title) {
		this(title, " ");
	}
	
	/**
	 * Creates a new table with the provided title and a
	 * separator used to separate each column
	 * 
	 * @param title table title
	 * @param seperator seperator string
	 */
	public Table(String title, String seperator) {
		this.title = title;
		this.table = new ArrayList<ArrayList<String>>();
		this.align = new ArrayList<Align>();
		this.seperator = seperator;
	}
	
	/**
	 * Adds a new cell onto the end of the current row
	 * 
	 * @param cell the contents of the new cell
	 */
	public void addCell(String cell) {
		table.get(table.size() - 1).add(cell);
	}
	
	/**
	 * Creates a new row in the table
	 */
	public void newRow() {
		table.add(new ArrayList<String>());
	}
	
	/**
	 * Sets the alignment of the column of the most recently added cell.
	 * The alignment cannot be set more than once per column.
	 * 
	 * @param align the alignment of the column
	 */
	public void setAlign(Align align) {
		this.align.add(align);
	}
	
	/**
	 * Prints the table to standard output
	 */
	public void printTable() {
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		String table = "";
		
		// Get the maximum length string in each column
		ArrayList<Integer> colSizes = new ArrayList<Integer>();
		for(List<String> row : this.table) {
			for(int i = 0; i < row.size(); i++) {
				if(i == colSizes.size())
					colSizes.add(row.get(i).length());
				else if(colSizes.get(i) < row.get(i).length())
					colSizes.set(i, row.get(i).length());
			}
		}
		
		if(title != null)
			table += title + "\n";
		
		// Print each row
		for(List<String> row : this.table) {
			// Print each cell
			for(int i = 0; i < row.size(); i++) {
				String result = row.get(i);
				
				// Set proper alignment strings
				String rightAlign = "";
				String leftAlign = "";
				switch(align.get(i)) {
					case RIGHT:
						rightAlign = " ";
						break;
					case LEFT:
						leftAlign = " ";
						break;
				}
				
				// Pad the cell so it is the same length as the longest cell
				// in the column
				while(result.length() != colSizes.get(i))
					result = rightAlign + result + leftAlign;
				table += seperator + result;
			}
			table += "\n";
		}
		
		return table;
	}
}
