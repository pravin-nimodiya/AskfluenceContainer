package com.ideas.askfluence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class Connections {

    @Value("${aws.s3.access-key:ASIARVGMFRYL4QTULKQD}")
    private String accessKey;
    @Value("${aws.s3.secret-key:3HUoqvaQiClRymC40gxrKMmQbsJ6hs0sJvPdES1I}")
    private String secretKey;
    @Value("${aws.use.session.token:true}")
    private Boolean useSessionToken;

    @Value("${aws.session.token:IQoJb3JpZ2luX2VjECwaCXVzLWVhc3QtMSJGMEQCIHA1RqqmMQYF5tYy0gKWG24UHPEdcst1IhAxr1bZ6vBuAiBHdClMOqcaqbwNuyUb5g0bP7R0TEHFqkQRWtRMHokB+CqaAwil//////////8BEAIaDDExNDI0NDgxNjQwNyIM/+vERc/ITP9C+i4bKu4CJsb4Et/W4e8DljmxgHc/I8kr8+QEIYhvGNsCGc3s6CaWmmV+fxW/BBT8T+Zx1aeBx8uWXBMEMtKgOZLGJ9z04FBNZ9J7ggf4F6+eJNVKjo9YcbOvhrz6y6QlLlX7excrCOCGzDHMkmOyv6YaC56evx9EXRvm/nfbHR1R2xlfnDwYeyh0YK0Gr5d5bnqCdmcsDsNzNVl2exdiAyp3skCxp9LusIL8UygP8h1N/HqJtfvQVwK4daVsdp7lk/R9hFNUndJLJhepe2cle0ou2pDcZ/gUigYwMyEdLYC4mFRapd7IKh54kOMkwc4v04k6ptHRK2L9c/x3apo1Rd49XCtWnJ6v1tV5oKEwhE4+n3XifN79NMDPbPq9z64BBC7PsBGaEIZkR1URUs1ihSk9w+q852WttLzS/0R4f/r7/Y7JYlGMM6IDEo1NQrVjSt8hg17qC6SI5xz4YgF72uk1oXogIXMnxNoYuOlhiPN+J7/OMILU3r8GOqcB6PKq9pq+yOaEsKL6DuReb81/Cu04/LzmcI09RWnQRGWZ8A+JC/QnZppqFOzgKHx/wbZKeNhgdqOrZAhdClHJ5iXtS5ogmvQX9kDBRm06v3rX4MVF1HQRRijiCg424gp8UhtAFk4T8rgRhPLLFqC2vs4iBv0AuTnaFXBllU4qF5Of+Bd/37vPL+scJNBOzy+KeTM8FpCj+OicDfWXjo87dvyDNBgsGlY=}")
    private String sessionToken;

    @Value("${postgres.url:jdbc:postgresql://localhost:5432/vector_db}")
    private String postgresUrl;

    @Value("${postgres.user:admin}")
    private String dbUser;

    @Value("${postgres.password:admin}")
    private String dbPassword;

    @Value("${aws.region:us-east-2}")
    private String awsRegion;
    @Bean
    public Connection getPostgresConnection() throws SQLException {
        return DriverManager.getConnection(postgresUrl, dbUser, dbPassword);
    }

    @Bean
    public  BedrockRuntimeClient getBedrockClient() {
        return BedrockRuntimeClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(getAwsCredentials())
                .build();
    }

    @Bean
    AwsCredentialsProvider getAwsCredentials() {
        if(useSessionToken) {
            return StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
        }
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey,secretKey));
    }

}
