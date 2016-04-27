package me.josephzhu.keepcrawler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 16/4/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class People
{
    public String _id;
    public String username;
    public String avatar;
    public Long followers;
    public Long followings;
}
