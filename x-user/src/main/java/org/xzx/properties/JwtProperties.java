package org.xzx.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtProperties {
    @Value("${xchat.jwt.SecretKey}")
    private String SecretKey;
    @Value("${xchat.jwt.LoseTime}")
    private long   LoseTime;
    @Value("${xchat.jwt.TokenName}")
    private String TokenName;
}
