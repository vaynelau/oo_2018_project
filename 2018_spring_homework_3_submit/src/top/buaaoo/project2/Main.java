package top.buaaoo.project2;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	private static final String REGEX_FR="\\(FR,\\+?\\d{1,8},(UP|DOWN),\\+?\\d{1,18}\\)";
	private static final String REGEX_ER="\\(ER,\\+?\\d{1,8},\\+?\\d{1,18}\\)";
	private static Pattern pFR = Pattern.compile(REGEX_FR);
	private static Pattern pER = Pattern.compile(REGEX_ER);
	
	public static void main(String[] args) {
		
		try {
			RequestQueue reqQueue = new RequestQueue();
			
			Scanner scan = new Scanner(System.in);
			for(int i=0; i<100; i++) {
				String str = scan.nextLine();
				str = str.replaceAll(" +", ""); //去掉字符串里的空格
				if(str.equals("RUN")) {
					break;
				}
				Matcher mFR = pFR.matcher(str);
				Matcher mER = pER.matcher(str);
				if(mFR.matches()) {  //利用正则表达式进行初步的格式匹配
					Floor.makeFloorRequest(str,reqQueue);
				}
				else if(mER.matches()) {  //利用正则表达式进行初步的格式匹配
					Elevator.makeElevatorRequest(str,reqQueue);
				}
				else {
					System.out.println("INVALID["+str+"]");
				}
			}
			scan.close();
			
//			Scheduler scheduler = new Scheduler(reqQueue);
			ALS_Scheduler scheduler = new ALS_Scheduler(reqQueue);
			scheduler.work();
		}
		catch (Throwable ex) {
			return;
		}
	}
}
