public class Subject implements Comparable<Subject>{
	public String name;
	public int point;
	
	public Subject(String name, int point){
		this.name = name;
		this.point = point;
	}
	
	@Override
	public int compareTo(Subject o) {
		return o.point - this.point;
	}
	
}
