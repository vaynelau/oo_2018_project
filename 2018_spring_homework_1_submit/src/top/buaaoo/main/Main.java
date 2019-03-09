package top.buaaoo.main;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		
		try {
			System.out.println("#Please input the polynomial:");
			Scanner scan = new Scanner(System.in);
			String str = scan.nextLine();
	        scan.close();
	        
	        str = str.replaceAll(" +", ""); //去掉字符串里的空格
	        str = str.replaceAll(",-0\\)", ",0\\)"); //去掉系数0前面的负号

		    Poly polynomial = new Poly(str); //实例化一个多项式对象
		    if(polynomial.compute()==0)  //进行格式检查并计算
		       polynomial.printPoly(); //输出结果
		}
		catch (Throwable e) {
			System.out.println("ERROR");
			System.out.println("#A fatal exception has occurred.");
		}
	}
}
