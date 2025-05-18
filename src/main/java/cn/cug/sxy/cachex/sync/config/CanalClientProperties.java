package cn.cug.sxy.cachex.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @version 1.0
 * @Date 2025/5/16 22:23
 * @Description canal客户端配置
 * @Author jerryhotton
 */


@Data
@ConfigurationProperties(prefix = "cachex.canal.client")
public class CanalClientProperties {

    /**
     * Canal Server 地址
     */
    private String host;
    /**
     * Canal Server 端口
     */
    private int port = 11111;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * instance name / destination 名称
     */
    private String destination = "example";
    /**
     * 每次拉取的最大消息数
     */
    private int batchSize = 100;
    /**
     * 空轮询休眠时间（毫秒）
     */
    private int emptySleepMs = 100;

    private String subscribe;

}
