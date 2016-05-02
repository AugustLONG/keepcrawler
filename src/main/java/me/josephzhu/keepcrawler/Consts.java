package me.josephzhu.keepcrawler;

/**
 * Created by joseph on 16/4/26.
 */
public class Consts
{
    private static Consts local = new Consts("/Users/joseph/Documents/crawlerimages/", "localhost", "localhost");
    private static Consts remote = new Consts("/home/data/", "mongodb://192.168.1.201:27017,192.168.1.202:27017,192.168.1.203:27017/?replicaSet=mongodbcluster&maxPoolSize=200", "192.168.1.203");
    public String ImageLocation;
    public String MongodbHost;
    public String RedisHost;
    public Consts(String imageLocation, String mongodbHost, String redisHost)
    {
        ImageLocation = imageLocation;
        MongodbHost = mongodbHost;
        RedisHost = redisHost;
    }

    public static Consts current()
    {
        return remote;
    }
}
