package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.handler.PatternProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 16/4/22.
 */
public class RelationshipPageProcessor extends PatternProcessor
{
    private static Logger logger = LoggerFactory.getLogger("RelationshipPageProcessor");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public RelationshipPageProcessor(String pattern)
    {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page)
    {
        AbstractResponse<Relationship> response = null;

        try
        {
            response = objectMapper.readValue(page.getRawText(), new TypeReference<AbstractResponse<Relationship>>()
            {
            });
        }
        catch (IOException ex)
        {
            logger.warn(String.format("数据%s解析失败,原因:%s", page.getUrl(), ex.getMessage()));
        }

        if (response != null && response.ok != null && response.ok)
        {
            if (response.data != null && response.data.users!= null && response.data.users.size()>0)
            {
                for (int i = 0; i < response.data.users.size(); i++)
                {
                    People relationship = response.data.users.get(i);
                    if (i == response.data.users.size() - 1)
                    {
                        String url = page.getUrl().toString();
                        if (url.indexOf('?')>0)
                        {
                            url = url.substring(0, url.indexOf('?'));
                        }
                        String nextRelationshipUrl = String.format("%s?lastId=%s", url , relationship._id);
                        page.addTargetRequest(nextRelationshipUrl);
                        logger.info("提交下载[nextRelationshipUrl]:" + nextRelationshipUrl);
                    }
                    if (Redis.shouldIssuePeopleRequest(relationship._id))
                    {
                        String relationshipPeopleUrl = String.format("http://api.gotokeep.com/v1.1/people/%s/", relationship._id);
                        logger.info("提交下载[relationshipPeopleUrl]:" + relationshipPeopleUrl);
                        page.addTargetRequest(relationshipPeopleUrl);
                    }
                }
            }
        }
        else
        {
            logger.warn(String.format("数据%s不正确,原因:%s", page.getUrl(), response.errorCode));
        }

        return MatchOther.NO;
    }

    @Override
    public MatchOther processResult(ResultItems resultItems, Task task)
    {
        return MatchOther.NO;
    }
}
