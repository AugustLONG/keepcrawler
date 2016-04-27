package me.josephzhu.keepcrawler;

import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by joseph on 16/4/26.
 */
public class Redis
{
    private static Jedis jedis = new Jedis(Consts.current().RedisHost);
    private static int daySeconds = 3600 * 24;

    public static boolean shouldIssuePeopleRequest(String peopleId)
    {
        try
        {
            String key = String.format("people:%s:%s", peopleId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            if (jedis.get(key) != null)
            {
                return false;
            }
            else
            {
                jedis.setex(key, daySeconds, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return true;
        }
    }
}
