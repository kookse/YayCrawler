package yaycrawler.admin;

/**
 * @author bill
 * @create 2017-09-14 17:59
 * @desc
 **/
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import yaycrawler.admin.mapper.UsereMapper;
import yaycrawler.dao.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class LiferayTest {

    @Autowired
    private UsereMapper usereMapper ;

    @org.junit.Test
    public void search(){
        List<User> userList = usereMapper.findAllUser();
        userList.forEach(user -> {
            System.out.println(user.getUid() + "-----" + user.getEmail());
        });
        System.out.println(userList.size()+"---------userList size-----------");
    }
}