package yaycrawler.admin;

import static java.lang.Math.min;

/**
 * Created by bill on 2017/3/31.
 */
public class Solution {
    private void SwapFirstK(int stt, int lst, char[] str) {
        while(stt < lst) {
            char tmp = str[stt];
            str[stt] = str[lst];
            str[lst] = tmp;
            stt++; lst--;
        }
    }

    public String reverseStr(String s, int k) {
        int sz = s.length();
        char[] tmp = s.toCharArray();
        for(int i = 0; i < sz; i += k) {
            if(i%(2*k) == 0) {
                SwapFirstK(i, min(i+k-1, sz - 1), tmp);
            }
        }
        return new String(tmp);
    }

    public static void main(String[] args) {

        Solution solution = new Solution();
        String result = solution.reverseStr("abcdefg",2);
        System.out.println(result);
    }
}