package yaycrawler.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.repositories.SiteCookieRepository;

import java.io.File;
import java.util.List;

/**
 * Created by ucs_yuananyun on 2016/5/18.
 */
@RestController
public class ResourceController {

    @Autowired
    private SiteCookieRepository cookieRepository;

    @RequestMapping(value = "/addCookie", method = RequestMethod.POST)
    public Object addCookie(String siteId, String domain, String cookie) {
        Assert.notNull(siteId);
        Assert.notNull(domain);
        Assert.notNull(cookie);
        SiteCookie siteCookie = new SiteCookie(siteId,domain, cookie);
        siteCookie.setLastUpdatedDate(new java.sql.Date(System.currentTimeMillis()));
        return cookieRepository.save(siteCookie);
    }

    @RequestMapping(value = "/addLoginJs", method = RequestMethod.POST)
    public Object addLoginJs(String siteId, String domain, @RequestParam("files") MultipartFile[] files) {
        Assert.notNull(siteId);
        Assert.notNull(domain);
        Assert.notNull(files);

        return 1;
    }

    @RequestMapping(value = "/deleteCookieByIds", method = RequestMethod.POST)
    public Object deleteCookieByIds(@RequestBody List<String> deletedIds) {
        if (deletedIds == null || deletedIds.size() == 0) return false;
            for (String deletedId : deletedIds) {
                cookieRepository.delete(deletedId);
            }
        return true;
    }



}
