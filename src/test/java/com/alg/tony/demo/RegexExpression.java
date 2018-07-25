package com.alg.tony.demo;

public class RegexExpression {
	/**
	 * 网址
	 */
	public static final String RegexUrl = "((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?)|"
			+ "((((ht|f)tp(s?))\\://)?(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\.(com|cn|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\+&amp;%\\$#\\=~_\\-]+))*)";
	/**
	 * Email
	 */
	public static final String RegexEmail = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	/**
	 * 电话号码
	 */
	public static final String RegexPhone = "((?<national>\\+?(?:86)?-)(\\d{3}-\\d{8}|\\d{4}-\\d{7})(-\\d{3}))|"
			+ "(\\d{3}-?\\d{8}|\\d{4}-?\\d{7})|"
			+ "((\\([0]\\d{2}\\)){1}\\d{6,8})|"
			+ "((\\([0]\\d{3}\\)){1}\\d{6,8})|"
			+ "((\\+?(?:86 ?)?-?)(?<phone>(?<vender>(13|15|18)[0-9])(?<area>\\d{4})(?<id>\\d{4})))";
	/**
	 * IP地址
	 */
	public static final String RegexIP = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])(\\/3[0-2]|[1-2][0-9]|[1-9])?";
	/**
	 * 英文字符
	 */
	public static final String RegexLetter = "[a-zA-Z]*";
	/**
	 * 中文字符
	 */
	public static final String RegexChinese = "[\u4E00-\u9FA5]+";
	/**
	 * 身份证号码
	 */
	public static final String RegexIDcard = "(\\d{17}([0-9]|X))";
	/**
	 * 空格键
	 */
	public static final String RegexSpace = " {1,}";
	/**
	 * tab键
	 */
	public static final String RegexTab = "\t{1,}";
	/**
	 * 回车
	 */
	public static final String RegexCR = "\n{1,}";
	/**
	 * 去除HTML字符，如[color=#000000]、[font=\'宋体\']等</p>
	 * 不匹配color=#000000</p>
	 * @param str 输入字符串
	 * @return 清除后的字符串
	 */
	public static String DeleteHtmlChar(String str) {
		String[] regex = { 
				"\\[url.*?\\]",
				"\\[/url\\]",
				"\\[font.*?\\]",
				"\\[\\/font\\]",
				"\\[color.*?\\]",
				"\\[\\/color\\]",
				"\\[size.*?\\]",
				"\\[\\/size\\]",
				"\\[b.*?\\]",
				"\\[\\/b\\]",
				"\\[email.*?\\]",
				"\\[\\/email\\]",
				"\\[align.*?\\]",
				"\\[\\/align\\]",
				"&ldquo;",
				"&rdquo;",
				"&quot;",
				"&quot;",
				"\\[I.*?\\]",
				"\\[\\/I\\]",
				"\\[table.*?\\]",
				"\\[\\/table\\]",
				"\\[tr.*?\\]",
				"\\[\\/tr\\]",
				"\\[td.*?\\]",
				"\\[\\/td\\]",
				"\\[back.*?\\]",
				"\\[\\/back\\]"
		};
		for(String s : regex) {
			if(s.equals("&ldquo;")) {
				str = str.replaceAll(s, "“");
			} else if(s.equals("&rdquo;")) {
				str = str.replaceAll(s, "”");
			} else if(s.equals("&quot;")) {
				str = str.replaceAll(s, "\"");
			} else if(s.equals("&quot;")) {
				str = str.replaceAll(s, "\"");
			} else {
				str = str.replaceAll(s, "");
			}			
		}
		return str;
	}
}
