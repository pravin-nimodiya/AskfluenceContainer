package com.ideas.askfluence.index;

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
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresVectorIndexer {
    @Autowired
    private DataSource dataSource;

    public void indexToPostgresWithMetadata(SpaceDetails spaceDetails, Map<String, List<Float>> embeddings){
            deleteIfRootDataAlreadyExists(String.valueOf(spaceDetails.getSpaceId()));
            addSpaceDetails(spaceDetails);
            insertVectorData(spaceDetails, embeddings);
    }

    private void insertVectorData(SpaceDetails spaceDetails, Map<String, List<Float>> embeddings) {
        try {
            Connection   connection = dataSource.getConnection();
            String insertQuery = "INSERT INTO confluence_vector (root_id,metadata, vectors) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            connection.setAutoCommit(false);
            for (Map.Entry<String, List<Float>> entry : embeddings.entrySet()) {
                preparedStatement.setLong(1, spaceDetails.getSpaceId());
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
        String deleteVectorSql = "DELETE FROM confluence_vector WHERE root_id = ?";
        String deleteSpaceSql = "DELETE FROM confluence_space WHERE space_id = ?";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction
            try (
                    PreparedStatement vectorStmt = connection.prepareStatement(deleteVectorSql);
                    PreparedStatement spaceStmt = connection.prepareStatement(deleteSpaceSql)
            ) {
                long id = Long.parseLong(rootId);

                vectorStmt.setLong(1, id);
                vectorStmt.executeUpdate();

                spaceStmt.setLong(1, id);
                spaceStmt.executeUpdate();

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback on error
                throw new RuntimeException("Failed to delete root data", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }


    private void addSpaceDetails(SpaceDetails spaceDetails) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("INSERT INTO confluence_space (space_id,space_key,space_name) VALUES (?, ?, ?)")) {
            pstmt.setLong(1, spaceDetails.getSpaceId());
            pstmt.setString(2, spaceDetails.getSpaceKey());
            pstmt.setString(3, spaceDetails.getSpaceName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<SpaceDetails> getAllSpaces() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM confluence_space")) {
            ResultSet rs = pstmt.executeQuery();
            List<SpaceDetails> spaces = new ArrayList<>();
            while (rs.next()) {
                SpaceDetails spaceDetails = new SpaceDetails();
                spaceDetails.setSpaceId(rs.getLong("space_id"));
                spaceDetails.setSpaceKey(rs.getString("space_key"));
                spaceDetails.setSpaceName(rs.getString("space_name"));
                spaces.add(spaceDetails);
            }
            return spaces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
