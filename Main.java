import java.util.Scanner;


public class Main {

	public static void main(String[] args) {
		OrderMatcher om = new OrderMatcher();
		Scanner s = new Scanner(System.in);
		while(s.hasNextLine()) {
			om.process_line((s.nextLine()).split("[ @]"));
		}
		s.close();
	}

}
