package cn.cug.sxy.cachex.guard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/5/18 16:32
 * @Description 逻辑过期封装类
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisData {

    private Object data;

    private LocalDateTime expireTime;

}
