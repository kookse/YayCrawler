package yaycrawler.api.engine.ocr;

import org.springframework.stereotype.Service;
import yaycrawler.api.engine.Engine;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.utils.OCRHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-29 11:35
 * @desc 验证码识别引擎
 **/

@Service("ocrEngine")
public class OCREngine implements Engine<BinaryDto> {

    @Override
    public EngineResult execute(BinaryDto info) {
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<BinaryDto> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(BinaryDto info) {
        EngineResult engineResult = new EngineResult();
        String recognizeText = "";
        String img = info.getImg();
        String[] paramArray = img.split("\\$\\$");
        Pattern pattern = Pattern.compile(paramArray[1]);
        try {
            File file = new File(info.getDest() + "/" + paramArray[0]);
            if (!file.exists()) {
                file = new File(info.getSrc() + "/" + paramArray[0]);
            }
            recognizeText = new OCRHelper(info.getLanguage()).recognizeText(file).trim().toLowerCase();
            engineResult.setCode(recognizeText);
            engineResult.setResult(recognizeText);
            Matcher parmMatcher = pattern.matcher(recognizeText);
            if(parmMatcher.find()) {
                engineResult.setStatus(Boolean.TRUE);
            } else {
                engineResult.setStatus(Boolean.FALSE);
            }
        } catch (Exception e) {
            engineResult = failureCallback(info, e);
            e.printStackTrace();
        }

        return engineResult;
    }
}
