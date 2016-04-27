package me.josephzhu.keepcrawler;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.handler.CompositePageProcessor;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import javax.annotation.processing.Processor;

/**
 * Created by joseph on 16/4/22.
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        Site site = Site.me()
                .setUserAgent("Dalvik/2.1.0 (Linux; U; Android 5.1; MX5 Build/LMY47I)")
                .addHeader("X-X", "a9fafb7f5c7604e808f159cb6ae95be8397ea230")
                .addHeader("X-KEEP-FROM", "android")
                .addHeader("X-KEEP-TIMEZONE", "Asia/Shanghai")
                .addHeader("X-DEVICE", "Meizu-MX5")
                .addHeader("X-KEEP-VERSION", "3.1.0")
                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI1NmFkMGFiZDM1NTg1YzcwMzhkYjBjYWUiLCJ1c2VybmFtZSI6ImZnYmhnZmRzcyIsImF2YXRhciI6IiIsImlhdCI6MTQ2MTMzMjY0NSwiZXhwIjoxNDYzOTI0NjQ1LCJpc3MiOiJodHRwOi8vd3d3LmdvdG9rZWVwLmNvbS8ifQ.wdwgYhIod4rtmIKvyuKY3UDze8o0ZJD4F5a3snYesFw")
                .addHeader("Connection", "Keep-Alive")
                .setSleepTime(100)
                .setRetryTimes(5)
                .setRetrySleepTime(10000);

        CompositePageProcessor processor = new CompositePageProcessor(site);
        processor.setSubPageProcessors(
                new HotTimelinePageProcessor(".*/timeline/hot.*"),
                new PeopleTimelinePageProcessor(".*/people/.*/timeline/.*"),
                new RelationshipPageProcessor(".*/people/.*/follow.*/.*"),
                new PeoplePageProcessor(".*/people/.*"));

        Spider spider = Spider.create(processor)
                .addUrl("http://api.gotokeep.com/v1.1/timeline/hot")
                .addPipeline(new MongodbPipeline())
//                .addPipeline(new ImageDownloadPipeline())
                .setScheduler(new QueueScheduler())
                .thread(4);

        SpiderMonitor.instance().register(spider);
        spider.start();
    }
}
