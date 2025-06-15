package org.xzx.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xzx.entity.dto.MessageDTO;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Component
public class RedisUtil {

        @Resource
        private RedisTemplate<String, Object> redisTemplate;

        // 存储单条聊天记录
        public void saveChatMessage(MessageDTO messageDTO) {

            String chatKey = "userId:"+messageDTO.getSend_id();
            String chatKey2 = "userId:"+messageDTO.getHear_id();
            // 使用时间戳作为消息ID
            long timestamp = System.currentTimeMillis();
            // 存储消息到Redis Hash
            redisTemplate.opsForHash().put(chatKey, String.valueOf(timestamp), messageDTO);
            redisTemplate.opsForHash().put(chatKey2, String.valueOf(timestamp), messageDTO);
            // 设置过期时间30天
            redisTemplate.expire(chatKey, 30, TimeUnit.DAYS);
            redisTemplate.expire(chatKey2, 30, TimeUnit.DAYS);
        }

    public List<MessageDTO> getChatHistory(Long userId) {
        // 1. 生成固定的聊天Key
        String chatKey = "userId:"+userId;
        // 2. 获取这个聊天Key下的所有消息（自动按时间戳升序排列）
       List<Object> allMessages = redisTemplate.opsForHash().values(chatKey);
        // 3. 转换为List<TextDTO>并按时间戳排序（从旧到新）
        return allMessages.stream()
                .map(obj -> (MessageDTO) obj)
                .sorted(Comparator.comparing(MessageDTO::getSend_time)) // 按发送时间排序
                .collect(Collectors.toList());
    }

}
