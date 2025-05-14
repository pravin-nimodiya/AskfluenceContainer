package com.ideas.askfluence.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresVectorIndexer {
    @Autowired
    private DataSource dataSource;

    public void indexToPostgresWithMetadata(String rootId, Map<String, List<Float>> embeddings){
        try {
            deleteIfRootDataAlreadyExists(rootId);
            Connection   connection = dataSource.getConnection();
            String insertQuery = "INSERT INTO confluence_vector (root_id,metadata, vectors) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            connection.setAutoCommit(false);
            for (Map.Entry<String, List<Float>> entry : embeddings.entrySet()) {
                preparedStatement.setLong(1, Long.parseLong(rootId));
                preparedStatement.setString(2, entry.getKey());
                preparedStatement.setArray(3, connection.createArrayOf("float4", entry.getValue().toArray()));
                preparedStatement.addBatch();

            }
            preparedStatement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteIfRootDataAlreadyExists(String rootId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("DELETE FROM confluence_vector WHERE root_id = ?")) {
            pstmt.setLong(1, Long.parseLong(rootId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
