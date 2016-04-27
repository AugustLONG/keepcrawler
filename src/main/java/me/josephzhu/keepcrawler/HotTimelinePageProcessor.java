package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;

import java.io.IOException;
import java.util.List;

/**
 * Created by joseph on 16/4/22.
 */
public class HotTimelinePageProcessor extends PatternProcessor
{
    private static Logger logger = LoggerFactory.getLogger("TimelineHotPageProcessor");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public HotTimelinePageProcessor(String pattern)
    {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page)
    {
        AbstractResponse<List<HotTimeline>> response = null;

        try
        {
            response = objectMapper.readValue(page.getRawText(), new TypeReference<AbstractResponse<List<HotTimeline>>>()
            {
            });
        }
        catch (IOException ex)
        {
            logger.warn(String.format("数据%s解析失败,原因:%s", page.getUrl(), ex.getMessage()));
        }

        if (response != null && response.ok != null && response.ok)
        {
            if (response.data != null && response.data.size() > 0)
            {
                for (int i = 0; i < response.data.size(); i++)
                {
                    HotTimeline hotTimeline = response.data.get(i);
                    if (i == response.data.size() - 1)
                    {
                        String nextHotTimelineUrl = String.format("http://api.gotokeep.com/v1.1/timeline/hot?lastId=%s", hotTimeline.id);
                        page.addTargetRequest(nextHotTimelineUrl);
                        logger.info("提交下载[nextHotTimelineUrl]:" + nextHotTimelineUrl);
                    }

                    if (Redis.shouldIssuePeopleRequest(hotTimeline.author.id))
                    {
                        String peopleUrl = String.format("http://api.gotokeep.com/v1.1/people/%s/", hotTimeline.author.id);
                        logger.info("提交下载[peopleUrl]:" + peopleUrl);
                        page.addTargetRequest(peopleUrl);
                    }
                }
            }
            else
            {
                logger.info("结束抓取数据:" + page.getUrl());
            }
        }
        else
        {
            logger.warn(String.format("数据%s不正确,原因:%s", page.getUrl(), response.errorCode));
            logger.info(page.getRawText());
        }
        return MatchOther.NO;
    }

    @Override
    public MatchOther processResult(ResultItems resultItems, Task task)
    {
        return MatchOther.NO;
    }
}
