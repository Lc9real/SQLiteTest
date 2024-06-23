import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class UserInput 
{

    private static Connection connection;

    private static User user;

    public static void main(String[] args) 
    {

        try
        {   //tes
            int postCount = 10;
            int lastPost = 0;
            int series = 0;
            connection = SMSystem.sqlSetup();
            Scanner reader = new Scanner(System.in); 
            boolean loggingIN = true;
            while(loggingIN)
            {
                System.out.println("Login");
                System.out.print("Email: ");
                String email = reader.nextLine().toLowerCase();
                System.out.print("Password: ");
                int password = reader.nextLine().hashCode();

                Integer userID = User.getUserIDFromEmail(email, connection);
                if (userID == null)
                {
                    System.out.println("\nRegister");
                    while (true) 
                    { 
                        System.out.print("Username: ");
                        String username = reader.nextLine();
                        if(User.getUserIDFromUsername(username, connection) == null)
                        {
                            System.out.print("Description: ");
                            String description = reader.nextLine();
                            User userToCreate = new User(username, email, description);
                            User.addUser(userToCreate, password, connection);
                            user = User.getUserInfo(User.getUserIDFromEmail(email, connection), connection);
                            System.out.println("Logged in as " + user.username);
                            loggingIN = false;
                            break;
                        }
                        else
                        {
                            System.out.println("This Username is already taken");
                        }
                    }
                    
                }
                else
                {
                    if(User.checkPassword(email, password, connection))
                    {
                        user = User.getUserInfo(userID, connection);
                        System.out.println("Logged in as " + user.username);
                        loggingIN = false;
                    }
                    else
                    {
                        System.out.println("Wrong Password");
                    }
                }
            }
            


            while (true) 
            {
                System.out.print("> ");
                String input = reader.nextLine().toLowerCase();

                if(input.equals("exit")) break;
                else if (input.length() >= 5 && input.substring(0, 5).equals("posts")) 
                {
                    SortBy sortBy = SortBy.RANDOM;

                    if(input.length() > 5)
                    {
                        switch (input.substring(6)) 
                        {
                            case "random":
                                sortBy = SortBy.RANDOM;
                                break;
                            case "newest":
                                sortBy = SortBy.CREATION;
                                break;
                            case "top":
                                sortBy = SortBy.VOTES;
                                break;
                            default:
                                break;
                        }
                    }
                    

                    
                    Integer t = series;
                    if(t==0) t=null;
                    List<Post> posts = SMSystem.getPosts(user.id, postCount, t, sortBy);

                    for(Post post : posts)
                    {
                        System.out.println("#"+post.id + ": " + post.title);
                    }

                    lastPost = 0;

                }
                else if (input.length() >= 7 && input.substring(0, 4).equals("post"))
                {   
                    if(input.substring(5, 6).equals("#"))
                    {
                        try
                        {
                            int postID = Integer.parseInt(input.substring(6));
                            Post post = Post.getPostInfo(postID, connection);
                            if (post.id != 0)
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                String string = "\n" + User.getUserInfo(post.userID, connection).username + " (" + sdf.format(post.createdAt) + ")>\n";
                                string += post.title + "\n\n";
                                string += post.content + "\n\n";
                                string += "Vote:    " + post.vote + "\n";
                                System.out.println(string);
                                lastPost = postID;
                            }
                            else
                            {
                                System.out.println("Post not found");
                            }
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Please give a valid Post Number");
                        }
                    }
                }
                else if (input.length() >= 8 && input.substring(0, 8).equals("comments"))
                {
                    if(lastPost == 0) 
                    { 
                        System.out.println("No Post selected");
                    }
                    else
                    {
                        List<Comment> comments = SMSystem.getComments(20, lastPost, SortBy.VOTES);
                        List<Comment.CommentLevel> commentLevels = SMSystem.getAllSubComments(comments, connection, 0);
                        SMSystem.printAllComments(commentLevels);
                    }
                    
                }
                else if (input.length() >= 9 && input.substring(0, 7).equals("comment"))
                {
                    if(lastPost == 0) 
                    { 
                        System.out.println("No Post selected");
                    }
                    else
                    {
                        if(input.substring(8, 9).equals("#"))
                        {
                            int spaceIndex = input.substring(9).indexOf(" ");
                            if(spaceIndex == -1)
                            {
                                System.out.println("Please Select a Valid Comment");

                            }
                            else
                            {
                                try
                                {
                                    int commentID = Integer.parseInt(input.substring(9, 9+spaceIndex));
                                    
                                    Comment comment = Comment.getCommentInfo(commentID, connection);
                                    if (comment != null && comment.postID == lastPost)
                                    {
                                        String commentInput = input.substring(9+spaceIndex);
                                        Comment commentToCreate = new Comment(user.id, lastPost, comment.id, commentInput);
                                        Comment.addComment(commentToCreate, connection);
                                    }
                                    else
                                    {
                                        System.out.println("Select a Valid Comment");
                                    }
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Please give a valid Post Number");
                                }
                            }
                        }
                        else
                        {
                            String commentInput = input.substring(7);
                            Comment comment = new Comment(user.id, lastPost, null, commentInput);
                            Comment.addComment(comment, connection);
                        }
                        
                    }
                    
                }
                else if (input.length() >= 6 && input.substring(0, 6).equals("series"))
                {
                    if(input.length() > 6)
                    {
                        String seriesToSearch = input.substring(7);

                        Integer seriesID = Series.getSeriesID(seriesToSearch, connection);

                        if(seriesID == null)
                        {
                            System.out.println(seriesToSearch + " was not found");
                        }
                        else
                        {
                            Series seriesInfo = Series.getSeriesInfo(seriesID, connection);
                            if (seriesInfo != null)
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                String string = "\n" + seriesInfo.name + " (" + sdf.format(seriesInfo.createdAt) + ")\n";
                                string += seriesInfo.description + "\n\n";
                                System.out.println(string);
                                series = seriesID;
                            }
                        }
                    }
                    else
                    {
                        series = 0;
                        System.out.println("Entered ForYou Page");
                    }
                } 
                else if (input.length() >= 10 && input.substring(0, 10).equals("setepisode"))
                {
                    
                    while (true) 
                    { 
                        try
                        {
                            System.out.print("Series: ");
                            String choosenSeries = reader.nextLine();
                            System.out.print("Episode: ");
                            int episode = Integer.parseInt(reader.nextLine());
                            System.out.print("Season: ");
                            int season = Integer.parseInt(reader.nextLine());

                            Integer seriesID = Series.getSeriesID(choosenSeries, connection);
                            if(seriesID!=null)
                            {
                                UserEpisode.addUserEpisode(new UserEpisode(user.id, seriesID, season, episode), connection);
                                break;
                            }
                            else
                            {
                                System.out.println("Please select a valid Series");
                            }
                            
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Please select a valid Episode and Season");
                        }   
                    }
                    
                    
                }
                
            }



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

}
