package yaycrawler.admin;

import java.util.*;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by bill on 2017/3/31.
 */
public class GroupAnagrams  {

        public List<List<String>> groupAnagrams(String[] strs) {
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            for(String str : strs){
                // 将单词按字母排序
                char[] carr = str.toCharArray();
                Arrays.sort(carr);
                String key = new String(carr);
                List<String> list = map.get(key);
                if(list == null){
                    list = new ArrayList<String>();
                }
                list.add(str);
                map.put(key, list);
            }
            List<List<String>> res = new ArrayList<List<String>>();
            // 将列表按单词排序
            for(String key : map.keySet()){
                List<String> curr = map.get(key);
                Collections.sort(curr);
                res.add(curr);
            }
            return res;
        }

    @Test
    public void test() {
        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};

               assertThat(groupAnagrams(strs).size(), is(3));
    }
}
