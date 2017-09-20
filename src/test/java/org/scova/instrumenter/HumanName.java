package org.scova.instrumenter;

 public class HumanName {
	public String first , last ;
	public HumanName ( String firstName , String lastName ) {
		last= lastName ;
		first= firstName ;
	}
	public HumanName ( String oneNameCelebrity ) {
		last= oneNameCelebrity ;
		first= null ;
	}
	public int factorial ( ) {
		return 0 ;
	}
	public boolean IsCelebrity ( ) {
		return first == null ;
	}
	
	public String getLastName() {
		return this.last;
	}
	
	public void etst() {
		String a = "jnska";
		String b = a;
		this.last = b;
	}
}

