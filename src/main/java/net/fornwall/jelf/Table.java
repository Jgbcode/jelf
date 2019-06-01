package net.fornwall.jelf;

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
	
	public Table(String title) {
		this.title = title;
		this.table = new ArrayList<ArrayList<String>>();
	}
	
	public void addCell(String cell) {
		table.get(table.size() - 1).add(cell);
	}
	
	public void newRow() {
		if(align.size() != table.size())
			align.add(Align.RIGHT);
		table.add(new ArrayList<String>());
	}
	
	public void setAlign(Align align) {
		this.align.add(align);
	}
	
	public void printTable() {
		ArrayList<Integer> colSizes = new ArrayList<Integer>();
		
		for(List<String> row : table) {
			for(int i = 0; i < row.size(); i++) {
				if(i == colSizes.size())
					colSizes.add(row.get(i).length());
				else if(colSizes.get(i) < row.get(i).length())
					colSizes.set(i, row.get(i).length());
			}
		}
		
		System.out.println(title);
		for(List<String> row : table) {
			for(int i = 0; i < row.size(); i++) {
				String result = row.get(i);
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
					
				while(result.length() != colSizes.get(i))
					result = rightAlign + result + leftAlign;
				System.out.print(result + seperator);
			}
			System.out.println();
		}
	}
}
