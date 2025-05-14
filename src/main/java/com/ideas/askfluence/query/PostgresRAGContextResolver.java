package com.ideas.askfluence.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresRAGContextResolver {
    public static final String COSINE_METRIC = "<=>";
    public static final String VECTOR_QUERY = " SELECT metadata FROM confluence_vector ORDER BY vectors "
            + COSINE_METRIC + " ?::vector(1024) LIMIT 2;";
    public static final String METADATA = "metadata";

    @Autowired
    private DataSource connections;

    public String resolve(List<Float> queryVector) {
        try (Connection conn = connections.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(VECTOR_QUERY)) {
            pstmt.setString(1, queryVector.toString());
            ResultSet rs = pstmt.executeQuery();
            List<String> context = new ArrayList<>();
            while (rs.next()) {
                log.debug("Retrieved metadata: " + rs.getString(METADATA));
                context.add(rs.getString(METADATA));
            }
            return String.join("\n", context);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
