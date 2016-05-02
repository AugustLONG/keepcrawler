package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 16/4/22.
 */
public class PeopleTimelinePageProcessor extends PatternProcessor
{
    private static Logger logger = LoggerFactory.getLogger("PeopleTimelinePageProcessor");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public PeopleTimelinePageProcessor(String pattern)
    {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page)
    {
        AbstractResponse<List<PeopleTimeline>> response = null;

        try
        {
            response = objectMapper.readValue(page.getRawText(), new TypeReference<AbstractResponse<List<PeopleTimeline>>>()
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
                //保存数据
                try
                {
                    JsonData jsonData = new JsonData();
                    jsonData.type = "timeline";
                    jsonData.data = new ArrayList<>();
                    JsonNode root = objectMapper.readTree(page.getRawText());
                    ArrayNode data = (ArrayNode) root.get("data");
                    for (JsonNode item : data)
                        jsonData.data.add(item.toString());
                    page.putField("jsonData", jsonData);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                //保存图片
                List<Image> images = new ArrayList<>();
                for (int i = 0; i < response.data.size(); i++)
                {
                    PeopleTimeline peopleTimeline = response.data.get(i);
                    try {
                        if (i == response.data.size() - 1) {
                            String nextPeopleTimelineUrl = String.format("http://api.gotokeep.com/v1.1/people/%s/timeline/?lastId=%s", peopleTimeline.author, peopleTimeline.id);
                            page.addTargetRequest(new Request(nextPeopleTimelineUrl).setPriority(3));
                            logger.info("提交下载[nextPeopleTimelineUrl]:" + nextPeopleTimelineUrl);
                        }
                        Image image = new Image();
                        image.remoteUrl = peopleTimeline.photo;
                        if (!StringUtils.isEmpty(image.remoteUrl)) {
                            image.localPath = peopleTimeline.photo.substring(peopleTimeline.photo.lastIndexOf("gotokeep.com")).replace("/", File.separator);
                            images.add(image);
                        }
                    } catch (Exception ex) {
                        logger.warn(peopleTimeline.photo);
                        ex.printStackTrace();
                    }
                }
                page.putField("images", images);
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
