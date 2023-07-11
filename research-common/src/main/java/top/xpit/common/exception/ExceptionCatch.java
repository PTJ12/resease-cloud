package top.xpit.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xpit.common.base.BaseErrorNotice;
import top.xpit.module.common.dtos.ResponseResult;
import top.xpit.module.common.enums.AppHttpCodeEnum;

@ControllerAdvice
@Slf4j
public class ExceptionCatch {

    @Autowired
    private BaseErrorNotice baseErrorNotice;

    /**
     * 处理不可控异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e){
        e.printStackTrace();
        log.error("catch exception:{}",e.getMessage());
        String errorMsg = e.getMessage();
        baseErrorNotice.sendMsgText(errorMsg);
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult exception(CustomException e){
        log.error("catch exception:{}",e);
        baseErrorNotice.sendMsgText(e.getAppHttpCodeEnum().getErrorMessage());
        return ResponseResult.errorResult(e.getAppHttpCodeEnum());
    }
}
