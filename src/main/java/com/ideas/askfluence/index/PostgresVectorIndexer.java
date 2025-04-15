package com.ideas.askfluence.index;

import com.ideas.askfluence.config.Connections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Connections connections;

    public void indexToPostgresWithMetadata(Map<String, List<Float>> embeddings){
        Connection connection = null;
        try {
            connection = connections.getPostgresConnection();
            String insertQuery = "INSERT INTO confluence_vector (metadata, vectors) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            connection.setAutoCommit(false);
            for (Map.Entry<String, List<Float>> entry : embeddings.entrySet()) {
                preparedStatement.setString(1, entry.getKey());
                preparedStatement.setArray(2, connection.createArrayOf("float4", entry.getValue().toArray()));
                preparedStatement.addBatch();

            }
            preparedStatement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
