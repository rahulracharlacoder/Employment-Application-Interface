package configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
