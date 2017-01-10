public class Cell {

	// PRIVATE DATA MEMBERS
	private String mymove;
	private char player;
	private int value;
	private int indexi;
	private int indexj;

	// CONSTRUCTOR
	public Cell(int val){

		this.value = val;
		this.mymove = "";
		this.player = '.';
	}
	public Cell(Cell cell){
		mymove = new String(cell.move());
		player = new Character(cell.player());
		value = new Integer(cell.value());
	}

	// SETTERS
	public void setMove(String move) {
		this.mymove = move;
	}
	public void setPlayer(char player) {
		this.player = player;
	}
	public void setValue(int val) {
		this.value = val;
	}
	public void setIndexi(int i){
		this.indexi = i;
	}
	public void setIndexj(int j){
		this.indexj = j;
	}

	// GETTERS
	public String move() {
		return mymove;
	}
	public char player() {
		return player;
	}
	public int value() {
		return value;
	}
	public int indexi() {
		return indexi;
	}
	public int indexj() {
		return indexj;
	}
}
