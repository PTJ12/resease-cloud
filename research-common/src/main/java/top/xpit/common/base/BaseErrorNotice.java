package top.xpit.common.base;

import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author PTJ
 * @date 2023/7/11 21:54
 */
@Slf4j
@Component
public class BaseErrorNotice implements EnvironmentAware {
    private String applicationName;
    private String profileActive;
    private String ip;
    private static ConcurrentLinkedQueue<String> errorQueue = new ConcurrentLinkedQueue<>();

    private static final String url = "";

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true){
                if (!errorQueue.isEmpty()){
                    String peek = errorQueue.poll();
                    sendMsgText(peek);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void sendMsgText(String msg) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("业务：").append(applicationName).append(" 环境：").append(profileActive);
        buffer.append(" ip：").append(ip);
        buffer.append(msg);
        JSONObject senObj = new JSONObject();
        senObj.put("msgText", "text");
        JSONObject content = new JSONObject();
        content.put("content", buffer.toString());
        senObj.put("text", content);
        if (!"dev".equals(profileActive)){
            String post = HttpUtil.post(url, senObj.toJSONString());
            log.info("发送状态:{}", post);
        }
    }

    public static void sendText(String msg){
        if (!errorQueue.contains(msg)){
            if (Objects.isNull(msg)){
                errorQueue.offer("系统消息为null");
            }
        }
        errorQueue.offer(msg);
    }

    @Override
    public void setEnvironment(Environment environment) {
        applicationName = environment.getProperty("spring.application.name");
        profileActive = environment.getProperty("spring.profiles.active");
        ip = NetUtil.getLocalhost().getHostAddress();
    }
}
