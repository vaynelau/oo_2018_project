package top.buaaoo.main;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poly {
	
	static boolean[] visit = new boolean[1000010]; //用来判断同一多项式内是否有同类项
	static int[] result = new int[1000010];  //保存计算结果
	static String poly; //保存输入的多项式字符串
	static int sign=0;  //保存多项式前的符号
	
	static final String REGEX = "[\\+-]?\\{(\\([\\+-]?[0-9]{1,6},[\\+]?[0-9]{1,6}\\),){0,49}"
     		+ "\\([\\+-]?[0-9]{1,6},[\\+]?[0-9]{1,6}\\)";   //用于匹配第一个多项式的正则表达式
	static final String REGEX2 = "[\\+-]\\{(\\([\\+-]?[0-9]{1,6},[\\+]?[0-9]{1,6}\\),){0,49}"
     		+ "\\([\\+-]?[0-9]{1,6},[\\+]?[0-9]{1,6}\\)";  //用于匹配第二个以后多项式的正则表达式
	static Pattern pattern;
	static Matcher matcher;
	
	
	public Poly(String str){ //构造方法
		poly = str;
		Arrays.fill(result,0);
	}
	
	public int compute() {
		
		char[] polyArray = poly.toCharArray();
		
		for(int j=1;j<polyArray.length;j++) {
			if(polyArray[j-1]=='}' && polyArray[j]=='}') {
				System.out.println("ERROR");
				System.out.println("#Incorrect input format.");
				return 1;
			}
		}
		
		String[] strArray = poly.split("\\}");
		
		if(strArray.length>20) {
			System.out.println("ERROR");
			System.out.println("#Incorrect input format.");
			return 1;
		}
			
		for (int i=0; i< strArray.length; i++){
			
			Arrays.fill(visit,false);
			
			if(i==0) { 
				pattern = Pattern.compile(REGEX);
			}
			else {
				pattern = Pattern.compile(REGEX2);
			}
			matcher = pattern.matcher(strArray[i]);
	        if(matcher.matches()) { //利用正则表达式检查输入格式是否正确
	        	
	        	if(strArray[i].charAt(0) == '-') 
	    			sign=1;
	    		else 
	    			sign=0;

	    		for (String str_j: strArray[i].split("\\)")){
	    			if(stringToNumber(str_j) == 1) {
	    				System.out.println("ERROR");
	    				System.out.println("#Similar items in the same polynomial.");
	    				return 1;
	    			}	
	    		}
	        }
	        else {
	        	System.out.println("ERROR");
				System.out.println("#Incorrect input format.");
				return 1;
	        }
		}
		return 0;
	}
	
	
	public int stringToNumber(String str) {

		char[] charArray = str.toCharArray();
		int i=0;
		int num=0,flag=0;//系数及其符号位
		while(!(charArray[i]>='0' && charArray[i]<='9' ))
			i++;
		if(charArray[i-1]== '-')
			flag = 1;
		while(charArray[i] != ',') {
			num = num*10 + charArray[i]-'0';
			i++;
		}
		if(flag == 1) 
			num = -num;
		if(sign == 1)
			num = -num;
		
		int num2 = 0; //指数
		while(!(charArray[i]>='0' && charArray[i]<='9' ))
			i++;
		
		while(i < str.length()) {
			num2 = num2*10 + charArray[i]-'0';
			i++;
		}
		if(visit[num2] == false) {
			visit[num2] = true;
			result[num2] += num;
		}
		else return 1;


		return 0;
	}
	
	
	public void printPoly() {
		int flag = 0;
		for(int i: result) {
			if(i != 0)
				flag =1;
		}
		if(flag == 0)
			System.out.println(0);
		else {
			
			int flag2 = 0;
			System.out.print("{");
			for(int i=0;i< result.length; i++) {
				if(result[i] != 0)
					if(flag2 == 0) {
						System.out.print("("+result[i]+","+i+")");
						flag2 = 1;
					}
					else {
						System.out.print(",("+result[i]+","+i+")");
					}	
			}
			System.out.println("}");
		}
	}
}
