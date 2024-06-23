
import java.sql.*;



public class Post 
{
    public int id;
    public int seriesID;
    public int userID;
    public int episode;
    public int season;
    public String title;
    public String content;
    public int vote;
    public Date createdAt;

    public Post() 
    {
    }

    public Post(int id, int seriesID, int userID, int episode, int season, String title, String content, int vote, Date createdAt) 
    {
        this.id = id;
        this.seriesID = seriesID;
        this.userID = userID;
        this.episode = episode;
        this.season = season;
        this.title = title;
        this.content = content;
        this.vote = vote;
        this.createdAt = createdAt;
    }

    public Post(int seriesID, int userID, int episode, int season, String title, String content) 
    {
        this.seriesID = seriesID;
        this.userID = userID;
        this.episode = episode;
        this.season = season;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Post " +
                id +
                ":\nSeries ID: " + seriesID +
                "\nUser ID: " + userID +
                "\n Episode: " + episode +" Season: " + season +
                "\nTitle: " + title +
                "\nContent: " + content +
                "\nVote: " + vote +
                "\nCreated At: " + createdAt;
    }

    public static void addPost(Post post, Connection connection) throws SQLException
    {
        String sql = "INSERT INTO post (seriesID, user, episode, season, title, content) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, post.seriesID);
                pstmt.setInt(2, post.userID);
                pstmt.setInt(3, post.episode);
                pstmt.setInt(4, post.season);
                pstmt.setString(5, post.title);
                pstmt.setString(6, post.content);
                pstmt.executeUpdate();
            }
    }

    public static Post getPostInfo(int id, Connection connection) throws SQLException
    {
        Post post = new Post();
        String sql = "SELECT * FROM post WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                post.id = id;
                post.seriesID = rs.getInt("seriesID");
                post.userID = rs.getInt("user");
                post.episode = rs.getInt("episode");
                post.season = rs.getInt("season");
                post.title = rs.getString("title");
                post.content = rs.getString("content");
                post.vote = rs.getInt("vote");
                post.createdAt = rs.getDate("created_at");
            }
        }
        
        return post;
    }
}
