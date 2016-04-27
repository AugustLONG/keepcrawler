package me.josephzhu.keepcrawler;

/**
 * Created by joseph on 16/4/26.
 */
public class Consts
{
    public String ImageLocation;
    public String MongodbHost;
    public String RedisHost;

    public Consts(String imageLocation, String mongodbHost, String redisHost)
    {
        ImageLocation = imageLocation;
        MongodbHost = mongodbHost;
        RedisHost = redisHost;
    }

    private static Consts local = new Consts("/Users/joseph/Documents/crawlerimages/", "localhost", "localhost");
    private static Consts remote = new Consts("/ssd/data/images/", "localhost", "localhost");

    public static Consts current()
    {
        return remote;
    }
}
