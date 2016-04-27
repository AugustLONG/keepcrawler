package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 16/4/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleTimeline
{
    public String id;
    public String photo;
    public String author;
}
