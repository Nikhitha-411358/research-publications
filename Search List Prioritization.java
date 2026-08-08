package SPL;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
public class spl 
{
    private static final Map<String,String[]> data = new HashMap<>();
    private static final Map<String,List<String>> commentsData = new HashMap<>();
    private static final Map<String,Integer[]> iconCounts = new HashMap<>();
    private static final Map<String,int[]> ratingsData = new HashMap<>();
    private static final Map<String,String> userVotes = new HashMap<>(); // Track user's last vote

    static 
    {
        // Define the data (keywords and their corresponding URLs)
        data.put("essay on global warming,global warming essay", new String[]{"https://www.esaral.com/english/essay-on-global-warming/","https://byjus.com/biology/essay-on-global-warming/","https://www.toppr.com/guides/essays/essay-on-global-warming/","https://www.vedantu.com/english/global-warming-essay"});
        data.put("loose weight in 30 days,loose weight in 1 month,loose weight 30 days,loose weight 1 month", new String[]{"https://www.wikihow.com/Lose-Weight-in-One-Month","https://www.forbes.com/health/weight-loss/how-to-lose-10-pounds-in-month/","https://timesofindia.indiatimes.com/life-style/health-fitness/weight-loss/lose-5-kgs-weight-in-a-month-with-these-simple-tips/photostory/107234649.cms","https://www.eatingwell.com/article/290902/how-much-weight-can-you-really-lose-in-a-month/" });
        data.put("good c-programming books,good c books,good c program books", new String[]{"https://www.mygreatlearning.com/blog/c-programming-books/","https://www.quora.com/What-is-the-best-book-on-C-programming","https://news.ycombinator.com/item?id=33130533","https://www.reddit.com/r/C_Programming/comments/10a4lz8/which_book_is_more_worth_it_to_learn_c/"});
        data.put("interior design for bedroom,bedroom interior design", new String[]{"https://in.pinterest.com/spaceoptimized/small-bedroom-design/","https://www.marthastewart.com/small-bedroom-ideas-7682155","https://www.housebeautiful.com/room-decorating/bedrooms/g2231/small-bedroom-design-tips/","https://www.architecturaldigest.com/gallery/best-small-bedroom-ideas"});
        // Initialize commentsData with both inbuilt and user comments
        for (String[] urls:data.values()) 
        {
            for (String url:urls) 
            {
                List<String> comments = new ArrayList<>();
                comments.add("I really loved this website");
                comments.add("This website produces good results");
                comments.add("I would love to suggest this website to people");
                comments.add("                                              ");
                commentsData.put(url,comments);
                iconCounts.put(url,new Integer[]{0,0}); // Initialize thumbs up and down counts
                // Initialize ratings with some example values
                int[] rating=new int[]{3,5,7,9,12}; // Example values for 1-star to 5-star ratings
                ratingsData.put(url,rating);
                userVotes.put(url,"none"); // Initialize user's last vote
            }
        }
    }
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(spl::createAndShowGUI);
    }
    private static void createAndShowGUI() 
    {
        // Create the frame
        JFrame frame=new JFrame("Search Bar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,400);
        frame.setLayout(new BorderLayout());
        // Create the search text field
        JTextField searchbar=new JTextField(20);
        // Create the search button with an icon
        JButton searchButton=new JButton();
        searchButton.setPreferredSize(new Dimension(30,30)); // Adjust the size of the button
        ImageIcon searchIcon=loadIcon("C:\\Users\\anany\\OneDrive - Amrita vishwa vidyapeetham\\AMRITA\\SEMESTER-2\\PROJECTS\\JAVA_DSA\\searchicon.jpeg",20,20);
        if(searchIcon!=null) 
            searchButton.setIcon(searchIcon);
        else 
            searchButton.setText("Search");
        // Create a panel to hold the search components
        JPanel searchpanel=new JPanel();
        searchpanel.setLayout(new FlowLayout());
        searchpanel.add(searchbar);
        searchpanel.add(searchButton);
        // Create a panel to display the search results
        JPanel resultPanel=new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));
        // Add the search panel and result panel to the frame
        frame.add(searchpanel,BorderLayout.NORTH);
        frame.add(new JScrollPane(resultPanel),BorderLayout.CENTER);
        // Add action listener to the search button
        ActionListener searchActionListener=e->
        {
            String searchText=searchbar.getText().trim().toLowerCase();
            resultPanel.removeAll();
            for (Map.Entry<String,String[]>entry:data.entrySet()) 
            {
                String keyword = entry.getKey().toLowerCase();
                if (keyword.contains(searchText)) 
                    displayResults(entry.getValue(),resultPanel);
            }
            resultPanel.revalidate();
            resultPanel.repaint();
        };
        searchButton.addActionListener(searchActionListener);
        searchbar.addActionListener(searchActionListener);
        // Make the frame visible
        frame.setVisible(true);
    }
    private static void displayResults(String[] urls, JPanel resultPanel) {
        PriorityQueue<UrlRating> priorityQueue=new PriorityQueue<>(Comparator.reverseOrder());
        for (String url:urls) 
        {
            double average=AverageRating(url);
            priorityQueue.add(new UrlRating(url,average));
        }
        while (!priorityQueue.isEmpty()) 
        {
            UrlRating urlRating=priorityQueue.poll();
            String url=urlRating.url;
            JPanel linkPanel=new JPanel();
            linkPanel.setLayout(new BoxLayout(linkPanel,BoxLayout.Y_AXIS));
            JLabel linkLabel=createLinkLabel(url);
            JPanel ratingPanel=RatingPanel(url,resultPanel,urls);
            JPanel emotionPanel=new JPanel();
            emotionPanel.setLayout(new BoxLayout(emotionPanel,BoxLayout.Y_AXIS));
            emotionPanel.add(happinessPanel());
            emotionPanel.add(angryPanel());

            JPanel linkAndRatingPanel=new JPanel(new BorderLayout());
            linkAndRatingPanel.add(linkLabel,BorderLayout.WEST);
            linkAndRatingPanel.add(ratingPanel,BorderLayout.CENTER);
            linkAndRatingPanel.add(emotionPanel,BorderLayout.EAST);
            linkPanel.add(linkAndRatingPanel);
            // Add comments heading
            JLabel Commentsheading=new JLabel("Comments:");
            Commentsheading.setAlignmentX(Component.LEFT_ALIGNMENT);
            linkPanel.add(Commentsheading);
            // Add user comments
            List<String> comments=commentsData.get(url);
            for(int i=0;i<comments.size();i++) 
            {
                String comment=comments.get(i);
                JPanel commentPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
                Integer[] counts=iconCounts.get(url);
                JLabel thumbsUp=new JLabel(loadIcon("C:\\Users\\anany\\OneDrive - Amrita vishwa vidyapeetham\\AMRITA\\SEMESTER-2\\PROJECTS\\JAVA_DSA\\thumbsupunfilled.jpeg",20,20));
                JLabel thumbsDown=new JLabel(loadIcon("C:\\Users\\anany\\OneDrive - Amrita vishwa vidyapeetham\\AMRITA\\SEMESTER-2\\PROJECTS\\JAVA_DSA\\thumbsdownunfilled.jpeg",20,20));
                JLabel CountthumbsUp=new JLabel(counts[0].toString());
                JLabel CountthumbsDown=new JLabel(counts[1].toString());
                thumbsUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
                thumbsDown.setCursor(new Cursor(Cursor.HAND_CURSOR));
                thumbsUp.addMouseListener(new MouseAdapter() 
                {
                    public void mouseClicked(MouseEvent e) 
                    {
                        Integer[] counts=iconCounts.get(url);
                        String lastVote=userVotes.get(url);
                        if (!"up".equals(lastVote)) 
                        {
                            counts[0]++;
                            CountthumbsUp.setText(counts[0].toString());
                            if ("down".equals(lastVote)) 
                            {
                                counts[1]--;
                                CountthumbsDown.setText(counts[1].toString());
                            }
                            userVotes.put(url,"up");
                        }
                    }
                });
                thumbsDown.addMouseListener(new MouseAdapter() 
                {
                    public void mouseClicked(MouseEvent e) {
                        Integer[] counts=iconCounts.get(url);
                        String lastVote=userVotes.get(url);
                        if (!"down".equals(lastVote)) 
                        {
                            counts[1]++;
                            CountthumbsDown.setText(counts[1].toString());
                            if ("up".equals(lastVote)) 
                            {
                                counts[0]--;
                                CountthumbsUp.setText(counts[0].toString());
                            }
                            userVotes.put(url,"down");
                        }
                    }
                });
                commentPanel.add(thumbsUp);
                commentPanel.add(thumbsUp);
                commentPanel.add(thumbsDown);
                commentPanel.add(CountthumbsDown);
                JLabel commentLabel=new JLabel(comment);
                commentPanel.add(commentLabel);
                if(comment.trim().isEmpty())
                {
                    JTextField commentField=new JTextField(20);
                    commentPanel.add(commentField);
                    JButton submitButton=new JButton("Submit");
                    int finalI=i;
                    submitButton.addActionListener(actionEvent -> 
                    {
                        String newComment=commentField.getText().trim();
                        if (!newComment.isEmpty()) 
                        {
                            comments.set(finalI,newComment);
                            JOptionPane.showMessageDialog(null,"Comment submitted!");
                        }
                    });
                    commentPanel.add(submitButton);
                }
                linkPanel.add(commentPanel);
            }
            resultPanel.add(linkPanel);
        }
    }
    private static JLabel createLinkLabel(String url) {
        JLabel link=new JLabel("<html><a href='"+url+"'>"+url+"</a></html>");
        link.setForeground(Color.BLUE.darker());
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                try 
                {
                    Desktop.getDesktop().browse(new java.net.URI(url));
                } 
                catch (Exception ex) 
                {
                    ex.printStackTrace();
                }
            }
        });
        return link;
    }
    private static JPanel RatingPanel(String url,JPanel resultPanel,String[] urls) {
        JPanel ratingPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        JLabel[] star=new JLabel[5];
        JLabel[] Countstar=new JLabel[5];
        int[] ratings=ratingsData.get(url);

        for (int i=0; i<star.length; i++) {
            JLabel starLabel=new JLabel("\u2606"); // Unicode for empty star
            starLabel.setFont(new Font("Serif",Font.PLAIN, 20));
            starLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            int finalI=i;
            starLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e) 
                {
                    setRating(star,finalI+1);
                }
                public void mouseExited(MouseEvent e) 
                {
                    setRating(star,-1); // Reset to previous rating on exit
                }
                public void mouseClicked(MouseEvent e) 
                {
                    ratings[finalI]++;
                    ratingsData.put(url,ratings); // Update the stored count
                    JOptionPane.showMessageDialog(null,"You rated this site "+(finalI + 1)+" stars.");
                    updateStarCounts(Countstar,ratings);
                    displayResults(urls,resultPanel); // Update the displayed results
                }
            });
            star[i]=starLabel;
            ratingPanel.add(starLabel);
            // Add label to display count of each rating
            JLabel starCountLabel=new JLabel(String.valueOf(ratings[i]));
            Countstar[i]=starCountLabel;
            ratingPanel.add(starCountLabel);
        }
        updateStarCounts(Countstar,ratings);
        return ratingPanel;
    }
    private static void setRating(JLabel[] star, int rating)
    {
        for (int i=0;i<star.length;i++) 
        {
            star[i].setText(i<rating?"\u2605":"\u2606"); // Unicode for filled and empty star
        }
    }
    private static void updateStarCounts(JLabel[] Countstar,int[] ratings)
    {
        for (int i=0;i<Countstar.length;i++)
            Countstar[i].setText(String.valueOf(ratings[i]));
    }
    private static double AverageRating(String url)
    {
        int[] ratings=ratingsData.get(url);
        int totalrating=Arrays.stream(ratings).sum();
        int ratingSum=0;
        for(int i = 0; i < ratings.length; i++) 
            ratingSum=ratingSum+ratings[i]*(i + 1);
        return (double)ratingSum/totalrating;
    }
    private static ImageIcon loadIcon(String path,int width,int height) 
    {
        try
        {
            BufferedImage originalImage=ImageIO.read(new File(path));
            Image scaledImage = originalImage.getScaledInstance(width,height,Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } 
        catch(IOException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    private static JPanel happinessPanel() 
    {
        JPanel happinessPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        JLabel happinessLabel=new JLabel("Happiness:");
        JProgressBar happinessBar=new JProgressBar(0,500); // Adjust maximum value
        happinessBar.setValue(250); // Example initial value (middle value)
        happinessBar.setStringPainted(true);
        happinessBar.setForeground(Color.GREEN); // Set color to green
        happinessPanel.add(happinessLabel);
        happinessPanel.add(happinessBar);
        happinessBar.addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e)
            {
                int width=happinessBar.getWidth();
                int partWidth=width/5;
                int clickedPart=e.getX()/partWidth;
                happinessBar.setValue(clickedPart*100); // Adjust value based on clicked part
            }
        });
        return happinessPanel;
    }
    private static JPanel angryPanel() 
    {
        JPanel angryPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        JLabel angryLabel=new JLabel("Angry:");
        JProgressBar angryBar=new JProgressBar(0,500); // Adjust maximum value
        angryBar.setValue(100); // Example initial value (one-fifth of the range)
        angryBar.setStringPainted(true);
        angryBar.setForeground(Color.RED); // Set color to red
        angryPanel.add(angryLabel);
        angryPanel.add(angryBar);
        angryBar.addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                int width=angryBar.getWidth();
                int partWidth=width/5;
                int clickedPart=e.getX()/partWidth;
                angryBar.setValue(clickedPart*100); // Adjust value based on clicked part
            }
        });
        return angryPanel;
    }
    static class UrlRating implements Comparable<UrlRating> 
    {
        String url;
        double averageRating;
        UrlRating(String url, double averageRating)
        {
            this.url=url;
            this.averageRating=averageRating;
        }
        public int compareTo(UrlRating other) 
        {
            return Double.compare(this.averageRating,other.averageRating);
        }
    }
}