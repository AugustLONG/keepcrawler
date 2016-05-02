package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PeoplePageProcessor extends PatternProcessor
{
    private static Logger logger = LoggerFactory.getLogger("PeoplePageProcessor");
    private static ObjectMapper objectMapper = new ObjectMapper();

    public PeoplePageProcessor(String pattern)
    {
        super(pattern);
    }

    @Override
    public MatchOther processPage(Page page)
    {
        AbstractResponse<People> response = null;

        try
        {
            response = objectMapper.readValue(page.getRawText(), new TypeReference<AbstractResponse<People>>()
            {
            });
        }
        catch (IOException ex)
        {
            logger.warn(String.format("数据%s解析失败,原因:%s", page.getUrl(), ex.getMessage()));
        }

        if (response != null && response.ok != null && response.ok)
        {
            //保存数据
            try
            {
                JsonData jsonData = new JsonData();
                jsonData.type = "people";
                jsonData.data = new ArrayList<>();
                JsonNode root = objectMapper.readTree(page.getRawText());
                JsonNode data = root.get("data");
                jsonData.data.add(data.toString());
                page.putField("jsonData", jsonData);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            //保存头像
            List<Image> images = new ArrayList<>();
            Image image = new Image();
            image.remoteUrl = response.data.avatar;
            if (!StringUtils.isEmpty(response.data.avatar) && response.data.avatar.lastIndexOf("gotokeep.com") > 0)
                image.localPath = response.data.avatar.substring(response.data.avatar.lastIndexOf("gotokeep.com")).replace("/", File.separator);

            images.add(image);
            page.putField("images", images);

            //请求时间线数据
            String peopleTimelineUrl =String.format("http://api.gotokeep.com/v1.1/people/%s/timeline/", response.data._id);
            logger.info("提交下载[peopleTimelineUrl]:" + peopleTimelineUrl);
            page.addTargetRequest(new Request(peopleTimelineUrl).setPriority(3));

            if (response.data.followers>0)
            {
                //请求粉丝数据
                String followersUrl = String.format("http://api.gotokeep.com/v1.1/people/%s/followers/", response.data._id);
                logger.info("提交下载[followersUrl]:" + followersUrl);
                page.addTargetRequest(new Request(followersUrl).setPriority(2));
            }

            if (response.data.followings>0)
            {
                //请求关注数据
                String followingsUrl = String.format("http://api.gotokeep.com/v1.1/people/%s/followings/", response.data._id);
                logger.info("提交下载[followingsUrl]:" + followingsUrl);
                page.addTargetRequest(new Request(followingsUrl).setPriority(2));
            }
        }

        return MatchOther.NO;
    }

    @Override
    public MatchOther processResult(ResultItems resultItems, Task task)
    {
        return MatchOther.NO;
    }
}
