package com.ideas.askfluence.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    public static final String ORDER_BY = " ORDER BY vectors ";
    public static final String SELECT = "SELECT metadata FROM confluence_vector";
    public static final String COSINE_METRIC_FILTER = COSINE_METRIC + " ?::vector(1024) LIMIT 2;";
    public static final String VECTOR_QUERY = SELECT + ORDER_BY
            + COSINE_METRIC_FILTER;

    public static final String VECTOR_QUERY_WITH_FILTER = SELECT
            + " WHERE root_id = ANY(?) "
            + ORDER_BY
            + COSINE_METRIC_FILTER;
    public static final String METADATA = "metadata";

    @Autowired
    private DataSource connections;

    public String resolve(List<Float> queryVector, List<Long> spaces) {
        boolean useSpaceFilter = !CollectionUtils.isEmpty(spaces);
        try (Connection conn = connections.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getQueryString(useSpaceFilter))) {
           if(useSpaceFilter) {
                pstmt.setArray(1, conn.createArrayOf("bigint", spaces.toArray(new Long[0])));
               pstmt.setString(2, queryVector.toString());
           }else{
               pstmt.setString(1, queryVector.toString());
           }
           log.debug("Executing query: " + pstmt.toString());
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

    private String getQueryString(boolean spaces) {
       return spaces?
               VECTOR_QUERY_WITH_FILTER:
                VECTOR_QUERY;

    }

}
