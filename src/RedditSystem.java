import java.sql.*;

public class RedditSystem {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            // Initialize database and create tables if they don't exist
            connection = DriverManager.getConnection("jdbc:sqlite:Database/reddit.db");
            createTables();

            // Example usage: Add a subreddit
            SubredditDAO subredditDAO = new SubredditDAO(connection);
            subredditDAO.addSubreddit("programming", "Discussions about programming languages and techniques.");
            subredditDAO.addSubreddit("coding", "Discussions about programming languages and techniques.");

            // Example usage: Add a post
            PostDAO postDAO = new PostDAO(connection);
            postDAO.addPost("programming", "Java Basics", "Java is a widely-used programming language.");

            // Example usage: Add a comment
            CommentDAO commentDAO = new CommentDAO(connection);
            commentDAO.addComment(1, "Great topic!");

            // Example usage: Add a subcomment
            SubcommentDAO subcommentDAO = new SubcommentDAO(connection);
            subcommentDAO.addSubcomment(1, "Thanks!");



            // Every time ran it creates a new subbredit




            String sql = "SELECT * FROM subreddit";
            try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                System.out.println(id+" | "+name+" | "+description);
            }
        }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createTables() throws SQLException {
        String createSubredditTable = "CREATE TABLE IF NOT EXISTS subreddit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT)";
        
        String createPostTable = "CREATE TABLE IF NOT EXISTS post (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "subreddit_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT," +
                "FOREIGN KEY (subreddit_id) REFERENCES subreddit(id))";
        
        String createCommentTable = "CREATE TABLE IF NOT EXISTS comment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "post_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "FOREIGN KEY (post_id) REFERENCES post(id))";
        
        String createSubcommentTable = "CREATE TABLE IF NOT EXISTS subcomment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "comment_id INTEGER NOT NULL," +
                "content TEXT NOT NULL," +
                "FOREIGN KEY (comment_id) REFERENCES comment(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createSubredditTable);
            stmt.execute(createPostTable);
            stmt.execute(createCommentTable);
            stmt.execute(createSubcommentTable);
        }
    }

    // DAO classes for Subreddit, Post, Comment, Subcomment operations
    static class SubredditDAO {
        private final Connection connection;

        public SubredditDAO(Connection connection) {
            this.connection = connection;
        }

        public void addSubreddit(String name, String description) throws SQLException {
            String sql = "INSERT INTO subreddit (name, description) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.executeUpdate();
            }
        }
    }

    static class PostDAO {
        private final Connection connection;

        public PostDAO(Connection connection) {
            this.connection = connection;
        }

        public void addPost(String subredditName, String title, String content) throws SQLException {
            // Get subreddit_id based on subredditName
            int subredditId = getSubredditId(subredditName);

            String sql = "INSERT INTO post (subreddit_id, title, content) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, subredditId);
                pstmt.setString(2, title);
                pstmt.setString(3, content);
                pstmt.executeUpdate();
            }
        }

        private int getSubredditId(String subredditName) throws SQLException {
            String sql = "SELECT id FROM subreddit WHERE name = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, subredditName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Subreddit not found: " + subredditName);
            }
        }
    }

    static class CommentDAO {
        private final Connection connection;

        public CommentDAO(Connection connection) {
            this.connection = connection;
        }

        public void addComment(int postId, String content) throws SQLException {
            String sql = "INSERT INTO comment (post_id, content) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, postId);
                pstmt.setString(2, content);
                pstmt.executeUpdate();
            }
        }
    }

    static class SubcommentDAO {
        private final Connection connection;

        public SubcommentDAO(Connection connection) {
            this.connection = connection;
        }

        public void addSubcomment(int commentId, String content) throws SQLException {
            String sql = "INSERT INTO subcomment (comment_id, content) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, commentId);
                pstmt.setString(2, content);
                pstmt.executeUpdate();
            }
        }
    }
    
}


