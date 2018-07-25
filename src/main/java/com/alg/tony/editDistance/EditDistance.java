package com.alg.tony.editDistance;

public class EditDistance {
	
	public static int caculate(String wordA, String wordB) {
		// TODO Auto-generated method stub
		char[] wrongWord = wordA.toCharArray();
		char[] rightWord = wordB.toCharArray();
	
	
		final int m = wrongWord.length;
		final int n = rightWord.length;
	
	
		int[][] d = new int[m + 1][n + 1];
		for (int j = 0; j <= n; ++j) {
			d[0][j] = j;
		}
		for (int i = 0; i <= m; ++i) {
			d[i][0] = i;
		}
	
	
		for (int i = 1; i <= m; ++i) {
			char ci = wrongWord[i - 1];
			for (int j = 1; j <= n; ++j) {
				char cj = rightWord[j - 1];
				if (ci == cj) {
					d[i][j] = d[i - 1][j - 1];
				} else if (i > 1 && j > 1 && ci == rightWord[j - 2]
						&& cj == wrongWord[i - 2]) {
					// 交错相等
					d[i][j] = 1 + Math.min(d[i - 2][j - 2],
							Math.min(d[i][j - 1], d[i - 1][j]));
				} else {
					// 等号右边的分别代表 将ci改成cj 错串加cj 错串删ci
					d[i][j] = Math.min(d[i - 1][j - 1] + 1,
							Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
				}
			}
		}
		return d[m][n];
	}
}
