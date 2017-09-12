package yaycrawler.ftpserver.service;

/**
 * Created by ucs_guoguibiao on 6/13 0013.
 */

import org.apache.ftpserver.ftplet.FtpletResult;

import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.utils.FtpClientUtils;

public class FtpService extends DefaultFtplet {

    private static final Logger logger  = LoggerFactory.getLogger(FtpService.class);

    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        String path = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();//获取当前路径
        String rootPath = session.getUser().getHomeDirectory();//获取根目录绝对路径
        String filename = request.getArgument();//获取文件名
        logger.info("path={},rootPath ={},filename={}",path,rootPath,filename);
        //得到相应的信息后，下面可以进行我们的逻辑处理啦.......
        return super.onUploadEnd(session, request);
    }

    @Override
    public FtpletResult onUploadStart(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        return super.onUploadStart(session, request);
    }
}

