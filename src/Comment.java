import java.sql.*;

public class Comment {

    public static class CommentLevel {
        public Comment comment;
        public int level;

        public CommentLevel(Comment comment, int level) 
        {
            this.comment = comment;
            this.level = level;
        }
        
    }

    public int id;
    public int postID;
    public Integer parentID;
    public int userID;
    public String content;
    public Integer vote;
    public Date createdAt;

    public Comment() 
    {

    }

    public Comment(int id, int postID, Integer parentID, int userID, String content, Integer vote, Date createdAt) 
    {
        this.id = id;
        this.postID = postID;
        this.parentID = parentID;
        this.userID = userID;
        this.content = content;
        this.vote = vote;
        this.createdAt = createdAt;
    }

    public Comment(int userID, int postID, Integer parentID, String content) 
    {
        this.postID = postID;
        this.parentID = parentID;
        this.userID = userID;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment " +
                id +
                ":\nPost ID: " + postID +
                "\nParent ID: " + (parentID != null ? parentID : "None") +
                "\nUser ID: " + userID +
                "\nContent: " + content +
                "\nVote: " + (vote != null ? vote : "0") +
                "\nCreated At: " + createdAt;
    }

    public static int addComment(Comment comment, Connection connection) throws SQLException 
    {
        String sql = "INSERT INTO comment (postID, parentID, user, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, comment.postID);

            if (comment.parentID != null) {
                pstmt.setInt(2, comment.parentID);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setInt(3, comment.userID);
            pstmt.setString(4, comment.content);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        }
    }

    public static Comment getCommentInfo(int id, Connection connection) throws SQLException
    {
        Comment comment = new Comment();
        String sql = "SELECT * FROM comment WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                comment.id = id;
                comment.postID = rs.getInt("postID");
                comment.parentID = rs.getInt("parentID");
                comment.userID = rs.getInt("user");
                comment.content = rs.getString("content");
                comment.vote = rs.getInt("vote");
                comment.createdAt = rs.getDate("created_at");

                
            }
        }
        
        return comment;
    }

}
