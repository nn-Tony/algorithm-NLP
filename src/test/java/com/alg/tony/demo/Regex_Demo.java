package com.alg.tony.demo;

import com.alg.tony.demo.RegexExpression;

public class Regex_Demo {
	public static void main(String[] args) {

		System.out.println("===========Url===========");
		String[] strUrl = { 
				"网址：www.eictech.com.cnEmail：albert.yang@eictech.com.cn",
				"详细任务:http://task.zhubajie.com/1700390/",
				"C:/Users/Administrator",
				"http://www.cqlxzj.com/zhqsh/轩辕传奇刺客属一.html",
				"http://www.zybang.com/question/626be4cf34ee213109e144f59d8f112b.html"
		};
		fun(strUrl, RegexExpression.RegexUrl);
		
		
		System.out.println("===========Email===========");
		String[] strEmail = { 
				"网址：www.eictech.com.cnEmail：我albert.yang@eictech.com.cn",
				"详细任务:http://task.zhubajie.com/1700390/",
				"求职招聘	1353	兼职	306	dfffffffffffffffffffffff"
		};
		fun(strEmail, RegexExpression.RegexEmail);
		
		
		System.out.println("===========Phone===========");
		String[] strPhone = { 
				"地18875208911方86 18875208911",
				"电话：+8618875208911",				
				"Tel:+86-755-82973805-828电话：025-86389356  网址+8613012345678",
				"试试区号0714-8490127and07148490127",
				"电 话：(021)58353198",
				"匹配(021)1234567and(0411)123456and(000)000000",
				"不匹配(123)1234567",
				"兼职	306	13661183384专业代办医疗器械经营企业许可证",
				"306	13661183384",
				"306 13661183384"
		};
		fun(strPhone, RegexExpression.RegexPhone);
		
		
		System.out.println("===========IP===========");
		String[] strIP = { 
				"匹配192.168.0.1/32this is an ip",
				"匹配192.168.0.1",
				"不匹配192.168.256.100",
		};
		fun(strIP, RegexExpression.RegexIP);
		
		
		System.out.println("===========Letter===========");
		String[] strLetter = { 
				"w我们中国dafde",
				"(0411)123456de(000)000000不匹this string will be deleted"
		};
		fun(strLetter, RegexExpression.RegexLetter);
		
		
		System.out.println("===========Chinese===========");
		String[] strChinese = { 
				"192.420281198910266590de",
				"(0411)123456de(000)000000不匹192.168.0.1"
		};
		fun(strChinese, RegexExpression.RegexChinese);
		
		
		System.out.println("===========IDcard===========");
		String[] strIDcard = { 
				"192.420281198910266590de",
				"(0411)123456de(000)000000不匹192.168.0.1"
		};
		fun(strIDcard, RegexExpression.RegexIDcard);
		
		
		System.out.println("===========Space===========");
		String[] strSpace = { 
				"the spaces will be deleted",
				"不	匹配"
		};
		fun(strSpace, RegexExpression.RegexSpace);
		
		
		System.out.println("===========HtmlChar===========");
		String[] strHtmlChar = {		
				"[color=#33ccff] [/color][b][color=#666666][size=3]品牌IC设计",
				"任务类型：[url=http://list.zhubajie.com/gongye/dianlu/]电路设计[/url]消毒机采用按键控制模式",
				"IC设计	[color=#000000][font=\\\'宋体\\\']需要设计[/font][/color][color=#000000][font=\\\'宋体\\\']是一款用WiFi控制的LED灯泡",
				"标    题：&quot;暖&quot;LOGO设计",
				"IC设计	color=#000000[font=\\\'宋体\\\']需要设计[/font][/color][color=#000000][font=\\\'宋体\\\']是一款用WiFi控制的LED灯泡"				
			};
		for(String s : strHtmlChar) {
			System.out.println(s + "--->" + RegexExpression.DeleteHtmlChar(s));
		}
		
	}
	
	public static void fun(String[] str, String regex) {
		for(String s : str) {
			System.out.println(s + "--->" + s.replaceAll(regex, ""));
		}
	}
}
