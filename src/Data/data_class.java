package Data;

public class data_class {
	int index = -1;
	String name = "";
	int size = 0;
//	public FlowPane layout = new FlowPane();
	
	public data_class(int index, String name, int size) {
		this.index = index;
		this.name = name;
		this.size = size;
	}
	
	//Set-Methoden ..............................................
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	
	//Get-Methoden ...............................................
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return ""+index+" "+name;
	}

	public void incrementSize() {
		size++;
	}
	
	public void decrementSize() {
		size--;
	}

//	public void addButton(ImageButton button) {
//		layout.getChildren().add(button);
//	}
//
//	public int getLayoutSize() {
//		return layout.getChildren().size();
//	}
	
	
}
