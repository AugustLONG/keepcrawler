package me.josephzhu.keepcrawler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by joseph on 16/4/22.
 */
public class MongodbPipeline implements Pipeline
{
    private static final MongoClient mongoClient = new MongoClient(new MongoClientURI(Consts.current().MongodbHost));
    private static final MongoDatabase database = mongoClient.getDatabase("keep");
    private static Logger logger = LoggerFactory.getLogger("MongodbPipeline");

    static {
        new Thread(() ->
        {
            while (true) {

                try {
                    MongoIterable<String> collections = database.listCollectionNames();
                    for (String s : collections) {
                        long count = database.getCollection(s).count();
                        if (count > 0)
                            logger.info(String.format("%s已保存数据:%d", s, count));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void process(ResultItems resultItems, Task task)
    {
        JsonData jsonData = resultItems.get("jsonData");
        if (jsonData != null && jsonData.data != null)
        {
            try
            {
                MongoCollection collection = database.getCollection(jsonData.type);
                for (String s : jsonData.data)
                {
                    collection.insertOne(Document.parse(s));
                }
                logger.info(String.format("向%s写入了%d条数据", jsonData.type, jsonData.data.size()));
            }
            catch (Exception ex)
            {
                logger.warn(String.format("向%s写入数据失败,原因:%s", jsonData.type, ex.getMessage()));
            }
        }
    }
}
