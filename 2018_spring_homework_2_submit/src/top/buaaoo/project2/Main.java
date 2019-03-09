package top.buaaoo.project2;

import java.util.Scanner;

public class Main {
	
	private static final String REGEX_FR="\\(FR,\\+?\\d{1,8},(UP|DOWN),\\+?\\d{1,18}\\)";
	private static final String REGEX_ER="\\(ER,\\+?\\d{1,8},\\+?\\d{1,18}\\)";
	private static int count=0;  // count为输入输入请求的个数

	
	public static void main(String[] args) {
		try {
			
			System.out.println("#Please input the request(s),end with \"RUN\":");
			Scanner scan = new Scanner(System.in);
			
			String str = scan.nextLine();
			str = str.replaceAll(" +", ""); //去掉字符串里的空格
			count++;
			
			while(!str.equals("RUN") && count<=150) {

				if(str.matches(REGEX_FR)) {
					String[] strSplit = str.split("[,\\)]");
					int floor = Integer.parseInt(strSplit[1]);
					long time = Long.parseLong(strSplit[3]);
					Floor.makeFloorRequest(floor,strSplit[2],time);
				}
				else if(str.matches(REGEX_ER)) {
					String[] strSplit = str.split("[,\\)]");
					int floor = Integer.parseInt(strSplit[1]);
					long time = Long.parseLong(strSplit[2]);
					Elevator.makeElevatorRequest(floor,time);
				}
				else {
					System.out.println("ERROR");
					System.out.println("#Incorrect input format.");
				}
					
				str = scan.nextLine();
				str = str.replaceAll(" +", ""); //去掉字符串里的空格
				count++;
			}
			scan.close();
			
			if(str.equals("RUN")) {
				Scheduler scheduler = new Scheduler();
				scheduler.work();
			}
			else {
				System.out.println("ERROR");
				System.out.println("#Too many input requests.");
			}
			//System.exit(0);
			
		}
		catch (Throwable ex) {
			System.out.println("ERROR");
			System.out.println("#A fatal exception has occurred.");
		}
	}
}
