package bgu.spl.net.api.bidi;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * class that represents the client and keeps all of his details
 */
public class User {
    private AtomicInteger id;
    private String username;
    private String password;
    private boolean isLoggedIn;
    private AtomicInteger numOfPosts;
    private AtomicInteger numOfFollowers;
    private AtomicInteger numOfFollowings;
    private LinkedBlockingQueue<String> followers;
    private LinkedBlockingQueue<String> followings;

    /**
     * constructor
     *
     * @param username String username of the user
     * @param password String password of the user
     */
    public User(String username, String password){
        this.id = new AtomicInteger(-1);
        this.password = password;
        this.username = username;
        isLoggedIn = false;
        numOfPosts = new AtomicInteger(0);
        numOfFollowers = new AtomicInteger(0);
        numOfFollowings = new AtomicInteger(0);
        followers = new LinkedBlockingQueue<>();
        followings = new LinkedBlockingQueue<>();
    }

    public int getId() { return id.get(); }

    public void setId(int id) { this.id.getAndSet(id); }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void login(){
        isLoggedIn = true;
    }

    public void logout(){
        isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public LinkedBlockingQueue<String> getFollowers(){
        return followers;
    }

    public int getNumOfPosts() {
        return numOfPosts.get();
    }

    public void addPost(){
        numOfPosts.incrementAndGet();
    }

    public int getNumOfFollowers() {
        return numOfFollowers.get();
    }

    public void addNumOfFollowers() {
        numOfFollowers.incrementAndGet();
    }

    public void reduceNumOfFollowers() {
        numOfFollowers.decrementAndGet();
    }

    public int getNumOfFollowings() {
        return numOfFollowings.get();
    }

    public void addNumOfFollowings() {
        numOfFollowings.incrementAndGet();
    }

    public void reduceNumOfFollowings() {
        numOfFollowings.decrementAndGet();
    }

    public LinkedBlockingQueue<String> getFollowings() {
        return followings;
    }

}
