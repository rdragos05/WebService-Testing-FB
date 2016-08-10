
import com.restfb.*;
import com.restfb.types.*;
import javafx.geometry.Pos;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.*;

/**
 * Created by draicu on 8/10/2016.
 */
public class FacebookAPITest {

    @Test


    public void FacebookTest() {
        String ACCESS_TOKEN = "EAACEdEose0cBADkDkFEbN5yypP9G1BG9IehyPB3YTzvbPkcCUZCMjx7MwIP8FhX4zvvru7o2y7ZB6FZAtRK7wJNmSLZCHRZCgbaCdU1R0vvvVDArvKo5Clc4hrPHDGgJmBCjZA8gnZC544bT6BRZCUNLwMau4qElJPnngKZBrVaIuuNrmLuxZCevHs";
        FacebookClient facebookClient= new DefaultFacebookClient(ACCESS_TOKEN);
        FacebookClient publicOnlyFacebookClient = new DefaultFacebookClient();

        //user details (GET)

        User user = facebookClient.fetchObject("me", User.class);
        System.out.println("My profile link: " + user.getLink());
        System.out.println("My user id: " + user.getId());
        System.out.println("My user name: " + user.getName());
        System.out.println("My birthday: " + user.getBirthday());
        System.out.println("My bio: " + user.getBio());
        assertEquals(user.getName(), "Test-test Test-test");
        assertNotEquals(user.getId(),null);
        assertTrue(user.getLink().contains("www.facebook.com"));




        //feed posts details (GET)

        Connection<Post> personalFeed = facebookClient.fetchConnection("me/feed", Post.class);
        System.out.println("First item in my feed: " + personalFeed.getData().get(0)); //for second item change 0 with 1 etc.
        for (List<Post> myFeedConnectionPage : personalFeed)
            for ( Post post : myFeedConnectionPage)
                System.out.println("Post: " + post);




        //searching (POST) --NOT WORKING ANYMORE

        //Connection<Post> publicSearch = facebookClient.fetchConnection("search", Post.class, Parameter.with("q", "silicon valley"));
        //System.out.println("Public search: " + publicSearch.getData().get(0).getMessage());





        //posting a text message (POST)

        Long timestamp = new Date().getTime(); //unique input due to error message when trying to repost the same text
        Long textToBePosted = timestamp; //saving the timestamp to another value (otherwise it will change)

        FacebookType publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", textToBePosted));
        System.out.println("Published message ID: " + publishMessageResponse.getId());

        String postId = publishMessageResponse.getId();

        Post post = facebookClient.fetchObject(postId, Post.class, Parameter.with("fields", "id"));
        String actualLastPostId = post.getId();
        System.out.println("Last post ID (verification): " + actualLastPostId);

        assertTrue(actualLastPostId.equals(postId));




        //deleting a post (DELETE)

        Boolean isItemRemoved = facebookClient.deleteObject(postId);
        System.out.println("Was the object deleted?" + isItemRemoved.toString());

        //as a verification, we will try deleting the same item again, if it returns false, it means it was deleted at the first try

        isItemRemoved = facebookClient.deleteObject(postId);
        assertFalse(isItemRemoved); //test passes when the item is deleted from the first try





        //getting friends list (GET)

        Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
        for(User user : myFriends.getData()){
            System.out.println(user.getName());
        }
        String firstFriendID = myFriends.getData().get(0).getId(); //get first friend id


        //publishing a checkin (POST)
        Map<String, String> coord = new HashMap<String, String>();
        coord.put("latitude", "44.429351");
        coord.put("longitude", "26.053076");

        FacebookType postCheckinResponse = facebookClient.publish("me/checkins", FacebookType.class, Parameter.with("message", "My current location!"), Parameter.with("coordinates", coord), Parameter.with("place", 1234));
        System.out.println("Published checkin ID: " + postCheckinResponse.getId());

        String checkinId = postCheckinResponse.getId();

        Checkin checkin = facebookClient.fetchObject(checkinId, Checkin.class, Parameter.with("fields", "id"));
        String actualCheckinId = checkin.getId();
        System.out.println("Last checkin ID (verification): " + actualCheckinId);

        assertEquals(actualCheckinId,checkinId);


        //publishing a photo (POST)

        FacebookType postPhotoResponse = facebookClient.publish("me/photos", FacebookType.class, BinaryAttachment.with("city.jpg", getClass().getResourceAsStream("c:\Users\draicu\Desktop\city.jpg")), Parameter.with("message", "I was here!"));
        System.out.println("Published photo ID: " + postPhotoResponse.getId());

        String photoId = postPhotoResponse.getId();

        Photo photo = facebookClient.fetchObject(photoId, Photo.class, Parameter.with("fields","id"));
        String actualPhotoId = photo.getId();
        assertEquals(actualPhotoId,photoId);


        //publish an event (POST)

        Date tomorrow = new Date(currentTimeMillis() + 1000L * 60L * 60L * 24L);
        Date twoDaysFromNow = new Date(currentTimeMillis() + 1000L * 60L * 60L * 48L);

        FacebookType postEventResponse = facebookClient.publish("me/events", FacebookType.class, Parameter.with("name", "Hangout"), Parameter.with("start_time", tomorrow), Parameter.with("end_time", twoDaysFromNow));

        System.out.println("Published event ID:" + postEventResponse.getId());

        String eventID = postEventResponse.getId();

        Event event = facebookClient.fetchObject(eventID, Event.class, Parameter.with("fields", "id"));
        String actualEventId = event.getId();
        assertEquals(actualEventId, eventID);



    }


}
